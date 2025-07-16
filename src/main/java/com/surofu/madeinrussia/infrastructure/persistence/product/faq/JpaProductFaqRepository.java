package com.surofu.madeinrussia.infrastructure.persistence.product.faq;

import com.surofu.madeinrussia.core.repository.ProductFaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaProductFaqRepository implements ProductFaqRepository {

    private final SpringDataProductFaqRepository repository;

    @Override
    public List<ProductFaqView> findAllViewsByProductIdAndLang(Long productId, String lang) {
        return repository.findAllByProductIdAndLang(productId, lang);
    }
}
