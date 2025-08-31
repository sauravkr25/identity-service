package com.shareride.identity.api.response;

import com.shareride.identity.domain.UserDomain;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private String userId;
    private String fullName;
    private String email;
    private String phone;
    private String status;
    private boolean isCorporateVerified;

    public static UserResponse from(UserDomain userDomain) {
        return UserResponse.builder()
                .userId(userDomain.getUserId().toString())
                .fullName(userDomain.getFullName())
                .email(userDomain.getEmail())
                .phone(userDomain.getPhone())
                .status(userDomain.getStatus().name())
                .isCorporateVerified(userDomain.isCorporateVerified())
                .build();
    }
}
