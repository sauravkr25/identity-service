package com.shareride.identity.api.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendAndVerifyEmailResponse {

    private String status;
    private String message;
}

