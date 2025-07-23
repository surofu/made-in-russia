package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.core.model.okved.OkvedCompany;
import com.surofu.madeinrussia.core.repository.CategoryRepository;
import com.surofu.madeinrussia.core.service.company.CompanyService;
import com.surofu.madeinrussia.core.service.company.operation.GetCompaniesByCategorySlug;
import com.surofu.madeinrussia.infrastructure.persistence.okved.OkvedCompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyApplicationService implements CompanyService {

    private final CategoryRepository categoryRepository;
    private final OkvedCompanyRepository okvedCompanyRepository;
    private final TaskExecutor appTaskExecutor;

    @Override
    @Transactional(readOnly = true)
    public GetCompaniesByCategorySlug.Result getByCategorySlug(GetCompaniesByCategorySlug operation) {
        List<String> okvedCategoryIds = categoryRepository.getOkvedCategoryIdsBySlug(operation.getCategorySlug());

        List<CompletableFuture<List<OkvedCompany>>> futures = okvedCategoryIds.stream()
                .map(id -> CompletableFuture.supplyAsync(
                        () -> okvedCompanyRepository.findByOkvedId(id),
                        appTaskExecutor
                ))
                .toList();

        List<OkvedCompany> okvedCompanyList = futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .toList();

        return GetCompaniesByCategorySlug.Result.success(okvedCompanyList);
    }
}
