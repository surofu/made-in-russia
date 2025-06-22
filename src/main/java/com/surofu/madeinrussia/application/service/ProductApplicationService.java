package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.command.product.CreateProductCharacteristicCommand;
import com.surofu.madeinrussia.application.command.product.CreateProductFaqCommand;
import com.surofu.madeinrussia.application.command.product.CreateProductPriceCommand;
import com.surofu.madeinrussia.application.dto.*;
import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.model.media.MediaType;
import com.surofu.madeinrussia.core.model.product.Product;
import com.surofu.madeinrussia.core.model.product.ProductPreviewImageUrl;
import com.surofu.madeinrussia.core.model.product.productCharacteristic.ProductCharacteristic;
import com.surofu.madeinrussia.core.model.product.productCharacteristic.ProductCharacteristicName;
import com.surofu.madeinrussia.core.model.product.productCharacteristic.ProductCharacteristicValue;
import com.surofu.madeinrussia.core.model.product.productFaq.ProductFaq;
import com.surofu.madeinrussia.core.model.product.productFaq.ProductFaqAnswer;
import com.surofu.madeinrussia.core.model.product.productFaq.ProductFaqQuestion;
import com.surofu.madeinrussia.core.model.product.productMedia.*;
import com.surofu.madeinrussia.core.model.product.productPrice.*;
import com.surofu.madeinrussia.core.model.product.productReview.productReviewMedia.ProductReviewMedia;
import com.surofu.madeinrussia.core.repository.*;
import com.surofu.madeinrussia.core.service.product.ProductService;
import com.surofu.madeinrussia.core.service.product.operation.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class ProductApplicationService implements ProductService {

    private final ProductRepository productRepository;
    private final ProductReviewMediaRepository productReviewMediaRepository;
    private final FileStorageRepository fileStorageRepository;
    private final CategoryRepository categoryRepository;
    private final DeliveryMethodRepository deliveryMethodRepository;

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

        if (productDto.isEmpty()) {
            return GetProductById.Result.notFound(operation.getProductId());
        }

        List<ProductReviewMedia> productReviewMedia = productReviewMediaRepository.findAllByProductId(operation.getProductId(), 10);
        List<ProductReviewMediaDto> productReviewMediaDtos = productReviewMedia.stream().map(ProductReviewMediaDto::of).toList();

        productDto.map(p -> {
            p.setReviewsMedia(productReviewMediaDtos);
            return p;
        });

        return GetProductById.Result.success(productDto.get());
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

    @Override
    @Transactional
    public CreateProduct.Result createProduct(CreateProduct operation) {

        Product product = new Product();
        product.setUser(operation.getSecurityUser().getUser());
        product.setTitle(operation.getProductTitle());
        product.setDescription(operation.getProductDescription());

        if (operation.getCategoryId() == null) {
            throw new IllegalArgumentException("Категория товара не может быть пустой");
        }

        Optional<Category> category = categoryRepository.getCategoryById(operation.getCategoryId());

        if (category.isEmpty()) {
            return CreateProduct.Result.categoryNotFound(operation.getCategoryId());
        }

        product.setCategory(category.get());

        if (operation.getDeliveryMethodIds() == null || operation.getDeliveryMethodIds().isEmpty()) {
            throw new IllegalArgumentException("Способы доставки товара не могут быть пустыми");
        }

        Set<DeliveryMethod> deliveryMethodSet = new HashSet<>();

        for (Long deliveryMethodId : operation.getDeliveryMethodIds()) {
            Optional<DeliveryMethod> deliveryMethod = deliveryMethodRepository.getDeliveryMethodById(deliveryMethodId);

            if (deliveryMethod.isEmpty()) {
                return CreateProduct.Result.deliveryMethodNotFound(deliveryMethodId);
            }

            deliveryMethodSet.add(deliveryMethod.get());
        }

        product.setDeliveryMethods(deliveryMethodSet);

        Set<ProductPrice> productPriceSet = new HashSet<>();

        for (CreateProductPriceCommand command : operation.getCreateProductPriceCommands()) {
            ProductPrice productPrice = new ProductPrice();
            productPrice.setProduct(product);
            productPrice.setOriginalPrice(ProductPriceOriginalPrice.of(command.price()));
            productPrice.setDiscount(ProductPriceDiscount.of(command.discount()));
            productPrice.setCurrency(ProductPriceCurrency.of(command.currency()));
            productPrice.setUnit(ProductPriceUnit.of(command.unit()));
            productPrice.setQuantityRange(ProductPriceQuantityRange.of(command.quantityFrom(), command.quantityTo()));
            productPrice.setMinimumOrderQuantity(ProductPriceMinimumOrderQuantity.of(command.minimumOrderQuantity()));
            productPrice.setExpirationDate(ProductPriceDiscountExpirationDate.of(command.discountExpirationDate()));
            productPriceSet.add(productPrice);
        }

        product.setPrices(productPriceSet);

        Set<ProductCharacteristic> productCharacteristicSet = new HashSet<>();

        for (CreateProductCharacteristicCommand command : operation.getCreateProductCharacteristicCommands()) {
            ProductCharacteristic productCharacteristic = new ProductCharacteristic();
            productCharacteristic.setProduct(product);
            productCharacteristic.setName(ProductCharacteristicName.of(command.name()));
            productCharacteristic.setValue(ProductCharacteristicValue.of(command.value()));
            productCharacteristicSet.add(productCharacteristic);
        }

        product.setCharacteristics(productCharacteristicSet);

        Set<ProductFaq> productFaqSet = new HashSet<>();

        for (CreateProductFaqCommand command : operation.getCreateProductFaqCommands()) {
            ProductFaq productFaq = new ProductFaq();
            productFaq.setProduct(product);
            productFaq.setQuestion(ProductFaqQuestion.of(command.question()));
            productFaq.setAnswer(ProductFaqAnswer.of(command.answer()));
            productFaqSet.add(productFaq);
        }

        product.setFaq(productFaqSet);
        product.setPreviewImageUrl(ProductPreviewImageUrl.of("Before saving media"));

        try {
            Set<ProductMedia> productMediaSet = new HashSet<>();

            for (int i = 0; i < operation.getFiles().size(); i++) {
                MultipartFile file = operation.getFiles().get(i);
                if (file.isEmpty()) {
                    log.warn("The file is empty. Skipping.");
                } else {
                    ProductMedia productMedia = new ProductMedia();
                    productMedia.setProduct(product);

                    productMedia.setMimeType(ProductMediaMimeType.of(file.getContentType()));
                    productMedia.setPosition(ProductMediaPosition.of(i));
                    productMedia.setAltText(ProductMediaAltText.of("Temp value"));

                    try {
                        String url = fileStorageRepository.upload(file);
                        productMedia.setUrl(ProductMediaUrl.of(url));

                        if (Objects.requireNonNull(file.getContentType()).startsWith("image")) {
                            productMedia.setMediaType(MediaType.IMAGE);
                        }

                        if (Objects.requireNonNull(file.getContentType()).startsWith("video")) {
                            productMedia.setMediaType(MediaType.VIDEO);
                        }

                        if (i == 0) {
                            product.setPreviewImageUrl(ProductPreviewImageUrl.of(url));
                        }
                    } catch (IOException e) {
                        log.error("Error saving media file: {}", e.getMessage(), e);
                        throw new FileUploadException("Failed to upload file", e);
                    }

                    productMediaSet.add(productMedia);
                }
            }

            product.setMedia(productMediaSet);
        } catch (Exception e) {
            log.error("Error uploading media files: {}", e.getMessage(), e);
            return CreateProduct.Result.errorSavingFiles();
        }

        try {
            productRepository.save(product);
        } catch (Exception e) {
            log.error("Error saving product: {}", e.getMessage(), e);
            return CreateProduct.Result.errorSavingProduct();
        }

        return CreateProduct.Result.success();
    }
}