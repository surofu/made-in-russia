package com.surofu.madeinrussia.core.service.mail;

import jakarta.mail.MessagingException;
import org.springframework.mail.MailException;

public interface MailService {
    void sendVerificationMail(String to, String verificationCode, String expiration) throws MailException, MessagingException;

    void sendRecoverPasswordVerificationMail(String to, String resetCode, String expiration) throws MailException, MessagingException;

    void sendEmail(String to, String subject, String text) throws MailException, MessagingException;
}
