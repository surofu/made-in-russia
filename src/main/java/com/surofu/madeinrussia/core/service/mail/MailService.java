package com.surofu.madeinrussia.core.service.mail;

import jakarta.mail.MessagingException;
import org.springframework.mail.MailException;

public interface MailService {
    void sendEmail(String to, String subject, String text) throws MailException, MessagingException;
}
