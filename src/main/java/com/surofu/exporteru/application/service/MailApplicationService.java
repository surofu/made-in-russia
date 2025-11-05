package com.surofu.exporteru.application.service;

import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.application.utils.MailTemplates;
import com.surofu.exporteru.core.service.mail.MailService;
import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.email.EmailPopulatingBuilder;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailApplicationService implements MailService {
    private final LocalizationManager localizationManager;
    private final Mailer mailer;

    @Value("${spring.mail.username}")
    private String mailHost;

    @Value("${app.mail.support}")
    private String supportMail;

    @Value("${spring.mail.from}")
    private String fromName;

    @Override
    public void sendVerificationMail(String to, String verificationCode, LocalDateTime expirationDate, Locale locale) throws MailException, IOException {
        String template = MailTemplates.getEmailVerification(verificationCode, formatDate(expirationDate), locale);
        String message = String.format(template, verificationCode, formatDate(expirationDate));
        String subject = localizationManager.localize("auth.email_verification.main_subject", locale);
        sendWithMailer(to, subject, message);
    }

    @Override
    public void sendRecoverPasswordVerificationMail(String to, String resetCode, LocalDateTime expirationDate) throws MailException, IOException {
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
        sendWithMailer(to, subject, message);
    }

    @Override
    public void sendDeleteAccountMail(String to, Locale locale) throws MailException, IOException {
        String message = MailTemplates.getDeleteAccountMail(locale);
        String subject = localizationManager.localize("account.mail.account_deleted_mail_subject");
        sendWithMailer(to, subject, message);
    }

    @Override
    public void sendConfirmDeleteAccountMail(String to, String code, LocalDateTime expirationDate, Locale locale) throws MailException, IOException {
        String message = MailTemplates.getConfirmDeleteAccountMail(code, formatDate(expirationDate), locale);
        String subject = localizationManager.localize("account.mail.confirm_delete_account_mail_subject");
        sendWithMailer(to, subject, message);
    }

    @Override
    public void sendSupportMail(String username, String from, String phoneNumber, String subject, String content, List<MultipartFile> attachments) throws IOException {
        String date = formatDate(LocalDateTime.now());
        String message = MailTemplates.getSupportMail(username, from, phoneNumber, subject, content, date);
        sendWithMailer("8268363@gmail.com", subject, message, attachments);
    }

    @Override
    public void sendProductOrder(String to, String productUrl, String productTitle, BigDecimal originalPrice, BigDecimal discountedPrice, String firstName, String email, String phoneNumber, Integer quantity) throws IOException {
        String template = MailTemplates.getOrderMail(productUrl, productTitle, originalPrice, discountedPrice, firstName, email, phoneNumber, quantity);
        sendWithMailer(to, "\uD83D\uDCE6 Новый заказ", template);
    }

    @Override
    public void sendPhoneRequestMail(String to, String senderFirstName, String senderEmail, String senderPhoneNumber) throws IOException {
        String template = MailTemplates.getPhoneRequestMail(senderFirstName, senderEmail, senderPhoneNumber, formatDate(LocalDateTime.now()));
        sendWithMailer(to, "\uD83D\uDCDE Заявка на звонок / Call Request / 通话请求", template);
    }

    @Override
    public void sendEmail(String to, String subject, String text) throws MailException, IOException {
        sendWithMailer(to, subject, text);
    }

    // Private

    private String formatDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return date.format(formatter);
    }

    // Simple Java Mail

    private void sendWithMailer(String to, String subject, String text) throws IOException {
        sendWithMailer(to, subject, text, Collections.emptyList());
    }

    public void sendWithMailer(String to, String subject, String text, List<MultipartFile> attachments) throws IOException {
        Email email = buildMail(to, subject, text, attachments);
        mailer.sendMail(email, true);
    }

    private Email buildMail( String to, String subject, String text, List<MultipartFile> attachments) throws IOException {
        EmailPopulatingBuilder builder = EmailBuilder.startingBlank()
                // Используем имя отправителя для лучшей доставляемости
                .from(fromName, mailHost)
                .to(to)
                .withSubject(subject)
                .withHTMLText(text)
                // Добавляем plain text версию для лучшей доставляемости
                .withPlainText(stripHtml(text))

                // Критически важные заголовки для Yandex и iCloud
                .withHeader("X-Mailer", "ExporterU Mail System")
                .withHeader("X-Priority", "3") // Нормальный приоритет
                .withHeader("Importance", "Normal")
                .withHeader("Content-Language", "ru")

                // Заголовки против спама
                .withHeader("Precedence", "bulk")
                .withHeader("Auto-Submitted", "auto-generated")

                // Reply-To для корректной обработки ответов
                .withReplyTo(fromName, mailHost)

                // Защита от классификации как спам
                .withReturnReceiptTo(mailHost)

                // Encoding для кириллицы
                .withHeader("Content-Transfer-Encoding", "quoted-printable");

        // Добавляем вложения с правильным encoding
        if (attachments != null) {
            for (MultipartFile attachment : attachments) {
                DataSource dataSource = new ByteArrayDataSource(
                        attachment.getInputStream(),
                        attachment.getContentType()
                );

                String filename = attachment.getOriginalFilename();
                // Обрабатываем кириллицу в именах файлов
                if (filename != null && containsCyrillic(filename)) {
                    filename = new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
                }

                builder.withAttachment(filename, dataSource);
            }
        }

        return builder.buildEmail();
    }

    private String stripHtml(String html) {
        if (html == null) return "";
        return html.replaceAll("<[^>]*>", "")
                .replaceAll("&nbsp;", " ")
                .replaceAll("&amp;", "&")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .trim();
    }

    private boolean containsCyrillic(String text) {
        return text.chars().anyMatch(ch ->
                Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.CYRILLIC
        );
    }
}
