package com.shareride.identity.service;

public interface OAuthClientService {

    String generateServiceToken(String clientId, String clientSecret);
}
