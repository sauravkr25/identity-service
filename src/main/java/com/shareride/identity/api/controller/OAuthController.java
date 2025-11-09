package com.shareride.identity.api.controller;

import com.shareride.identity.api.response.JwtResponse;
import com.shareride.identity.exception.ApplicationException;
import com.shareride.identity.exception.ErrorCodes;
import com.shareride.identity.service.OAuthClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.shareride.identity.utils.Constants.CLIENT_CREDENTIALS_GRANT_TYPE;
import static com.shareride.identity.utils.Constants.CLIENT_ID;
import static com.shareride.identity.utils.Constants.CLIENT_SECRET;
import static com.shareride.identity.utils.Constants.GRANT_TYPE;
import static com.shareride.identity.utils.Constants.Routes.API_V1;
import static com.shareride.identity.utils.Constants.Routes.OAUTH;
import static com.shareride.identity.utils.Constants.TOKEN;

@RequiredArgsConstructor
@RestController
@RequestMapping(API_V1 + OAUTH)
public class OAuthController {

    private final OAuthClientService oAuthClientService;

    @PostMapping(value = TOKEN)
    public ResponseEntity<JwtResponse> getServiceToken(
            @RequestParam(GRANT_TYPE) String grantType,
            @RequestParam(CLIENT_ID) String clientId,
            @RequestParam(CLIENT_SECRET) String clientSecret
    ) {
        if (!CLIENT_CREDENTIALS_GRANT_TYPE.equals(grantType)) {
            throw ApplicationException.of(ErrorCodes.BAD_REQUEST);
        }

        String jwt = oAuthClientService.generateServiceToken(clientId, clientSecret);
        JwtResponse response = JwtResponse.builder()
                .token(jwt)
                .build();
        return ResponseEntity.ok(response);

    }

}
