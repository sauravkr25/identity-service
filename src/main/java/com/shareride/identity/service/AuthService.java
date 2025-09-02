package com.shareride.identity.service;

import com.shareride.identity.domain.UserDomain;

public interface AuthService {

    UserDomain register(UserDomain userDomain);

    UserDomain login(UserDomain userDomain);

    void sendVerificationEmail(String email);

    boolean verifyEmail(String token);
}
