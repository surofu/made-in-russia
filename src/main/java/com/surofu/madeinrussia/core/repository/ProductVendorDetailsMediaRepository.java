package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.infrastructure.persistence.product.productVendorDetails.productVendorDetailsMedia.ProductVendorDetailsMediaView;

import java.util.List;

public interface ProductVendorDetailsMediaRepository {

    List<ProductVendorDetailsMediaView> getAllViewsByProductVendorDetailsId(Long id);
}
