package com.shareride.identity.service;

import com.shareride.identity.domain.EmailMessage;

public interface EmailService {

    void sendEmail(EmailMessage emailMessage);
}
