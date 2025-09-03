package com.surofu.madeinrussia.core.service.mail;

import jakarta.mail.MessagingException;
import org.springframework.mail.MailException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public interface MailService {
    void sendVerificationMail(String to, String verificationCode, LocalDateTime expirationDate, Locale locale) throws MailException, MessagingException;

    void sendRecoverPasswordVerificationMail(String to, String resetCode, LocalDateTime expirationDate) throws MailException, MessagingException;

    void sendEmail(String to, String subject, String text) throws MailException, MessagingException;

    void sendDeleteAccountMail(String to, Locale locale) throws MailException, MessagingException;

    void sendConfirmDeleteAccountMail(String to, String code, LocalDateTime expirationDate,  Locale locale) throws MailException, MessagingException;

    void sendSupportMail(String username, String from, String subject, String content, List<MultipartFile> attachments) throws MessagingException;
}
