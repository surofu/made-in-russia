package com.surofu.madeinrussia.core.repository.specification;

import com.surofu.madeinrussia.core.model.product.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecifications {
    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, cb) ->
                categoryId != null ?
                        cb.equal(root.get("category").get("id"), categoryId) :
                        null;
    }

    public static Specification<Product> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null) return null;

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
