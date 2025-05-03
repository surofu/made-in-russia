package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.ProductDto;
import com.surofu.madeinrussia.application.query.GetProductByIdQuery;
import com.surofu.madeinrussia.application.query.GetProductsQuery;
import com.surofu.madeinrussia.core.model.product.Product;
import com.surofu.madeinrussia.core.repository.ProductRepository;
import com.surofu.madeinrussia.core.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductApplicationService implements ProductService {

    private final ProductRepository repository;

    @Override
    @Cacheable("productsPage")
    public Page<ProductDto> getProducts(GetProductsQuery query) {
        Page<Product> products = repository.findAllWithDeliveryMethodAsDto(query.getPageable());
        List<ProductDto> productDtos = new ArrayList<>(products.getTotalPages());

        for (Product product : products) {
            productDtos.add(ProductDto.of(product));
        }

        return new PageImpl<>(productDtos, query.getPageable(), products.getTotalElements());
    }

    @Override
    @Cacheable("product")
    public Optional<ProductDto> getProductById(GetProductByIdQuery query) {
        return repository.findById(query.getProductId()).map(ProductDto::of);
    }
}
