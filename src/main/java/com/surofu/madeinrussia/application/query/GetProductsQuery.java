package com.surofu.madeinrussia.application.query;

import com.surofu.madeinrussia.core.model.product.Product;
import lombok.Data;
import lombok.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Data
@Value(staticConstructor = "of")
public class GetProductsQuery {
    Specification<Product> specification;
    Pageable pageable;
}
