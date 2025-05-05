package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.ProductDto;
import com.surofu.madeinrussia.core.model.product.Product;
import com.surofu.madeinrussia.core.repository.ProductRepository;
import com.surofu.madeinrussia.core.repository.specification.ProductSpecifications;
import com.surofu.madeinrussia.core.service.product.ProductService;
import com.surofu.madeinrussia.core.service.product.operation.GetProductById;
import com.surofu.madeinrussia.core.service.product.operation.GetProducts;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    public GetProducts.Result getProducts(GetProducts operation) {
        Pageable pageable = PageRequest.of(operation.getQuery().page(), operation.getQuery().size());

        Specification<Product> specification = Specification
                .where(ProductSpecifications.hasCategories(operation.getQuery().categoryIds()))
                .and(ProductSpecifications.priceBetween(operation.getQuery().minPrice(), operation.getQuery().maxPrice()));

        Page<Product> products = repository.findAll(specification, pageable);
        List<ProductDto> productDtos = new ArrayList<>(products.getTotalPages());

        for (Product product : products) {
            productDtos.add(ProductDto.of(product));
        }

        Page<ProductDto> productDtoPage = new PageImpl<>(productDtos, pageable, products.getTotalElements());

        return GetProducts.Result.success(productDtoPage);
    }

    @Override
    @Cacheable("product")
    public GetProductById.Result getProductById(GetProductById operation) {
        Optional<Product> product = repository.findById(operation.getQuery().productId());
        Optional<ProductDto> productDto = product.map(ProductDto::of);

        if (productDto.isPresent()) {
            return GetProductById.Result.success(productDto.get());
        }

        return GetProductById.Result.notFound();
    }
}
