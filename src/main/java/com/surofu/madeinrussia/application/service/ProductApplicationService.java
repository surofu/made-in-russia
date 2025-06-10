package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.*;
import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.model.product.Product;
import com.surofu.madeinrussia.core.model.product.productCharacteristic.ProductCharacteristic;
import com.surofu.madeinrussia.core.model.product.productFaq.ProductFaq;
import com.surofu.madeinrussia.core.model.product.productMedia.ProductMedia;
import com.surofu.madeinrussia.core.repository.ProductRepository;
import com.surofu.madeinrussia.core.service.product.ProductService;
import com.surofu.madeinrussia.core.service.product.operation.*;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductApplicationService implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "productById",
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

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "productCategoryByProductId",
            key = "#operation.getProductId()",
            unless = "#result instanceof T(com.surofu.madeinrussia.core.service.product.operation.GetProductCategoryByProductId$Result$NotFound)"
    )
    public GetProductCategoryByProductId.Result getProductCategoryByProductId(GetProductCategoryByProductId operation) {
        Long productId = operation.getProductId();
        Optional<Category> category = productRepository.getProductCategoryByProductId(productId);
        Optional<CategoryDto> categoryDto = category.map(CategoryDto::of);

        if (categoryDto.isPresent()) {
            return GetProductCategoryByProductId.Result.success(categoryDto.get());
        }

        return GetProductCategoryByProductId.Result.notFound(productId);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "productDeliveryMethodsByProductId",
            key = "#operation.getProductId()",
            unless = """
                    {
                    #result instanceof T(com.surofu.madeinrussia.core.service.product.operation.GetProductDeliveryMethodsByProductId$Result$NotFound)
                    or #result.getProductDeliveryMethodDtos().isEmpty()
                    }
                    """
    )
    public GetProductDeliveryMethodsByProductId.Result getProductDeliveryMethodsByProductId(GetProductDeliveryMethodsByProductId operation) {
        Long productId = operation.getProductId();

        Optional<List<DeliveryMethod>> deliveryMethods = productRepository.getProductDeliveryMethodsByProductId(productId);
        Optional<List<DeliveryMethodDto>> deliveryMethodDtos = deliveryMethods.map(list -> list.stream().map(DeliveryMethodDto::of).toList());

        if (deliveryMethodDtos.isPresent()) {
            return GetProductDeliveryMethodsByProductId.Result.success(deliveryMethodDtos.get());
        }

        return GetProductDeliveryMethodsByProductId.Result.notFound(productId);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "productMediaByProductId",
            key = "#operation.getProductId()",
            unless = """
                    {
                    #result instanceof T(com.surofu.madeinrussia.core.service.product.operation.GetProductMediaByProductId$Result$NotFound)
                    or #result.getProductMediaDtos().isEmpty()
                    }
                    """
    )
    public GetProductMediaByProductId.Result getProductMediaByProductId(GetProductMediaByProductId operation) {
        Long productId = operation.getProductId();
        Optional<List<ProductMedia>> productMedia = productRepository.getProductMediaByProductId(productId);
        Optional<List<ProductMediaDto>> productMediaDtos = productMedia.map(list -> list.stream().map(ProductMediaDto::of).toList());

        if (productMediaDtos.isPresent()) {
            return GetProductMediaByProductId.Result.success(productMediaDtos.get());
        }

        return GetProductMediaByProductId.Result.notFound(productId);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "productCharacteristicsByProductId",
            key = "#operation.getProductId()",
            unless = """
                    {
                    #result instanceof T(com.surofu.madeinrussia.core.service.product.operation.GetProductCharacteristicsByProductId$Result$NotFound)
                    or #result.getProductCharacteristicDtos().isEmpty()
                    }
                    """
    )
    public GetProductCharacteristicsByProductId.Result getProductCharacteristicsByProductId(GetProductCharacteristicsByProductId operation) {
        Long productId = operation.getProductId();
        Optional<List<ProductCharacteristic>> productCharacteristics = productRepository.getProductCharacteristicsByProductId(productId);
        Optional<List<ProductCharacteristicDto>> productCharacteristicDtos = productCharacteristics.map(list -> list.stream().map(ProductCharacteristicDto::of).toList());
        if (productCharacteristicDtos.isPresent()) {
            return GetProductCharacteristicsByProductId.Result.success(productCharacteristicDtos.get());
        }

        return GetProductCharacteristicsByProductId.Result.notFound(productId);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "productFaqByProductId",
            key = "#operation.getProductId()",
            unless = """
                    {
                    #result instanceof T(com.surofu.madeinrussia.core.service.product.operation.GetProductFaqByProductId)
                    or #result.getProductFaqDtos().isEmpty()
                    }
                    """
    )
    public GetProductFaqByProductId.Result getProductFaqByProductId(GetProductFaqByProductId operation) {
        Long productId = operation.getProductId();

        Optional<List<ProductFaq>> productFaq = productRepository.getProductFaqByProductId(productId);
        Optional<List<ProductFaqDto>> productFaqDtos = productFaq.map(list -> list.stream().map(ProductFaqDto::of).toList());

        if (productFaqDtos.isPresent()) {
            return GetProductFaqByProductId.Result.success(productFaqDtos.get());
        }

        return GetProductFaqByProductId.Result.notFound(productId);
    }
}