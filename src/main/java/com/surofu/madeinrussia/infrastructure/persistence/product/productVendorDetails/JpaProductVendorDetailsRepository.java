package com.surofu.madeinrussia.infrastructure.persistence.product.productVendorDetails;

import com.surofu.madeinrussia.core.repository.ProductVendorDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaProductVendorDetailsRepository implements ProductVendorDetailsRepository {

    private final SpringDataProductVendorDetailsRepository repository;

    @Override
    public Optional<ProductVendorDetailsView> getViewByProductId(Long productId) {
        return repository.findViewByProductId(productId);
    }
}
