package com.surofu.exporteru.infrastructure.persistence.product;

import com.surofu.exporteru.core.model.category.Category;
import com.surofu.exporteru.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.exporteru.core.model.moderation.ApproveStatus;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.media.ProductMedia;
import com.surofu.exporteru.core.repository.ProductRepository;
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
    public Optional<Product> getById(Long productId) {
        return repository.findById(productId);
    }

    @Override
    public Optional<Product> getByIdWithDependencies(Long productId) {
        return repository.findAllById(productId);
    }

    @Override
    public Optional<Product> getProductWithUserById(Long productId) {
        return repository.findByIdWithUser(productId);
    }

    @Override
    public Optional<Product> getProductByIdApproved(Long productId) {
        return repository.findByIdAndApproveStatusIn(productId, List.of(ApproveStatus.APPROVED));
    }

    @Override
    public Product getReferenceById(Long productId) {
        return repository.getReferenceById(productId);
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
    public Product save(Product product) {
        return repository.save(product);
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
        return repository.existsById(productId);
    }

    @Override
    public boolean existsByIdAndStatusApproved(Long productId) {
        return repository.existsByIdAndApproveStatus(productId, ApproveStatus.APPROVED);
    }

    @Override
    public boolean existsWithUserId(Long productId, Long userId) {
        return repository.existsWithUserId(productId, userId);
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

    @Override
    public boolean existsInFavorite(Long userId, Long productId) {
        return repository.existsByIdInUserFavorites(userId, productId);
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
    public List<ProductForReviewView> getProductForReviewViewsByLang(String lang) {
        return repository.findProductForReviewViewsByLang(lang);
    }

    @Override
    public void flush() {
        repository.flush();
    }

    @Override
    public Optional<Product> findByIdWithPrices(Long id) {
        return repository.findByIdWithPrices(id);
    }
}