package com.shareride.identity.api.response;

import com.shareride.identity.domain.UserDomain;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponse {

    private String token;

    public JwtResponse(String token) {
        this.token = token;
    }

    public static JwtResponse from(UserDomain userDomain) {
        return new JwtResponse(userDomain.getToken());
    }
}
