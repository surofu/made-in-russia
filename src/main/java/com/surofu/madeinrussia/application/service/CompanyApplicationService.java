package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.core.model.okved.OkvedCompany;
import com.surofu.madeinrussia.core.repository.CategoryRepository;
import com.surofu.madeinrussia.core.service.company.CompanyService;
import com.surofu.madeinrussia.core.service.company.operation.GetCompaniesByCategorySlug;
import com.surofu.madeinrussia.infrastructure.persistence.okved.OkvedCompanyRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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

    private ThreadPoolTaskExecutor taskExecutor;

    @PostConstruct
    public void init() {
        this.taskExecutor = new ThreadPoolTaskExecutor();
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        this.taskExecutor.setCorePoolSize(corePoolSize);
        this.taskExecutor.setMaxPoolSize(corePoolSize);
        this.taskExecutor.setQueueCapacity(100);
        this.taskExecutor.setThreadNamePrefix("OkvedClientThread-");
        this.taskExecutor.initialize();
    }

    @Override
    @Transactional(readOnly = true)
    public GetCompaniesByCategorySlug.Result getByCategorySlug(GetCompaniesByCategorySlug operation) {
        List<String> okvedCategoryIds = categoryRepository.getOkvedCategoryIdsBySlug(operation.getCategorySlug());

        System.out.println("Categories: " + okvedCategoryIds);

        List<CompletableFuture<List<OkvedCompany>>> futures = okvedCategoryIds.stream()
                .map(id -> CompletableFuture.supplyAsync(
                        () -> okvedCompanyRepository.findByOkvedId(id),
                        taskExecutor
                ))
                .toList();

        List<OkvedCompany> okvedCompanyList = futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .toList();

        return GetCompaniesByCategorySlug.Result.success(okvedCompanyList);
    }
}
