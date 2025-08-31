package com.shareride.identity.domain;

import com.shareride.identity.entity.Role;
import com.shareride.identity.entity.User;
import com.shareride.identity.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class UserDomain {

    private UUID userId;
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private UserStatus status;
    private boolean isCorporateVerified;
    private String token;
    private Set<Role> roles;

    public User toUser() {
        return User.builder()
                .id(this.userId)
                .fullName(this.fullName)
                .email(this.email)
                .phone(this.phone)
                .status(this.status)
                .isCorporateVerified(this.isCorporateVerified)
                .build();
    }

    public void updateFrom(User user) {
        this.userId = user.getId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.status = user.getStatus();
        this.isCorporateVerified = user.isCorporateVerified();
        this.roles = user.getRoles();
    }

}
