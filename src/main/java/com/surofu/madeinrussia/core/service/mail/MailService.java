package com.surofu.madeinrussia.core.service.mail;

import jakarta.mail.MessagingException;
import org.springframework.mail.MailException;

import java.time.LocalDateTime;
import java.util.Locale;

public interface MailService {
    void sendVerificationMail(String to, String verificationCode, LocalDateTime expirationDate, Locale locale) throws MailException, MessagingException;

    void sendRecoverPasswordVerificationMail(String to, String resetCode, LocalDateTime expirationDate) throws MailException, MessagingException;

    void sendEmail(String to, String subject, String text) throws MailException, MessagingException;

    void sendDeleteAccountMail(String to, Locale locale) throws MailException, MessagingException;
}
