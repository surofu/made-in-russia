package com.surofu.madeinrussia.infrastructure.persistence.product;

import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.model.product.Product;
import com.surofu.madeinrussia.core.model.product.productCharacteristic.ProductCharacteristic;
import com.surofu.madeinrussia.core.model.product.productFaq.ProductFaq;
import com.surofu.madeinrussia.core.model.product.productMedia.ProductMedia;
import com.surofu.madeinrussia.core.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaProductRepository implements ProductRepository {

    private final SpringDataProductRepository repository;

    @Override
    public Page<Product> getProductPage(Specification<Product> specification, Pageable pageable) {
        return repository.findAll(specification, pageable);
    }

    @Override
    @Transactional
    public Optional<Product> getProductById(Long productId) {
        return repository.getProductById(productId);
    }

    @Override
    public Optional<Category> getProductCategoryByProductId(Long productId) {
        return repository.getProductCategoryByProductId(productId);
    }

    @Override
    public Optional<List<DeliveryMethod>> getProductDeliveryMethodsByProductId(Long productId) {
        return repository.getProductDeliveryMethodsByProductId(productId);
    }

    @Override
    public Optional<List<ProductMedia>> getProductMediaByProductId(Long productId) {
        return repository.getProductMediaByProductId(productId);
    }

    @Override
    public Optional<List<ProductCharacteristic>> getProductCharacteristicsByProductId(Long productId) {
        return repository.getProductCharacteristicsByProductId(productId);
    }

    @Override
    public Optional<List<ProductFaq>> getProductFaqByProductId(Long productId) {
        return repository.getProductFaqByProductId(productId);
    }
}