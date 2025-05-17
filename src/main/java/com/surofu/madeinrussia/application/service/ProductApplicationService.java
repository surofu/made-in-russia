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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductApplicationService implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "productsPage",
            key = """
                    {
                     #operation.getPage(), #operation.getSize(),
                     #operation.getDeliveryMethodIds()?.hashCode(),
                     #operation.getCategoryIds()?.hashCode(),
                     #operation.getMinPrice(), #operation.getMaxPrice()
                     }
                    """,
            unless = "#result.getProductDtoPage().isEmpty()"
    )
    public GetProducts.Result getProducts(GetProducts operation) {
        Pageable pageable = PageRequest.of(operation.getPage(), operation.getSize());

        Specification<Product> specification = Specification
                .where(ProductSpecifications.hasDeliveryMethods(operation.getDeliveryMethodIds()))
                .and(ProductSpecifications.hasCategories(operation.getCategoryIds()))
                .and(ProductSpecifications.priceBetween(operation.getMinPrice(), operation.getMaxPrice()));

        Page<Product> productPage = productRepository.getAllProductsWithCategoryAndDeliveryMethods(specification, pageable);
        Page<ProductDto> productDtoPage = productPage.map(ProductDto::of);

        return GetProducts.Result.success(productDtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "product",
            key = "#operation.getProductId()",
            unless = "#result instanceof T(com.surofu.madeinrussia.core.service.product.operation.GetProductById$Result$NotFound)"
    )
    public GetProductById.Result getProductById(GetProductById operation) {
        Optional<Product> product = productRepository.getProductById(operation.getProductId());
        Optional<ProductDto> productDto = product.map(ProductDto::of);

        if (productDto.isPresent()) {
            return GetProductById.Result.success(productDto.get());
        }

        return GetProductById.Result.notFound(operation.getProductId());
    }
}