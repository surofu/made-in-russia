package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.core.repository.TranslationRepository;
import com.surofu.madeinrussia.core.service.mail.MailService;
import com.surofu.madeinrussia.core.service.support.SupportService;
import com.surofu.madeinrussia.core.service.support.operation.SendSupportMail;
import com.surofu.madeinrussia.infrastructure.persistence.translation.TranslationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupportApplicationService implements SupportService {

    private final MailService mailService;
    private final TranslationRepository translationRepository;

    @Override
    public SendSupportMail.Result sendSupportMail(SendSupportMail operation) {
        TranslationResponse subjectTranslationResponse;
        TranslationResponse bodyTranslationResponse;

        try {
            subjectTranslationResponse = translationRepository.translateToRu(operation.getSubject());
            bodyTranslationResponse = translationRepository.translateToRu(operation.getBody());
        } catch (Exception e) {
            return SendSupportMail.Result.translationError(e);
        }

        try {
            mailService.sendSupportMail(
                    operation.getUsername(),
                    operation.getEmail(),
                    subjectTranslationResponse.getTranslations()[0].getText(),
                    bodyTranslationResponse.getTranslations()[0].getText(),
                    operation.getMedia()
            );
            return SendSupportMail.Result.success(operation.getEmail());
        } catch (Exception e) {
            return SendSupportMail.Result.sendEmailError(e);
        }
    }
}
