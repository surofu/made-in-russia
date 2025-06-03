package com.surofu.madeinrussia.core.repository.specification;

import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.model.product.Product;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecifications {
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

    public static Specification<Product> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price").get("discountedPrice"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price").get("discountedPrice"), maxPrice));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
