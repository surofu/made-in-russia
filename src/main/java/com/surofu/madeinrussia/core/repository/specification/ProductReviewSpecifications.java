package com.surofu.madeinrussia.core.repository.specification;

import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

public class ProductReviewSpecifications {
    public static Specification<ProductReview> byProductId(@Param("productId") Long productId) {
        return (root, query, criteriaBuilder) -> {
            if (productId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("product").get("id"), productId);
        };
    }

    public static Specification<ProductReview> byUserId(@Param("userId") Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("user").get("id"), userId);
        };
    }

    public static Specification<ProductReview> ratingBetween(@Param("minRating") Integer minRating, @Param("maxRating") Integer maxRating) {
        return (root, query, criteriaBuilder) -> {
            if (minRating == null && maxRating == null) return null;

            List<Predicate> predicates = new ArrayList<>();
            if (minRating != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("rating").get("rating"), minRating));
            }
            if (maxRating != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("rating").get("rating"), maxRating));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
