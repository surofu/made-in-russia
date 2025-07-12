package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.infrastructure.persistence.product.productVendorDetails.ProductVendorDetailsView;

import java.util.Optional;

public interface ProductVendorDetailsRepository {
    Optional<ProductVendorDetailsView> getViewByProductId(Long productId);
}
