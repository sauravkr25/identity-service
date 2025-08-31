package com.shareride.identity.service.impl;

import com.shareride.identity.dao.RoleRepository;
import com.shareride.identity.dao.UserRepository;
import com.shareride.identity.domain.UserDomain;
import com.shareride.identity.entity.User;
import com.shareride.identity.service.jwt.JwtService;
import com.shareride.identity.service.AuthService;
import com.shareride.identity.utils.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.shareride.identity.enums.UserStatus.PENDING_VERIFICATION;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final String userRole;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository,
                           PasswordEncoder passwordEncoder, RoleRepository roleRepository, @Value(Constants.PropertyKeys.ROLE_USER) String userRole) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRole = userRole;
    }

    @Override
    public UserDomain register(UserDomain userDomain) {
       User user = userDomain.toUser();
       user.setStatus(PENDING_VERIFICATION);
       user.setPassword(passwordEncoder.encode(userDomain.getPassword()));
       user.setRoles(Set.of(roleRepository.findByName(userRole).orElseThrow(() -> new RuntimeException("Role not found"))));
       user = userRepository.save(user);

       userDomain.updateFrom(user);
       return userDomain;
    }

    @Override
    public UserDomain login(UserDomain userDomain) {
        // 1. Spring Security authenticates the user.
        // This returns an Authentication object if successful, or throws an exception if not.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDomain.getEmail(), userDomain.getPassword())
        );

        // 2. Set the authenticated user in the security context.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Get the UserDetails object (the principal) from the Authentication object.
        User user = (User) authentication.getPrincipal();

        // 4. Pass the UserDetails object to your JWT provider to generate the token.
        // The provider can now get the username and roles directly from userDetails.
        String jwt = jwtService.generateToken(user);

        userDomain.setToken(jwt);
        return userDomain;
    }

    @Override
    public boolean verifyEmail(String token) {
        return false;
    }

}
