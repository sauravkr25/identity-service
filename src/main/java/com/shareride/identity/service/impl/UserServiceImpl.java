package com.shareride.identity.service.impl;

import com.shareride.identity.dao.UserRepository;
import com.shareride.identity.domain.UserDomain;
import com.shareride.identity.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDomain getMyProfile(UserDomain userDomain) {

        userRepository.findById(userDomain.getUserId())
                .ifPresent(userDomain::updateFrom);

        return userDomain;
    }
}
