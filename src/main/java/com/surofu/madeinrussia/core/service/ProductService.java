package com.surofu.madeinrussia.core.service;

import com.surofu.madeinrussia.application.dto.GetProductsDto;
import com.surofu.madeinrussia.application.dto.ProductDto;
import com.surofu.madeinrussia.application.query.GetProductByIdQuery;
import com.surofu.madeinrussia.application.query.GetProductsQuery;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    GetProductsDto getProducts(GetProductsQuery query);
    Optional<ProductDto> getProductById(GetProductByIdQuery query);
}
