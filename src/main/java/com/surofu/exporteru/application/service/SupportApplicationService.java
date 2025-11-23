package com.surofu.exporteru.application.service;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import com.surofu.exporteru.core.repository.TranslationRepository;
import com.surofu.exporteru.core.service.mail.MailService;
import com.surofu.exporteru.core.service.support.SupportService;
import com.surofu.exporteru.core.service.support.operation.SendSupportMail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupportApplicationService implements SupportService {

  private final MailService mailService;
  private final TranslationRepository translationRepository;

  @Override
  public SendSupportMail.Result sendSupportMail(SendSupportMail operation) {
    if (StringUtils.trimToNull(operation.getUsername()) == null) {
      throw new LocalizedValidationException("validation.support.username.empty");
    }

    if (StringUtils.trimToNull(operation.getEmail()) == null) {
      throw new LocalizedValidationException("validation.support.email.empty");
    }

    if (StringUtils.trimToNull(operation.getBody()) == null) {
      throw new LocalizedValidationException("validation.support.body.empty");
    }

    String subject, body;

    try {
      subject = "Помощь - " + translationRepository.translateToRu(operation.getSubject())
          .getTranslations()[0].getText();
      body =
          translationRepository.translateToRu(operation.getBody()).getTranslations()[0].getText();
    } catch (Exception e) {
      return SendSupportMail.Result.translationError(e);
    }

    try {
      mailService.sendSupportMail(
          operation.getUsername(),
          operation.getEmail(),
          operation.getPhoneNumber(),
          subject,
          body,
          operation.getMedia()
      );
      return SendSupportMail.Result.success(operation.getEmail());
    } catch (Exception e) {
      return SendSupportMail.Result.sendEmailError(e);
    }
  }
}
