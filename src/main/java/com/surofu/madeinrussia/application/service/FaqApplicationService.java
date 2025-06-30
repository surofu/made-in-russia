package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.FaqDto;
import com.surofu.madeinrussia.core.model.faq.Faq;
import com.surofu.madeinrussia.core.repository.FaqRepository;
import com.surofu.madeinrussia.core.service.faq.FaqService;
import com.surofu.madeinrussia.core.service.faq.operation.CreateFaq;
import com.surofu.madeinrussia.core.service.faq.operation.DeleteFaqById;
import com.surofu.madeinrussia.core.service.faq.operation.GetAllFaq;
import com.surofu.madeinrussia.core.service.faq.operation.UpdateFaqById;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "faq")
public class FaqApplicationService implements FaqService {

    private final FaqRepository repository;

    @Override
    @Cacheable
    @Transactional(readOnly = true)
    public GetAllFaq.Result getAllFaq(GetAllFaq operation) {
        List<Faq> faqList = repository.getAllFaq();
        List<FaqDto> faqDtoList = new ArrayList<>(faqList.size());

        for (Faq faq : faqList) {
            faqDtoList.add(FaqDto.of(faq));
        }

        System.out.println("Retrieved from datasource");

        return GetAllFaq.Result.success(faqDtoList);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional
    public CreateFaq.Result createFaq(CreateFaq operation) {
        Faq faq = new Faq();
        faq.setQuestion(operation.getQuestion());
        faq.setAnswer(operation.getAnswer());
        return CreateFaq.Result.success();
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional
    public UpdateFaqById.Result updateFaqById(UpdateFaqById operation) {
        Optional<Faq> faq = repository.getFaqById(operation.getFaqId());

        if (faq.isEmpty()) {
            return UpdateFaqById.Result.notFound(operation.getFaqId());
        }

        faq.get().setQuestion(operation.getQuestion());
        faq.get().setAnswer(operation.getAnswer());

        return UpdateFaqById.Result.success(operation.getFaqId());
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional
    public DeleteFaqById.Result deleteFaqById(DeleteFaqById operation) {
        Optional<Faq> faq = repository.getFaqById(operation.getFaqId());

        if (faq.isEmpty()) {
            return DeleteFaqById.Result.notFound(operation.getFaqId());
        }

        repository.deleteFaqById(operation.getFaqId());
        return DeleteFaqById.Result.success(operation.getFaqId());
    }
}
