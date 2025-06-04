package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.core.service.mail.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailApplicationService implements MailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendVerificationMail(String to, String verificationCode, String expiration) throws MailException, MessagingException {
        String VERIFICATION_MAIL_TEMPLATE = """
                <!DOCTYPE html>
                    <html lang="ru">
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Подтверждение электронной почты</title>
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
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <h2>Здравствуйте!</h2>
                            <p>Спасибо за регистрацию в MadeInRussia! Чтобы завершить процесс регистрации, пожалуйста, подтвердите свою электронную почту, введя код ниже:</p>
                           \s
                            <strong>Ваш код подтверждения:</strong>
                            <h1>%s</h1>
                           \s
                            <p>Пожалуйста, введите этот код в соответствующее поле на нашем сайте. Если вы не регистрировались в MadeInRussia, просто проигнорируйте это сообщение.</p>
                           \s
                            <p>Код истечет %s</p>
                           \s
                            <p>Спасибо, что выбрали MadeInRussia!</p>
                           \s
                            <div class="footer">
                                <p>С уважением,<br>Команда MadeInRussia</p>
                            </div>
                        </div>
                    </body>
                    </html>
                """;
        String message = String.format(VERIFICATION_MAIL_TEMPLATE, verificationCode, expiration);
        String VERIFICATION_MAIL_SUBJECT = "Подтверждение вашей электронной почты для MadeInRussia";
        sendEmail(to, VERIFICATION_MAIL_SUBJECT, message);
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
}
