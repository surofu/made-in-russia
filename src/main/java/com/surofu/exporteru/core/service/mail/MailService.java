package com.surofu.exporteru.core.service.mail;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.mail.MailException;
import org.springframework.web.multipart.MultipartFile;

public interface MailService {
  void sendVerificationMail(String to, String verificationCode, ZonedDateTime expirationDate,
                            Locale locale) throws MailException, IOException;

  void sendRecoverPasswordVerificationMail(String to, String resetCode,
                                           ZonedDateTime expirationDate, Locale locale)
      throws MailException, IOException;

  void sendDeleteAccountMail(String to, Locale locale) throws MailException, IOException;

  void sendConfirmDeleteAccountMail(String to, String code, ZonedDateTime expirationDate,
                                    Locale locale) throws MailException, IOException;

  void sendSupportMail(String username, String from, String phoneNumber, String subject,
                       String content, List<MultipartFile> attachments) throws IOException;

  void sendProductOrder(String to, String productUrl, String productTitle, BigDecimal originalPrice,
                        BigDecimal discountedPrice, String email, String firstName,
                        String phoneNumber, Integer quantity) throws IOException;

  void sendPhoneRequestMail(String email, String senderFirstName, String senderEmail,
                            String senderPhoneNumber) throws IOException;

  void sendRejectedProductMail(String to, String productUrl, Map<String, String> translations)
      throws MailException, IOException;
}
