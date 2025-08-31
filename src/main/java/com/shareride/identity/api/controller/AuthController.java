package com.shareride.identity.api.controller;

import com.shareride.identity.api.request.LoginRequest;
import com.shareride.identity.api.response.JwtResponse;
import com.shareride.identity.domain.UserDomain;
import com.shareride.identity.api.request.RegisterRequest;
import com.shareride.identity.api.response.RegisterResponse;
import com.shareride.identity.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.shareride.identity.utils.Constants.Routes.*;
import static com.shareride.identity.utils.Constants.TOKEN;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(API_V1+AUTH)
public class AuthController {

    private final AuthService authService;

    public AuthController (AuthService authService) {
        this.authService = authService;
    }


    @PostMapping(REGISTER)
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody @Valid RegisterRequest request) {

        UserDomain userDomain = UserDomain.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(request.getPassword())
                .phone(request.getPhone())
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

        return  ResponseEntity.status(OK).body(response);
    }

    @GetMapping(VERIFY_EMAIL)
    public ResponseEntity<Void> verifyEmail(@RequestParam(TOKEN) String token) {
        boolean isVerified = authService.verifyEmail(token);
        return isVerified ? ResponseEntity.status(OK).build() : ResponseEntity.status(BAD_REQUEST).build() ;
    }



}
