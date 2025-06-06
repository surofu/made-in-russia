package com.surofu.madeinrussia.core.repository.specification;

import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.view.ProductSummaryView;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSummarySpecifications {
    public static Specification<ProductSummaryView> hasDeliveryMethods(List<Long> deliveryMethodIds) {
        return (root, query, cb) -> {
            if (deliveryMethodIds == null || deliveryMethodIds.isEmpty()) {
                return cb.conjunction();
            }

            Join<ProductSummaryView, DeliveryMethod> deliveryMethodsJoin = root.join("deliveryMethods", JoinType.INNER);
            return deliveryMethodsJoin.get("id").in(deliveryMethodIds);
        };
    }

    public static Specification<ProductSummaryView> hasCategories(List<Long> categoryIds) {
        return (root, query, cb) -> {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return cb.conjunction();
            }

            return root.get("categoryId").in(categoryIds);
        };
    }

    public static Specification<ProductSummaryView> byTitle(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null || title.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(root.get("title"), "%" + title.trim() + "%");
        };
    }

    public static Specification<ProductSummaryView> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null) return null;

            List<Predicate> predicates = new ArrayList<>();
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("discountedPrice"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("discountedPrice"), maxPrice));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
