package com.shareride.identity.api.response;

import com.shareride.identity.domain.UserDomain;
import com.shareride.identity.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RegisterResponse {

    UUID userId;
    String fullName;
    String email;
    String phone;
    UserStatus status;
    boolean isCorporateVerified;

    public static RegisterResponse from(UserDomain userDomain){
        return RegisterResponse.builder()
                .userId(userDomain.getUserId())
                .fullName(userDomain.getFullName())
                .email(userDomain.getEmail())
                .phone(userDomain.getPhone())
                .status(userDomain.getStatus())
                .isCorporateVerified(userDomain.isCorporateVerified())
                .build();
    }
}
