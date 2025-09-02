package com.shareride.identity.api.controller;

import com.shareride.identity.api.response.UserResponse;
import com.shareride.identity.domain.UserDomain;
import com.shareride.identity.service.UserService;
import com.shareride.identity.utils.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.shareride.identity.utils.Constants.Routes.*;

@RestController
@RequestMapping(API_V1+USERS)
public class UserController {

    private final UserService userService;
    private final String userRole;

    public UserController(UserService userService, @Value(Constants.PropertyKeys.ROLE_USER) String userRole) {
        this.userService = userService;
        this.userRole = userRole;
    }

    @GetMapping(ME)
    @PreAuthorize("hasAnyAuthority(@roleProps.user)")
    public ResponseEntity<UserResponse> getMyProfile(Authentication authentication) {
        // By the time this code runs, the user is already authenticated.
        // Spring injects the 'Authentication' object from the security context.

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userEmail = userDetails.getUsername();

        UserDomain userDomain = UserDomain.builder()
                .email(userEmail)
                .build();

        userDomain = userService.getMyProfile(userDomain);

        return ResponseEntity.ok(UserResponse.from(userDomain));
    }

}
