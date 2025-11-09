package com.shareride.identity.service.impl;

import com.shareride.identity.config.properties.AppProperties;
import com.shareride.identity.config.properties.RoleProperties;
import com.shareride.identity.dao.RoleRepository;
import com.shareride.identity.dao.UserRepository;
import com.shareride.identity.dao.VerificationTokenRepository;
import com.shareride.identity.domain.EmailMessage;
import com.shareride.identity.domain.UserDomain;
import com.shareride.identity.entity.Role;
import com.shareride.identity.entity.User;
import com.shareride.identity.entity.VerificationToken;
import com.shareride.identity.enums.UserStatus;
import com.shareride.identity.exception.ApplicationException;
import com.shareride.identity.service.EmailService;
import com.shareride.identity.service.jwt.JwtService;
import com.shareride.identity.service.AuthService;
import com.shareride.identity.utils.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.shareride.identity.enums.UserStatus.PENDING_VERIFICATION;
import static com.shareride.identity.exception.ErrorCodes.BAD_REQUEST;
import static com.shareride.identity.exception.ErrorCodes.INVALID_VERIFICATION_TOKEN;
import static com.shareride.identity.exception.ErrorCodes.ROLE_NOT_FOUND;
import static com.shareride.identity.utils.Constants.CAUSE;
import static com.shareride.identity.utils.Constants.Routes.API_V1;
import static com.shareride.identity.utils.Constants.Routes.AUTH;
import static com.shareride.identity.utils.Constants.Routes.VERIFY_EMAIL;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final VerificationTokenRepository tokenRepository;
    private final RoleProperties role;
    private final AppProperties appProperties;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtService jwtService, EmailService emailService,
                           UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository,
                           VerificationTokenRepository tokenRepository, RoleProperties roleProperties,
                           AppProperties appProperties) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.tokenRepository = tokenRepository;
        this.role = roleProperties;
        this.appProperties = appProperties;
    }

    @Override
    public UserDomain register(UserDomain userDomain) {
        if(userRepository.existsByEmail(userDomain.getEmail())) {
            throw ApplicationException.of(BAD_REQUEST, Map.of(CAUSE, "Email is already in use"));
        }

       User user = userDomain.toUser();
       user.setStatus(PENDING_VERIFICATION);
       user.setPassword(passwordEncoder.encode(userDomain.getPassword()));
       user.setRoles(getUserRoles());
       user = userRepository.save(user);

       userDomain.updateFrom(user);
       return userDomain;
    }

    @Override
    public UserDomain login(UserDomain userDomain) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDomain.getEmail(), userDomain.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        String jwt = jwtService.generateUserToken(user);

        userDomain.setToken(jwt);
        return userDomain;
    }

    @Override
    public void sendVerificationEmail(String email) {

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            logger.warn("Verification email requested for a non-existent email: {}", email);
            return;
        }
        User user = userOptional.get();
        String token = RandomUtil.generateRandomBase64String(appProperties.getVerificationToken().getBytes());
        long expirationInMs = appProperties.getVerificationToken().getExpiryInMillis();

        tokenRepository.save(createVerificationTokenObject(user, token, expirationInMs));
        EmailMessage emailMessage = createVerificationEmail(user, token, expirationInMs, appProperties.getBaseUrl());
        emailService.sendEmail(emailMessage);

        logger.info("Queued verification email for {}", email);
    }

    @Override
    public boolean verifyEmail(String token) {
        VerificationToken tokenFromDB = tokenRepository.findByToken(token).orElseThrow(() -> ApplicationException.of(INVALID_VERIFICATION_TOKEN));

        if (tokenFromDB.getExpiryTimestamp().isBefore(Instant.now())) {
            tokenRepository.delete(tokenFromDB);
            throw ApplicationException.of(INVALID_VERIFICATION_TOKEN);
        }

        User user = tokenFromDB.getUser();
        user.setStatus(UserStatus.ACTIVE);
        if (!isPublicDomain(user.getEmail())) user.setCorporateVerified(true);
        userRepository.save(user);

        // CRUCIAL: Delete the token so it cannot be used again.
        tokenRepository.delete(tokenFromDB);
        return true;
    }

    private VerificationToken createVerificationTokenObject(User user, String token, long expirationInMs) {
        Optional<VerificationToken> existingToken = tokenRepository.findByUser(user);

        VerificationToken newToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryTimestamp(Instant.now().plus(expirationInMs, ChronoUnit.MILLIS))
                .build();

        existingToken.ifPresent(verificationToken -> newToken.setId(verificationToken.getId()));
        return newToken;
    }

    private EmailMessage createVerificationEmail(User user, String token, long expirationInMs, String baseUrl) {
        String verificationLink = baseUrl + API_V1 + AUTH + VERIFY_EMAIL + "?token=" + token;

        String subject = "Verify your email - ShareRide";
        String body = "Hi " + user.getFullName() + ",\n\n" +
                "Please verify your email by clicking the link below:\n" +
                verificationLink + "\n\n" +
                "This link will expire in "+ expirationInMs/(1000*60) +" minute(s).\n\n" +
                "Thanks,\nShareRide Team";

        return EmailMessage.builder()
                .to(user.getEmail())
                .subject(subject)
                .body(body)
                .build();
    }

    private Set<Role> getUserRoles() {
        return Set.of(
                roleRepository.findByName(role.getUser()).orElseThrow(() -> ApplicationException.of(ROLE_NOT_FOUND))
        );
    }

    private boolean isPublicDomain(String email) {
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();
        return appProperties.getEmail().getPublicDomains().contains(domain);
    }

}
