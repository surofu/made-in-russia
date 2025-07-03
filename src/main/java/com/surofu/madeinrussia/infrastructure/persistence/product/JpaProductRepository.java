package com.surofu.madeinrussia.infrastructure.persistence.product;

import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.model.product.Product;
import com.surofu.madeinrussia.core.model.product.ProductArticleCode;
import com.surofu.madeinrussia.core.model.product.productCharacteristic.ProductCharacteristic;
import com.surofu.madeinrussia.core.model.product.productFaq.ProductFaq;
import com.surofu.madeinrussia.core.model.product.productMedia.ProductMedia;
import com.surofu.madeinrussia.core.repository.ProductRepository;
import com.surofu.madeinrussia.infrastructure.persistence.view.SearchHintView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaProductRepository implements ProductRepository {

    private final SpringDataProductRepository repository;

    @Override
    public Optional<Product> getProductById(Long productId) {
        return repository.findById(productId);
    }

    @Override
    public Optional<Product> getProductByArticleCode(ProductArticleCode articleCode) {
        return repository.findByArticleCode(articleCode);
    }

    @Override
    public Optional<Category> getProductCategoryByProductId(Long productId) {
        return repository.getProductCategoryByProductId(productId);
    }

    @Override
    public List<DeliveryMethod> getProductDeliveryMethodsByProductId(Long productId) {
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

    @Override
    public void save(Product product) {
        repository.save(product);
    }

    @Override
    public Optional<Long> firstNotExists(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Optional.empty();
        }
        return repository.firstNotExists(productIds.toArray(new Long[0]));
    }

    @Override
    public List<Product> findAllByIds(List<Long> productIds) {
        return repository.findAllById(productIds);
    }

    @Override
    public Optional<Double> getProductRating(Long productId) {
        return repository.getProductRatingById(productId);
    }

    @Override
    public boolean existsById(Long productId) {
        return repository.existsById(productId);
    }

    @Override
    public List<SearchHintView> findHintViews(String searchTerm) {
        return repository.findHintViews(searchTerm);
    }
}