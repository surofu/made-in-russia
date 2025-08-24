package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.faq.FaqDto;
import com.surofu.madeinrussia.application.dto.faq.FaqWithTranslationsDto;
import com.surofu.madeinrussia.application.dto.translation.HstoreTranslationDto;
import com.surofu.madeinrussia.application.exception.EmptyTranslationException;
import com.surofu.madeinrussia.core.model.faq.Faq;
import com.surofu.madeinrussia.core.repository.FaqRepository;
import com.surofu.madeinrussia.core.repository.TranslationRepository;
import com.surofu.madeinrussia.core.service.faq.FaqService;
import com.surofu.madeinrussia.core.service.faq.operation.*;
import com.surofu.madeinrussia.infrastructure.persistence.faq.FaqView;
import com.surofu.madeinrussia.infrastructure.persistence.faq.FaqWithTranslationsView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FaqApplicationService implements FaqService {

    private final FaqRepository faqRepository;
    private final TranslationRepository translationRepository;

    @Override
    @Transactional(readOnly = true)
    public GetAllFaq.Result getAllFaq(GetAllFaq operation) {
        List<FaqView> viewList = faqRepository.getAllViewsByLang(operation.getLocale().getLanguage());
        List<FaqDto> faqDtoList = new ArrayList<>(viewList.size());

        for (FaqView view : viewList) {
            faqDtoList.add(FaqDto.of(view));
        }

        return GetAllFaq.Result.success(faqDtoList);
    }

    @Override
    @Transactional(readOnly = true)
    public GetFaqById.Result getFaqById(GetFaqById operation) {
        Optional<FaqView> view = faqRepository.getViewByIdAndLang(
                operation.getFaqId(), operation.getLocale().getLanguage());

        if (view.isEmpty()) {
            return GetFaqById.Result.notFound(operation.getFaqId());
        }

        return GetFaqById.Result.success(FaqDto.of(view.get()));
    }

    @Override
    @Transactional(readOnly = true)
    public GetFaqWithTranslationsById.Result getFaqWithTranslationsById(GetFaqWithTranslationsById operation) {
        Optional<FaqWithTranslationsView> view = faqRepository.getViewWithTranslationsByIdAndLang(
                operation.getFaqId(), operation.getLocale().getLanguage());

        if (view.isEmpty()) {
            return GetFaqWithTranslationsById.Result.notFound(operation.getFaqId());
        }

        return GetFaqWithTranslationsById.Result.success(FaqWithTranslationsDto.of(view.get()));
    }

    @Override
    @Transactional
    public CreateFaq.Result createFaq(CreateFaq operation) {
        Faq faq = new Faq();
        faq.setQuestion(operation.getQuestion());
        faq.setAnswer(operation.getAnswer());

        Map<String, HstoreTranslationDto> translationMap = new HashMap<>();
        translationMap.put(TranslationKeys.QUESTION.name(), operation.getQuestionTranslations());
        translationMap.put(TranslationKeys.ANSWER.name(), operation.getAnswerTranslations());

        Map<String, HstoreTranslationDto> resultMap;

        try {
            resultMap = translationRepository.expand(translationMap);
        } catch (EmptyTranslationException e) {
            return CreateFaq.Result.emptyTranslations(e);
        } catch (Exception e) {
            return CreateFaq.Result.translationError(e);
        }

        HstoreTranslationDto translatedQuestion = resultMap.get(TranslationKeys.QUESTION.name());
        HstoreTranslationDto translatedAnswer = resultMap.get(TranslationKeys.ANSWER.name());

        faq.getQuestion().setTranslations(translatedQuestion);
        faq.getAnswer().setTranslations(translatedAnswer);

        try {
            faqRepository.save(faq);
            return CreateFaq.Result.success();
        } catch (Exception e) {
            return CreateFaq.Result.saveFaqError(e);
        }
    }

    @Override
    @Transactional
    public UpdateFaqById.Result updateFaqById(UpdateFaqById operation) {
        Optional<Faq> faq = faqRepository.getById(operation.getFaqId());

        if (faq.isEmpty()) {
            return UpdateFaqById.Result.notFound(operation.getFaqId());
        }

        faq.get().setQuestion(operation.getQuestion());
        faq.get().setAnswer(operation.getAnswer());

        Map<String, HstoreTranslationDto> translationMap = new HashMap<>();
        translationMap.put(TranslationKeys.QUESTION.name(), operation.getQuestionTranslations());
        translationMap.put(TranslationKeys.ANSWER.name(), operation.getAnswerTranslations());

        Map<String, HstoreTranslationDto> resultMap;

        try {
            resultMap = translationRepository.expand(translationMap);
        } catch (EmptyTranslationException e) {
            return UpdateFaqById.Result.emptyTranslations(e);
        } catch (Exception e) {
            return UpdateFaqById.Result.translationError(e);
        }

        HstoreTranslationDto translatedQuestion = resultMap.get(TranslationKeys.QUESTION.name());
        HstoreTranslationDto translatedAnswer = resultMap.get(TranslationKeys.ANSWER.name());

        faq.get().getQuestion().setTranslations(translatedQuestion);
        faq.get().getAnswer().setTranslations(translatedAnswer);

        try {
            faqRepository.save(faq.get());
            return UpdateFaqById.Result.success(operation.getFaqId());
        } catch (Exception e) {
            return UpdateFaqById.Result.saveFaqError(e);
        }
    }

    @Override
    @Transactional
    public DeleteFaqById.Result deleteFaqById(DeleteFaqById operation) {
        Optional<Faq> faq = faqRepository.getById(operation.getFaqId());

        if (faq.isEmpty()) {
            return DeleteFaqById.Result.notFound(operation.getFaqId());
        }

        try {
            faqRepository.delete(faq.get());
            return DeleteFaqById.Result.success(operation.getFaqId());
        } catch (Exception e) {
            return DeleteFaqById.Result.deleteError(e);
        }
    }

    private enum TranslationKeys {
        QUESTION, ANSWER
    }
}
