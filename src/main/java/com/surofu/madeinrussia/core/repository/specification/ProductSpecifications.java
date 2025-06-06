package com.surofu.madeinrussia.core.repository.specification;

import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.model.product.Product;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ProductSpecifications {

    public static Specification<Product> byTitle(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null || title.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(root.get("title").get("value"), "%" + title.trim() + "%");
        };
    }

    public static Specification<Product> byUserId(Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("user").get("id"), userId);
        };
    };

    public static Specification<Product> hasDeliveryMethods(List<Long> deliveryMethodIds) {
        return (root, query, cb) -> {
            if (deliveryMethodIds == null || deliveryMethodIds.isEmpty()) {
                return cb.conjunction();
            }

            Join<Product, DeliveryMethod> deliveryMethodsJoin = root.join("deliveryMethods", JoinType.INNER);

            if (deliveryMethodsJoin.get("id") == null) {
                return cb.conjunction();
            }

            return deliveryMethodsJoin.get("id").in(deliveryMethodIds);
        };
    }

    public static Specification<Product> hasCategories(List<Long> categoryIds) {
        return (root, query, cb) -> {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return cb.conjunction();
            }

            if (root.get("category").get("id") == null) {
                return cb.conjunction();
            }

            return root.get("category").get("id").in(categoryIds);
        };
    }
}
