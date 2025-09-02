package com.shareride.identity.service.impl;

import com.shareride.identity.config.properties.AppProperties;
import com.shareride.identity.domain.EmailMessage;
import com.shareride.identity.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final AppProperties appProperties;

    public EmailServiceImpl(JavaMailSender mailSender, AppProperties appProperties) {
        this.mailSender = mailSender;
        this.appProperties = appProperties;
    }

    @Override
    @Async
    public void sendEmail(EmailMessage emailMessage) {
        logger.info("Queueing email for sending to {} in a background thread.", emailMessage.getTo());
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailMessage.getTo());
            message.setFrom(appProperties.getSupportEmail());
            message.setSubject(emailMessage.getSubject());
            message.setText(emailMessage.getBody());

            mailSender.send(message);
            logger.info("Email sent successfully to {}.", emailMessage.getTo());
        } catch (MailException e) {
            logger.error("Failed to send email to {}: {}", emailMessage.getTo(), e.getMessage());
        }
    }
}
