package com.surofu.madeinrussia.core.repository.specification;

import com.surofu.madeinrussia.core.model.moderation.ApproveStatus;
import com.surofu.madeinrussia.core.model.user.UserRole;
import com.surofu.madeinrussia.core.view.ProductSummaryView;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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

            Expression<String> categoryIdExpression = criteriaBuilder.function(
                    "jsonb_extract_path_text",
                    String.class,
                    root.get("category"),
                    criteriaBuilder.literal("id")
            );

            List<String> categoryIdStrings = categoryIds.stream()
                    .map(String::valueOf)
                    .toList();

            return categoryIdExpression.in(categoryIdStrings);
        };
    }

    public static Specification<ProductSummaryView> byTitle(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null || title.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicateList = new ArrayList<>(2);

            predicateList.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("title")), "%" + title.toUpperCase() + "%"));
            predicateList.add(criteriaBuilder.like(criteriaBuilder.upper(criteriaBuilder.toString(root.get("titleTranslations"))), "%" + title.toUpperCase() + "%"));

            return criteriaBuilder.or(predicateList.toArray(new Predicate[0]));
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

    public static Specification<ProductSummaryView> byVendorId(Long vendorId) {
        return (root, query, criteriaBuilder) -> {
            if (vendorId == null) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(
                    criteriaBuilder.function("jsonb_extract_path_text", String.class,
                            root.get("user"),
                            criteriaBuilder.literal("id")
                    )
                    , vendorId));

            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.equal(
                            criteriaBuilder.function("jsonb_extract_path_text", String.class,
                                    root.get("user"),
                                    criteriaBuilder.literal("role")
                            )
                            , UserRole.ROLE_VENDOR.name()),
                    criteriaBuilder.equal(
                            criteriaBuilder.function("jsonb_extract_path_text", String.class,
                                    root.get("user"),
                                    criteriaBuilder.literal("role")
                            )
                            , UserRole.ROLE_ADMIN.name())
            ));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<ProductSummaryView> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) return null;

            List<Predicate> predicates = new ArrayList<>();
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("discountedPrice"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("discountedPrice"), maxPrice));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<ProductSummaryView> approveStatusIn(@NonNull List<ApproveStatus> statuses) {
        return approveStatusIn(statuses.toArray(new ApproveStatus[0]));
    }

    public static Specification<ProductSummaryView> approveStatusIn(@NonNull ApproveStatus ...statuses) {
        if (statuses.length == 0) {
            return ((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());
        }

        List<String> approveStatusNames = Arrays.stream(statuses).map(ApproveStatus::name).toList();
        return (root, query, cb) -> root.get("approveStatus").in(approveStatusNames);
    }
}
