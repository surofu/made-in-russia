package com.surofu.madeinrussia.core.service;

import com.surofu.madeinrussia.application.dto.ProductDto;
import com.surofu.madeinrussia.application.query.GetProductByIdQuery;
import com.surofu.madeinrussia.application.query.GetProductsQuery;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Page<ProductDto> getProducts(GetProductsQuery query);
    Optional<ProductDto> getProductById(GetProductByIdQuery query);
}
