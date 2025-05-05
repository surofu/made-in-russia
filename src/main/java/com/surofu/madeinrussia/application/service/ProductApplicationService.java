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

/**
 * Application service implementation for product operations.
 * Handles business logic and acts as a bridge between controllers and domain layer.
 */
@Service
@AllArgsConstructor
public class ProductApplicationService implements ProductService {

    private final ProductRepository repository;

    /**
     * Retrieves paginated and filtered list of products
     * @param operation The operation containing query parameters and pagination info
     * @return Result containing paginated product DTOs
     * @apiNote Results are cached using a composite key of all filter parameters
     */
    @Override
    @Cacheable(
            value = "productsPage",
            key = """
            {
             #operation.query.page(), #operation.query.size(),
             #operation.query.deliveryMethodIds()?.hashCode(),
             #operation.query.categoryIds()?.hashCode(),
             #operation.query.minPrice(), #operation.query.maxPrice()
             }
            """,
            unless = "#result == null"
    )
    public GetProducts.Result getProducts(GetProducts operation) {
        Pageable pageable = PageRequest.of(operation.getQuery().page(), operation.getQuery().size());

        Specification<Product> specification = Specification
                .where(ProductSpecifications.hasDeliveryMethods(operation.getQuery().deliveryMethodIds()))
                .and(ProductSpecifications.hasCategories(operation.getQuery().categoryIds()))
                .and(ProductSpecifications.priceBetween(operation.getQuery().minPrice(), operation.getQuery().maxPrice()));

        Page<Product> products = repository.findAll(specification, pageable);
        List<ProductDto> productDtos = new ArrayList<>(products.getSize());

        for (Product product : products) {
            productDtos.add(ProductDto.of(product));
        }

        Page<ProductDto> productDtoPage = new PageImpl<>(productDtos, pageable, products.getTotalElements());

        return GetProducts.Result.success(productDtoPage);
    }

    /**
     * Retrieves a single product by its unique identifier
     * @param operation The operation containing the product ID to lookup
     * @return Result containing either the found product or not-found status
     * @apiNote Results are cached by product ID
     */
    @Override
    @Cacheable(
            value = "product",
            key = "#operation.query.productId()",
            unless = "#result == null"
    )
    public GetProductById.Result getProductById(GetProductById operation) {
        Optional<Product> product = repository.findById(operation.getQuery().productId());
        Optional<ProductDto> productDto = product.map(ProductDto::of);

        if (productDto.isPresent()) {
            return GetProductById.Result.success(productDto.get());
        }

        return GetProductById.Result.notFound(operation.getQuery().productId());
    }
}