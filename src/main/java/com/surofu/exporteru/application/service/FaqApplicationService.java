package com.surofu.exporteru.application.service;

import com.surofu.exporteru.application.dto.faq.FaqDto;
import com.surofu.exporteru.application.dto.faq.FaqWithTranslationsDto;
import com.surofu.exporteru.application.exception.EmptyTranslationException;
import com.surofu.exporteru.core.model.faq.Faq;
import com.surofu.exporteru.core.repository.FaqRepository;
import com.surofu.exporteru.core.repository.TranslationRepository;
import com.surofu.exporteru.core.service.faq.FaqService;
import com.surofu.exporteru.core.service.faq.operation.CreateFaq;
import com.surofu.exporteru.core.service.faq.operation.DeleteFaqById;
import com.surofu.exporteru.core.service.faq.operation.GetAllFaq;
import com.surofu.exporteru.core.service.faq.operation.GetFaqById;
import com.surofu.exporteru.core.service.faq.operation.GetFaqWithTranslationsById;
import com.surofu.exporteru.core.service.faq.operation.UpdateFaqById;
import com.surofu.exporteru.infrastructure.persistence.faq.FaqView;
import com.surofu.exporteru.infrastructure.persistence.faq.FaqWithTranslationsView;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  public GetFaqWithTranslationsById.Result getFaqWithTranslationsById(
      GetFaqWithTranslationsById operation) {
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

    try {
      faq.getQuestion()
          .setTranslations(translationRepository.expand(operation.getQuestionTranslations()));
      faq.getAnswer()
          .setTranslations(translationRepository.expand(operation.getAnswerTranslations()));
    } catch (EmptyTranslationException e) {
      return CreateFaq.Result.emptyTranslations(e);
    } catch (Exception e) {
      return CreateFaq.Result.translationError(e);
    }

    Faq savedFaq;

    try {
      savedFaq = faqRepository.save(faq);
    } catch (Exception e) {
      return CreateFaq.Result.saveFaqError(e);
    }

    return CreateFaq.Result.success(FaqDto.of(savedFaq));
  }

  @Override
  @Transactional
  public UpdateFaqById.Result updateFaqById(UpdateFaqById operation) {
    Optional<Faq> faqOptional = faqRepository.getById(operation.getFaqId());

    if (faqOptional.isEmpty()) {
      return UpdateFaqById.Result.notFound(operation.getFaqId());
    }

    Faq faq = faqOptional.get();

    faq.setQuestion(operation.getQuestion());
    faq.setAnswer(operation.getAnswer());

    try {
      faq.getQuestion()
          .setTranslations(translationRepository.expand(operation.getQuestionTranslations()));
      faq.getAnswer()
          .setTranslations(translationRepository.expand(operation.getAnswerTranslations()));
    } catch (EmptyTranslationException e) {
      return UpdateFaqById.Result.emptyTranslations(e);
    } catch (Exception e) {
      return UpdateFaqById.Result.translationError(e);
    }

    try {
      faqRepository.save(faq);
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
}
