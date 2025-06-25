package com.surofu.madeinrussia.infrastructure.persistence.product.productPackageOption;

import com.surofu.madeinrussia.core.model.product.productPackageOption.ProductPackageOption;
import com.surofu.madeinrussia.core.repository.ProductPackageOptionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaProductPackageOptionRepository implements ProductPackageOptionsRepository {

    private final SpringDataProductPackageOptionRepository repository;

    @Override
    public List<ProductPackageOption> findAllByProductId(Long productId) {
        return repository.findAllByProduct_Id(productId);
    }
}
