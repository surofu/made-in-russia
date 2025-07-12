package com.surofu.madeinrussia.infrastructure.persistence.product.productVendorDetails.productVendorDetailsMedia;

import com.surofu.madeinrussia.core.repository.ProductVendorDetailsMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaProductVendorDetailsMediaRepository implements ProductVendorDetailsMediaRepository {

    private final SpringDataProductVendorDetailsMediaRepository repository;

    @Override
    public List<ProductVendorDetailsMediaView> getAllViewsByProductVendorDetailsId(Long id) {
        return repository.findAllViewsByProductVendorDetails_Id(id);
    }
}
