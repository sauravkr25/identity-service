package com.shareride.identity.api.controller;

import com.shareride.identity.api.request.EmailRequest;
import com.shareride.identity.api.request.LoginRequest;
import com.shareride.identity.api.response.JwtResponse;
import com.shareride.identity.api.response.SendAndVerifyEmailResponse;
import com.shareride.identity.domain.UserDomain;
import com.shareride.identity.api.request.RegisterRequest;
import com.shareride.identity.api.response.RegisterResponse;
import com.shareride.identity.service.AuthService;
import com.shareride.identity.ratelimiter.RateLimiterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.shareride.identity.utils.Constants.Routes.*;
import static com.shareride.identity.utils.Constants.SUCCESS;
import static com.shareride.identity.utils.Constants.TOKEN;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(API_V1 + AUTH)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService, RateLimiterService rateLimiterService) {
        this.authService = authService;
    }

    @PostMapping(REGISTER)
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody @Valid RegisterRequest request) {

        UserDomain userDomain = UserDomain.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(request.getPassword())
                .phone(request.getPhoneNumber())
                .build();

        userDomain = authService.register(userDomain);
        RegisterResponse response = RegisterResponse.from(userDomain);
        return ResponseEntity.status(CREATED).body(response);
    }

    @PostMapping(LOGIN)
    public ResponseEntity<JwtResponse> loginUser(@RequestBody @Valid LoginRequest request) {

        UserDomain userDomain = UserDomain.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        userDomain = authService.login(userDomain);
        JwtResponse response = JwtResponse.from(userDomain);
        return ResponseEntity.status(OK).body(response);
    }

    @PostMapping(SEND_VERIFICATION_EMAIL)
    public ResponseEntity<SendAndVerifyEmailResponse> sendVerificationEmail(@RequestBody @Valid EmailRequest request) {

        authService.sendVerificationEmail(request.getEmail());
        SendAndVerifyEmailResponse responseBody = SendAndVerifyEmailResponse.builder()
                .status(SUCCESS)
                .message("Verification email sent successfully to " + request.getEmail())
                .build();
        return ResponseEntity.status(OK).body(responseBody);
    }

    @GetMapping(VERIFY_EMAIL)
    public ResponseEntity<SendAndVerifyEmailResponse> verifyEmail(@RequestParam(TOKEN) String token) {

        boolean isVerified = authService.verifyEmail(token);
        return isVerified ? ResponseEntity.ok(
                SendAndVerifyEmailResponse.builder()
                        .status(SUCCESS)
                        .message("Email verified successfully")
                        .build()
        ) : ResponseEntity.badRequest().build();
    }

}
