package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.application.utils.MailTemplates;
import com.surofu.madeinrussia.core.service.mail.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MailApplicationService implements MailService {
    private final JavaMailSender mailSender;
    private final LocalizationManager localizationManager;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendVerificationMail(String to, String verificationCode, LocalDateTime expirationDate, Locale locale) throws MailException, MessagingException {
        String template = MailTemplates.getEmailVerification(verificationCode, formatDate(expirationDate), locale);
        String message = String.format(template, verificationCode, formatDate(expirationDate));
        String subject = localizationManager.localize("auth.email_verification.main_subject", locale);
        sendEmail(to, subject, message);
    }

    @Override
    public void sendRecoverPasswordVerificationMail(String to, String resetCode, LocalDateTime expirationDate) throws MailException, MessagingException {
        String template = """
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Восстановление пароля</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f4;
                            color: #333;
                            padding: 20px;
                        }
                        .container {
                            background-color: #fff;
                            border-radius: 8px;
                            padding: 20px;
                            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                        }
                        h1 {
                            font-size: 72px;
                            color: #4CAF50;
                            text-align: center;
                        }
                        .footer {
                            margin-top: 20px;
                            font-size: 14px;
                            text-align: center;
                        }
                        img {
                            display: block;
                            margin: 0 auto 20px;
                        }
                        .button {
                            display: inline-block;
                            background-color: #4CAF50;
                            color: white;
                            padding: 12px 24px;
                            text-align: center;
                            text-decoration: none;
                            border-radius: 4px;
                            margin: 20px 0;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h2>Восстановление пароля</h2>
                        <p>Вы получили это письмо, потому что был запрошен сброс пароля для вашей учетной записи в Exporteru.</p>
                
                        <strong>Ваш код для восстановления:</strong>
                        <h1>%s</h1>
                
                        <p>Пожалуйста, введите этот код на странице восстановления пароля. Если вы не запрашивали сброс пароля, проигнорируйте это письмо или свяжитесь с нашей поддержкой.</p>
                
                        <p>Код действителен до: %s</p>
                
                        <p>Если у вас возникли проблемы, пожалуйста, свяжитесь с нашей службой поддержки.</p>
                
                        <div class="footer">
                            <p>С уважением,<br>Команда exporteru.com</p>
                        </div>
                    </div>
                </body>
                </html>
                """;
        String message = String.format(template, resetCode, formatDate(expirationDate));
        String subject = "Подтверждение для восстановления пароля на сайте Exporteru.com";
        sendEmail(to, subject, message);
    }

    @Override
    public void sendDeleteAccountMail(String to, Locale locale) throws MailException, MessagingException {
        String message = MailTemplates.getDeleteAccountMail(locale);
        String subject = localizationManager.localize("account.mail.deleted");
        sendEmail(to, subject, message);
    }

    @Override
    public void sendEmail(String to, String subject, String text) throws MailException, MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);

        message.setContent(text, "text/html;charset=utf-8");
        mailSender.send(message);
    }

    private String formatDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return date.format(formatter);
    }
}
