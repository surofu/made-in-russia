package com.surofu.madeinrussia.core.repository.specification;

import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.model.product.Product;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductReviewSpecifications {
    public static Specification<ProductReview> ratingBetween(Integer minRating, Integer maxRating) {
        return (root, query, cb) -> {
            if (minRating == null && maxRating == null) return null;

            List<Predicate> predicates = new ArrayList<>();
            if (minRating != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("rating").get("rating"), minRating));
            }
            if (maxRating != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("rating").get("rating"), maxRating));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
