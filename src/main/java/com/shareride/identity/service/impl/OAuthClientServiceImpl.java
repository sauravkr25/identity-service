package com.shareride.identity.service.impl;

import com.shareride.identity.dao.OAuthClientRepository;
import com.shareride.identity.entity.OAuthClient;
import com.shareride.identity.enums.OAuthClientStatus;
import com.shareride.identity.exception.ApplicationException;
import com.shareride.identity.exception.ErrorCodes;
import com.shareride.identity.service.OAuthClientService;
import com.shareride.identity.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthClientServiceImpl implements OAuthClientService {

    private final OAuthClientRepository oAuthClientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public String generateServiceToken(String clientId, String clientSecret) {

        OAuthClient oAuthClient = oAuthClientRepository.findByClientId(clientId)
                .orElseThrow(() -> ApplicationException.of(ErrorCodes.INVALID_CLIENT_CREDENTIALS));

        if (oAuthClient.getStatus() != OAuthClientStatus.ACTIVE) {
            throw ApplicationException.of(ErrorCodes.CLIENT_INACTIVE);
        }

        if (!passwordEncoder.matches(clientSecret, oAuthClient.getClientSecretHash())) {
            throw ApplicationException.of(ErrorCodes.INVALID_CLIENT_CREDENTIALS);
        }

        return jwtService.generateServiceToken(oAuthClient);
    }
}
