package com.surofu.exporteru.core.service.mail;

import jakarta.mail.MessagingException;
import org.springframework.mail.MailException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public interface MailService {
    void sendVerificationMail(String to, String verificationCode, LocalDateTime expirationDate, Locale locale) throws MailException, MessagingException, IOException;

    void sendRecoverPasswordVerificationMail(String to, String resetCode, LocalDateTime expirationDate) throws MailException, MessagingException, IOException;

    void sendEmail(String to, String subject, String text) throws MailException, MessagingException, IOException;

    void sendDeleteAccountMail(String to, Locale locale) throws MailException, MessagingException, IOException;

    void sendConfirmDeleteAccountMail(String to, String code, LocalDateTime expirationDate,  Locale locale) throws MailException, MessagingException, IOException;

    void sendSupportMail(String username, String from, String subject, String content, List<MultipartFile> attachments) throws MessagingException, IOException;

    void sendProductOrder(String to, String productUrl, String productTitle, BigDecimal originalPrice, BigDecimal discountedPrice, String email, String firstName, String phoneNumber, Integer quantity) throws MessagingException, IOException;

    void sendPhoneRequestMail(String email, String senderFirstName, String senderEmail, String senderPhoneNumber) throws MessagingException, IOException;
}
