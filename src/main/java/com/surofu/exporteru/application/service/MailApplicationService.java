package com.surofu.exporteru.application.service;

import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.application.utils.MailTemplates;
import com.surofu.exporteru.core.service.mail.MailService;
import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
  public void sendVerificationMail(String to, String verificationCode, ZonedDateTime expirationDate,
                                   Locale locale) throws MailException, IOException {
    String template =
        MailTemplates.getEmailVerification(verificationCode, formatDate(expirationDate), locale);
    String message = String.format(template, verificationCode, formatDate(expirationDate));
    String subject = localizationManager.localize("auth.email_verification.main_subject", locale);
    sendWithMailer(to, subject, message);
  }

  @Override
  public void sendRecoverPasswordVerificationMail(String to, String resetCode,
                                                  ZonedDateTime expirationDate, Locale locale)
      throws MailException, IOException {
    String message =
        MailTemplates.getRecoverPasswordMail(resetCode, formatDate(expirationDate), locale);
    String subject = localizationManager.localize("account.mail.recover_password");
    sendWithMailer(to, subject, message);
  }

  @Override
  public void sendDeleteAccountMail(String to, Locale locale) throws MailException, IOException {
    String message = MailTemplates.getDeleteAccountMail(locale);
    String subject = localizationManager.localize("account.mail.account_deleted_mail_subject");
    sendWithMailer(to, subject, message);
  }

  @Override
  public void sendConfirmDeleteAccountMail(String to, String code, ZonedDateTime expirationDate,
                                           Locale locale) throws MailException, IOException {
    String message =
        MailTemplates.getConfirmDeleteAccountMail(code, formatDate(expirationDate), locale);
    String subject =
        localizationManager.localize("account.mail.confirm_delete_account_mail_subject");
    sendWithMailer(to, subject, message);
  }

  @Override
  public void sendSupportMail(String username, String from, String phoneNumber, String subject,
                              String content, List<MultipartFile> attachments) throws IOException {
    String date = formatDate(ZonedDateTime.now());
    String message =
        MailTemplates.getSupportMail(username, from, phoneNumber, subject, content, date);
    sendWithMailer(supportMail, subject, message, attachments);
  }

  @Override
  public void sendProductOrder(String to, String productUrl, String productTitle,
                               BigDecimal originalPrice, BigDecimal discountedPrice,
                               String firstName, String phoneNumber, String comment)
      throws IOException {
    String template =
        MailTemplates.getOrderMail(productUrl, productTitle, originalPrice, discountedPrice,
            firstName, phoneNumber, comment);
    sendWithMailer(to, "\uD83D\uDCE6 Новый заказ", template);
  }

  @Override
  public void sendPhoneRequestMail(String to, String senderFirstName, String senderEmail,
                                   String senderPhoneNumber) throws IOException {
    String template =
        MailTemplates.getPhoneRequestMail(senderFirstName, senderEmail, senderPhoneNumber,
            formatDate(ZonedDateTime.now()));
    sendWithMailer(to, "\uD83D\uDCDE Заявка на звонок / Call Request / 通话请求", template);
  }

  @Override
  public void sendRejectedProductMail(String to, String productUrl,
                                      Map<String, String> translations)
      throws MailException, IOException {
    String template = MailTemplates.getRejectedProductMail(productUrl, translations);
    sendWithMailer(to,
        "\uD83D\uDCDE Модерация отклонила ваш товар / The moderation rejected your product / 审核拒绝了您的商品",
        template);
  }

  // Private

  private String formatDate(ZonedDateTime date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    return date.format(formatter);
  }

  // Simple Java Mail

  private void sendWithMailer(String to, String subject, String text) throws IOException {
    sendWithMailer(to, subject, text, Collections.emptyList());
  }

  public void sendWithMailer(String to, String subject, String text,
                             List<MultipartFile> attachments) throws IOException {
    Email email = buildMail(to, subject, text, attachments);
    mailer.sendMail(email, true);
  }

  private Email buildMail(String to, String subject, String text, List<MultipartFile> attachments)
      throws IOException {
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
    if (html == null) {
      return "";
    }
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
