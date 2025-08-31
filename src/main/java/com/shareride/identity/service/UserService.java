package com.shareride.identity.service;

import com.shareride.identity.domain.UserDomain;

public interface UserService {

    UserDomain getMyProfile(UserDomain userDomain);
}
