package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.okved.OkvedCompany;
import com.surofu.madeinrussia.core.repository.CategoryRepository;
import com.surofu.madeinrussia.core.service.company.CompanyService;
import com.surofu.madeinrussia.core.service.company.operation.GetCompaniesByCategorySlug;
import com.surofu.madeinrussia.infrastructure.persistence.okved.OkvedCompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyApplicationService implements CompanyService {

    private final CategoryRepository categoryRepository;
    private final OkvedCompanyRepository okvedCompanyRepository;

    @Override
    @Transactional(readOnly = true)
    public GetCompaniesByCategorySlug.Result getByCategorySlug(GetCompaniesByCategorySlug operation) {
        Optional<Category> category = categoryRepository.getCategoryWithOkvedCategoriesBySlug(operation.getCategorySlug());

        if (category.isEmpty()) {
            return GetCompaniesByCategorySlug.Result.notFound(operation.getCategorySlug());
        }

        List<CompletableFuture<List<OkvedCompany>>> futures = category.get().getOkvedCategories().stream().map(c -> CompletableFuture.supplyAsync(
                () -> okvedCompanyRepository.findByOkvedId(c.getOkvedId())
        )).toList();

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );

        List<OkvedCompany> okvedCompanyList = allFutures.thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .toList()
        ).join();

        return GetCompaniesByCategorySlug.Result.success(okvedCompanyList);
    }
}
