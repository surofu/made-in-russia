package com.surofu.madeinrussia.core.repository.specification;

import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.view.ProductSummaryView;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSummarySpecifications {
    public static Specification<ProductSummaryView> hasDeliveryMethods(List<Long> deliveryMethodIds) {
        return (root, query, criteriaBuilder) -> {
            if (deliveryMethodIds == null || deliveryMethodIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            // Create expression to extract IDs from JSON array
            Expression<String> jsonArray = root.get("deliveryMethods");

            // Build predicates for each requested ID
            List<Predicate> predicates = new ArrayList<>();
            for (Long id : deliveryMethodIds) {
                predicates.add(
                        criteriaBuilder.isTrue(
                                criteriaBuilder.function(
                                        "jsonb_array_contains_id",
                                        Boolean.class,
                                        jsonArray,
                                        criteriaBuilder.literal(id)
                                )
                        )
                );
            }

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<ProductSummaryView> hasCategories(List<Long> categoryIds) {
        return (root, query, criteriaBuilder) -> {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.function("jsonb_extract_path_text", String.class,
                    root.get("category"),
                    criteriaBuilder.literal("id")
            ).in(categoryIds);
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

    public static Specification<ProductSummaryView> byUserId(Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    criteriaBuilder.function("jsonb_extract_path_text", String.class,
                            root.get("user"),
                            criteriaBuilder.literal("id")
                    )
                    , userId);
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
