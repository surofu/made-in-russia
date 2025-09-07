package com.surofu.madeinrussia.infrastructure.persistence.product;

import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.model.moderation.ApproveStatus;
import com.surofu.madeinrussia.core.model.product.Product;
import com.surofu.madeinrussia.core.model.product.media.ProductMedia;
import com.surofu.madeinrussia.core.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaProductRepository implements ProductRepository {

    private final SpringDataProductRepository repository;

    @Override
    public Optional<Product> getProductById(Long productId) {
        return repository.findByIdAndApproveStatusIn(productId, List.of(ApproveStatus.APPROVED));
    }

    @Override
    public Optional<Product> getProductById(Long productId, List<ApproveStatus> approveStatuses) {
        return repository.findByIdAndApproveStatusIn(productId, approveStatuses);
    }

    @Override
    public Optional<ProductView> getProductViewByIdAndLangAndApproveStatuses(Long productId, String lang, List<ApproveStatus> approveStatuses) {
        return repository.findProductViewByIdAndLangAndStatuses(productId, lang, approveStatuses.stream().map(ApproveStatus::name).toList());
    }

    @Override
    public Optional<Product> getProductByIdWithAnyApproveStatus(Long productId) {
        return repository.findById(productId);
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
        return repository.findAllByIdInAndApproveStatus(productIds, ApproveStatus.APPROVED);
    }

    @Override
    public boolean existsById(Long productId) {
        return repository.existsByIdAndApproveStatus(productId, ApproveStatus.APPROVED);
    }

    @Override
    public List<SearchHintView> findHintViews(String searchTerm, Long vendorId, Locale locale) {
        return repository.findHintViews(searchTerm, vendorId, locale.getLanguage());
    }

    @Override
    public void delete(Product product) {
        repository.delete(product);
    }

    @Override
    public void deleteByUserId(Long userId) {
        repository.deleteByUserId(userId);
    }

    // View
    @Override
    public Optional<ProductView> getProductViewByArticleAndLang(String article, String lang) {
        return repository.findProductViewByArticleCodeAndLang(article, lang);
    }

    @Override
    public List<SimilarProductView> getAllSimilarProductViewsByProductIdAndLang(Long id, String lang) {
        return repository.findAllSimilarProductViewByIdAndLang(id, lang);
    }

    @Override
    public Optional<ProductWithTranslationsView> getProductWithTranslationsByProductIdAndLang(Long id, String lang) {
        return repository.findProductWithTranslationsByIdAndLang(id, lang);
    }

    @Override
    public void flush() {
        repository.flush();
    }
}