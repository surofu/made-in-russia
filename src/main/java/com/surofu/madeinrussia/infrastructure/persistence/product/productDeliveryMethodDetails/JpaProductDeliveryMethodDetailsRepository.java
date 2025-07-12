package com.surofu.madeinrussia.infrastructure.persistence.product.productDeliveryMethodDetails;

import com.surofu.madeinrussia.core.model.product.productDeliveryMethodDetails.ProductDeliveryMethodDetails;
import com.surofu.madeinrussia.core.repository.ProductDeliveryMethodDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaProductDeliveryMethodDetailsRepository implements ProductDeliveryMethodDetailsRepository {

    private final SpringDataProductDeliveryMethodDetailsRepository repository;

    @Override
    public List<ProductDeliveryMethodDetails> getAllByProductId(Long productId) {
        return repository.findAllByProduct_Id(productId);

    }

    @Override
    public List<ProductDeliveryMethodDetailsView> getAllViewsByProductId(Long productId) {
        return repository.findAllViewsByProduct_Id(productId);
    }
}
