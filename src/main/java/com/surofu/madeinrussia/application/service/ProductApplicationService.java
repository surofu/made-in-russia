package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.command.product.create.*;
import com.surofu.madeinrussia.application.command.product.update.*;
import com.surofu.madeinrussia.application.dto.*;
import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.model.media.MediaType;
import com.surofu.madeinrussia.core.model.product.*;
import com.surofu.madeinrussia.core.model.product.productCharacteristic.ProductCharacteristic;
import com.surofu.madeinrussia.core.model.product.productCharacteristic.ProductCharacteristicName;
import com.surofu.madeinrussia.core.model.product.productCharacteristic.ProductCharacteristicValue;
import com.surofu.madeinrussia.core.model.product.productDeliveryMethodDetails.ProductDeliveryMethodDetails;
import com.surofu.madeinrussia.core.model.product.productDeliveryMethodDetails.ProductDeliveryMethodDetailsName;
import com.surofu.madeinrussia.core.model.product.productDeliveryMethodDetails.ProductDeliveryMethodDetailsValue;
import com.surofu.madeinrussia.core.model.product.productFaq.ProductFaq;
import com.surofu.madeinrussia.core.model.product.productFaq.ProductFaqAnswer;
import com.surofu.madeinrussia.core.model.product.productFaq.ProductFaqQuestion;
import com.surofu.madeinrussia.core.model.product.productMedia.*;
import com.surofu.madeinrussia.core.model.product.productPackageOption.ProductPackageOption;
import com.surofu.madeinrussia.core.model.product.productPackageOption.ProductPackageOptionName;
import com.surofu.madeinrussia.core.model.product.productPackageOption.ProductPackageOptionPrice;
import com.surofu.madeinrussia.core.model.product.productPackageOption.ProductPackageOptionPriceUnit;
import com.surofu.madeinrussia.core.model.product.productPrice.*;
import com.surofu.madeinrussia.core.model.product.productVendorDetails.ProductVendorDetails;
import com.surofu.madeinrussia.core.model.product.productVendorDetails.ProductVendorDetailsDescription;
import com.surofu.madeinrussia.core.model.product.productVendorDetails.productVendorDetailsMedia.ProductVendorDetailsMedia;
import com.surofu.madeinrussia.core.model.product.productVendorDetails.productVendorDetailsMedia.ProductVendorDetailsMediaImage;
import com.surofu.madeinrussia.core.model.product.productVendorDetails.productVendorDetailsMedia.ProductVendorDetailsMediaPosition;
import com.surofu.madeinrussia.core.repository.*;
import com.surofu.madeinrussia.core.service.product.ProductService;
import com.surofu.madeinrussia.core.service.product.operation.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ProductApplicationService implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMediaRepository productMediaRepository;
    private final CategoryRepository categoryRepository;
    private final DeliveryMethodRepository deliveryMethodRepository;
    private final FileStorageRepository fileStorageRepository;

    private final String TEMP_URL = "TEMP_URL";

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "productById",
            key = "#operation.getProductId()",
            unless = "#result instanceof T(com.surofu.madeinrussia.core.service.product.operation.GetProductById$Result$NotFound)"
    )
    public GetProductById.Result getProductById(GetProductById operation) {
        LocalDateTime startTotal = LocalDateTime.now();

        LocalDateTime startGetProductById = LocalDateTime.now();
        Optional<Product> product = productRepository.getProductById(operation.getProductId());
        LocalDateTime endGetProductById = LocalDateTime.now();
        System.out.printf("productRepository.getProductById: %s ms\n", startGetProductById.until(endGetProductById, ChronoUnit.MILLIS));

        if (product.isEmpty()) {
            return GetProductById.Result.notFound(operation.getProductId());
        }

        LocalDateTime startGetMedia = LocalDateTime.now();
        List<ProductMedia> productMedia = productMediaRepository.findAllByProductId(operation.getProductId());
        LocalDateTime endGetMedia = LocalDateTime.now();
        System.out.printf("productMediaRepository.findAllByProductId: %s ms\n", startGetMedia.until(endGetMedia, ChronoUnit.MILLIS));

        product.get().setMedia(new HashSet<>(productMedia));

        LocalDateTime startGetRating = LocalDateTime.now();
        Double productRating = productRepository.getProductRating(operation.getProductId()).orElse(null);
        LocalDateTime endGetRating = LocalDateTime.now();
        System.out.printf("productRepository.getProductRating: %s : %s ms\n", productRating, startGetRating.until(endGetRating, ChronoUnit.MILLIS));

        product.get().setRating(productRating);

        LocalDateTime endTotal = LocalDateTime.now();
        System.out.printf("Total getProductById: %s ms\n", startTotal.until(endTotal, ChronoUnit.MILLIS));

        return GetProductById.Result.success(ProductDto.of(product.get()));
    }

    @Override
    @Transactional(readOnly = true)
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
    public GetProductDeliveryMethodsByProductId.Result getProductDeliveryMethodsByProductId(GetProductDeliveryMethodsByProductId operation) {
        Long productId = operation.getProductId();

        boolean productExists = productRepository.existsById(productId);

        if (!productExists) {
            return GetProductDeliveryMethodsByProductId.Result.notFound(productId);
        }

        List<DeliveryMethod> deliveryMethods = productRepository.getProductDeliveryMethodsByProductId(productId);
        List<DeliveryMethodDto> deliveryMethodDtos = deliveryMethods.stream().map(DeliveryMethodDto::of).toList();

        return GetProductDeliveryMethodsByProductId.Result.success(deliveryMethodDtos);
    }

    @Override
    @Transactional(readOnly = true)
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

        product.setMinimumOrderQuantity(ProductMinimumOrderQuantity.of(operation.getMinimumOrderQuantity()));
        product.setDiscountExpirationDate(ProductDiscountExpirationDate.of(operation.getDiscountExpirationDate()));

        Set<PreloadContentInfo<?>> preloadContentSet = new HashSet<>();

        /* ========== Product Delivery Methods ========== */

        Optional<Long> firstNotExistsDeliveryMethodId = deliveryMethodRepository.firstNotExists(operation.getDeliveryMethodIds());

        if (firstNotExistsDeliveryMethodId.isPresent()) {
            return CreateProduct.Result.deliveryMethodNotFound(firstNotExistsDeliveryMethodId.get());
        }

        List<DeliveryMethod> deliveryMethodList = deliveryMethodRepository.getAllDeliveryMethodsByIds(operation.getDeliveryMethodIds());

        product.setDeliveryMethods(new HashSet<>(deliveryMethodList));

        /* ========== Product Prices ========== */

        Set<ProductPrice> productPriceSet = new HashSet<>();

        for (CreateProductPriceCommand command : operation.getCreateProductPriceCommands()) {
            ProductPrice productPrice = new ProductPrice();
            productPrice.setProduct(product);
            productPrice.setOriginalPrice(ProductPriceOriginalPrice.of(command.price()));
            productPrice.setDiscount(ProductPriceDiscount.of(command.discount()));
            productPrice.setCurrency(ProductPriceCurrency.of(command.currency()));
            productPrice.setUnit(ProductPriceUnit.of(command.unit()));
            productPrice.setQuantityRange(ProductPriceQuantityRange.of(command.quantityFrom(), command.quantityTo()));
            productPriceSet.add(productPrice);
        }

        product.setPrices(productPriceSet);

        /* ========== Similar Products ========== */

        Optional<Long> firstNotExistsSimilarProductId = productRepository.firstNotExists(operation.getSimilarProductIds());

        if (firstNotExistsSimilarProductId.isPresent()) {
            return CreateProduct.Result.similarProductNotFound(firstNotExistsSimilarProductId.get());
        }

        List<Product> similarProducts = productRepository.findAllByIds(operation.getSimilarProductIds());
        product.setSimilarProducts(new HashSet<>(similarProducts));

        /* ========== Product Characteristics ========== */

        Set<ProductCharacteristic> productCharacteristicSet = new HashSet<>();

        for (CreateProductCharacteristicCommand command : operation.getCreateProductCharacteristicCommands()) {
            ProductCharacteristic productCharacteristic = new ProductCharacteristic();
            productCharacteristic.setProduct(product);
            productCharacteristic.setName(ProductCharacteristicName.of(command.name()));
            productCharacteristic.setValue(ProductCharacteristicValue.of(command.value()));
            productCharacteristicSet.add(productCharacteristic);
        }

        product.setCharacteristics(productCharacteristicSet);

        /* ========== Product Faq ========== */

        Set<ProductFaq> productFaqSet = new HashSet<>();

        for (CreateProductFaqCommand command : operation.getCreateProductFaqCommands()) {
            ProductFaq productFaq = new ProductFaq();
            productFaq.setProduct(product);
            productFaq.setQuestion(ProductFaqQuestion.of(command.question()));
            productFaq.setAnswer(ProductFaqAnswer.of(command.answer()));
            productFaqSet.add(productFaq);
        }

        product.setFaq(productFaqSet);

        /* ========== Product Delivery Method Details ========== */

        Set<ProductDeliveryMethodDetails> productDeliveryMethodDetailsSet = new HashSet<>();

        for (CreateProductDeliveryMethodDetailsCommand command : operation.getCreateProductDeliveryMethodDetailsCommands()) {
            ProductDeliveryMethodDetails productDeliveryMethodDetails = new ProductDeliveryMethodDetails();
            productDeliveryMethodDetails.setProduct(product);
            productDeliveryMethodDetails.setName(ProductDeliveryMethodDetailsName.of(command.name()));
            productDeliveryMethodDetails.setValue(ProductDeliveryMethodDetailsValue.of(command.value()));
            productDeliveryMethodDetailsSet.add(productDeliveryMethodDetails);
        }

        product.setDeliveryMethodDetails(productDeliveryMethodDetailsSet);

        /* ========== Product Package Options ========== */

        Set<ProductPackageOption> productPackageOptionSet = new HashSet<>();

        for (CreateProductPackageOptionCommand command : operation.getCreateProductPackageOptionCommands()) {
            ProductPackageOption productPackageOption = new ProductPackageOption();
            productPackageOption.setProduct(product);
            productPackageOption.setName(ProductPackageOptionName.of(command.name()));
            productPackageOption.setPrice(ProductPackageOptionPrice.of(command.price()));
            productPackageOption.setPriceUnit(ProductPackageOptionPriceUnit.of(command.priceUnit()));
            productPackageOptionSet.add(productPackageOption);
        }

        product.setPackageOptions(productPackageOptionSet);

        /* ========== Product Media ========== */

        Set<ProductMedia> productMediaSet = new HashSet<>();

        for (int i = 0; i < operation.getProductMedia().size(); i++) {
            MultipartFile file = operation.getProductMedia().get(i);

            if (file.isEmpty()) {
                return CreateProduct.Result.emptyFile();
            }

            ProductMedia productMedia = new ProductMedia();
            productMedia.setProduct(product);

            productMedia.setMimeType(ProductMediaMimeType.of(file.getContentType()));
            productMedia.setPosition(ProductMediaPosition.of(i));
            productMedia.setAltText(ProductMediaAltText.of("Temp value"));

            if (file.getContentType() == null ||
                    (!file.getContentType().startsWith("image") &&
                            !file.getContentType().startsWith("video"))) {
                return CreateProduct.Result.invalidMediaType(file.getContentType());
            }

            productMedia.setUrl(ProductMediaUrl.of(UUID.randomUUID().toString()));

            if (file.getContentType().startsWith("image")) {
                productMedia.setMediaType(MediaType.IMAGE);
                preloadContentSet.add(new PreloadContentInfo<>(productMedia, file, "productImages"));
            } else if (file.getContentType().startsWith("video")) {
                productMedia.setMediaType(MediaType.VIDEO);
                preloadContentSet.add(new PreloadContentInfo<>(productMedia, file, "productVideos"));
            }

            if (i == 0) {
                product.setPreviewImageUrl(ProductPreviewImageUrl.of(TEMP_URL));
            }

            productMediaSet.add(productMedia);
        }

        product.setMedia(productMediaSet);

        /* ========== Product Vendor Details ========== */

        if (operation.getCreateProductVendorDetailsCommand() != null) {
            ProductVendorDetails productVendorDetails = new ProductVendorDetails();
            productVendorDetails.setProduct(product);
            productVendorDetails.setDescription(ProductVendorDetailsDescription.of(
                    operation.getCreateProductVendorDetailsCommand().mainDescription(),
                    operation.getCreateProductVendorDetailsCommand().furtherDescription()
            ));

            /* ========== Product Vendor Details Media ========== */

            Set<ProductVendorDetailsMedia> productVendorDetailsMediaSet = new HashSet<>();

            for (int i = 0; i < operation.getProductVendorDetailsMedia().size(); i++) {
                MultipartFile file = operation.getProductVendorDetailsMedia().get(i);

                if (file.isEmpty()) {
                    return CreateProduct.Result.emptyFile();
                }

                ProductVendorDetailsMedia productVendorDetailsMedia = new ProductVendorDetailsMedia();
                productVendorDetailsMedia.setProductVendorDetails(productVendorDetails);
                productVendorDetailsMedia.setPosition(ProductVendorDetailsMediaPosition.of(i));

                if (file.getContentType() == null ||
                        (!file.getContentType().startsWith("image") &&
                                !file.getContentType().startsWith("video"))) {
                    return CreateProduct.Result.invalidMediaType(Objects.requireNonNull(file.getContentType()));
                }

                String altText = Objects.requireNonNullElse(operation.getCreateProductVendorDetailsCommand().mediaAltTexts().get(i), "");
                productVendorDetailsMedia.setImage(ProductVendorDetailsMediaImage.of(UUID.randomUUID().toString(), altText));

                if (file.getContentType().startsWith("image")) {
                    productVendorDetailsMedia.setMediaType(MediaType.IMAGE);
                    preloadContentSet.add(new PreloadContentInfo<>(productVendorDetailsMedia, file, "productVendorDetailsImages"));
                } else if (file.getContentType().startsWith("video")) {
                    productVendorDetailsMedia.setMediaType(MediaType.VIDEO);
                    preloadContentSet.add(new PreloadContentInfo<>(productVendorDetailsMedia, file, "productVendorDetailsVideos"));
                }

                productVendorDetailsMediaSet.add(productVendorDetailsMedia);
            }

            productVendorDetails.setMedia(productVendorDetailsMediaSet);
            product.setProductVendorDetails(productVendorDetails);
        }

        try {
            productRepository.save(product);
        } catch (Exception e) {
            log.error("Error saving product: {}", e.getMessage(), e);
            return CreateProduct.Result.errorSavingProduct();
        }

        try {
            for (PreloadContentInfo<?> contentInfo : preloadContentSet) {
                if (contentInfo.entity() instanceof ProductMedia) {
                    ProductMedia productMedia = product.getMedia().stream()
                            .filter(m -> m.getUrl().toString().equals(((ProductMedia) contentInfo.entity()).getUrl().toString()))
                            .findFirst().orElseThrow();

                    if (productMedia.getMediaType().equals(MediaType.IMAGE)) {
                        String url = fileStorageRepository.uploadImageToFolder(contentInfo.file(), contentInfo.folderName());
                        productMedia.setUrl(ProductMediaUrl.of(url));

                        if (product.getPreviewImageUrl().getValue().equals(TEMP_URL)) {
                            product.setPreviewImageUrl(ProductPreviewImageUrl.of(url));
                        }
                    }

                    if (productMedia.getMediaType().equals(MediaType.VIDEO)) {
                        String url = fileStorageRepository.uploadVideoToFolder(contentInfo.file(), contentInfo.folderName());
                        productMedia.setUrl(ProductMediaUrl.of(url));

                        if (product.getPreviewImageUrl().getValue().equals(TEMP_URL)) {
                            product.setPreviewImageUrl(ProductPreviewImageUrl.of(url));
                        }
                    }
                }

                if (contentInfo.entity() instanceof ProductVendorDetailsMedia) {
                    ProductVendorDetailsMedia productVendorDetailsMedia = product.getProductVendorDetails().getMedia().stream()
                            .filter(m -> m.getImage().getUrl().equals(((ProductVendorDetailsMedia) contentInfo.entity()).getImage().getUrl()))
                            .findFirst().orElseThrow();

                    if (productVendorDetailsMedia.getMediaType().equals(MediaType.IMAGE)) {
                        String url = fileStorageRepository.uploadImageToFolder(contentInfo.file(), contentInfo.folderName());
                        productVendorDetailsMedia.setImage(ProductVendorDetailsMediaImage.of(
                                url,
                                productVendorDetailsMedia.getImage().getAltText()
                        ));
                    }

                    if (productVendorDetailsMedia.getMediaType().equals(MediaType.VIDEO)) {
                        String url = fileStorageRepository.uploadVideoToFolder(contentInfo.file(), contentInfo.folderName());
                        productVendorDetailsMedia.setImage(ProductVendorDetailsMediaImage.of(
                                url,
                                productVendorDetailsMedia.getImage().getAltText()
                        ));
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error saving files: {}", e.getMessage(), e);
            return CreateProduct.Result.errorSavingFiles();
        }

        try {
            productRepository.save(product);
        } catch (Exception e) {
            log.error("Error saving product after uploading files: {}", e.getMessage(), e);
            return CreateProduct.Result.errorSavingProduct();
        }

        return CreateProduct.Result.success();
    }

    @Override
    @Transactional
    @CacheEvict(value = "productById", key = "#operation.getProductId()")
    public UpdateProduct.Result updateProduct(UpdateProduct operation) {
        Optional<Product> optionalProduct = productRepository.getProductById(operation.getProductId());

        if (optionalProduct.isEmpty()) {
            return UpdateProduct.Result.productNotFound(operation.getProductId());
        }

        Product product = optionalProduct.get();

        if (!product.getUser().getId().equals(operation.getSecurityUser().getUser().getId())) {
            return UpdateProduct.Result.invalidOwner(operation.getProductId(), operation.getSecurityUser().getUser().getLogin());
        }

        product.setTitle(operation.getProductTitle());
        product.setMinimumOrderQuantity(ProductMinimumOrderQuantity.of(operation.getMinimumOrderQuantity()));
        product.setDiscountExpirationDate(ProductDiscountExpirationDate.of(operation.getDiscountExpirationDate()));

        Set<PreloadContentInfo<?>> preloadContentSet = new HashSet<>();

        /* ========== Product Description ========== */

        product.setDescription(ProductDescription.of(
                operation.getProductDescription().getMainDescription(),
                operation.getProductDescription().getFurtherDescription()
        ));

        /* ========== Product Category ========== */

        Category productCategory = product.getCategory();

        if (!productCategory.getId().equals(operation.getCategoryId())) {
            Optional<Category> newCategory = categoryRepository.getCategoryById(operation.getCategoryId());

            if (newCategory.isEmpty()) {
                return UpdateProduct.Result.categoryNotFound(operation.getCategoryId());
            }

            product.setCategory(newCategory.get());
        }

        /* ========== Product Delivery Methods ========== */

        Set<DeliveryMethod> productDeliveryMethodList = product.getDeliveryMethods();
        Set<Long> productDeliveryMethodIdsSet = productDeliveryMethodList.stream().map(DeliveryMethod::getId).collect(Collectors.toSet());

        if (
                !(productDeliveryMethodIdsSet.containsAll(operation.getDeliveryMethodIds()) &&
                        new HashSet<>(operation.getDeliveryMethodIds()).containsAll(productDeliveryMethodIdsSet))
        ) {
            Optional<Long> firstNotExistsDeliveryMethodId = deliveryMethodRepository.firstNotExists(operation.getDeliveryMethodIds());

            if (firstNotExistsDeliveryMethodId.isPresent()) {
                return UpdateProduct.Result.deliveryMethodNotFound(firstNotExistsDeliveryMethodId.get());
            }

            List<DeliveryMethod> deliveryMethodList = deliveryMethodRepository.getAllDeliveryMethodsByIds(operation.getDeliveryMethodIds());
            product.setDeliveryMethods(new HashSet<>(deliveryMethodList));
        }

        /* ========== Similar Products ========== */

        Set<Long> similarProductIdSet = product.getSimilarProducts().stream().map(Product::getId).collect(Collectors.toSet());

        if (
                !(similarProductIdSet.containsAll(operation.getSimilarProductIds()) &&
                        new HashSet<>(operation.getSimilarProductIds()).containsAll(similarProductIdSet))
        ) {
            Optional<Long> firstNotExistsSimilarProductId = productRepository.firstNotExists(operation.getSimilarProductIds());

            if (firstNotExistsSimilarProductId.isPresent()) {
                return UpdateProduct.Result.similarProductNotFound(firstNotExistsSimilarProductId.get());
            }

            List<Product> similarProductList = productRepository.findAllByIds(operation.getSimilarProductIds());
            product.setSimilarProducts(new HashSet<>(similarProductList));
        }

        /* ========== Product Prices ========== */

        Set<ProductPrice> productPriceSet = new HashSet<>();

        for (UpdateProductPriceCommand command : operation.getUpdateProductPriceCommands()) {
            ProductPrice productPrice = new ProductPrice();
            productPrice.setProduct(product);
            productPrice.setOriginalPrice(ProductPriceOriginalPrice.of(command.price()));
            productPrice.setDiscount(ProductPriceDiscount.of(command.discount()));
            productPrice.setCurrency(ProductPriceCurrency.of(command.currency()));
            productPrice.setUnit(ProductPriceUnit.of(command.unit()));
            productPrice.setQuantityRange(ProductPriceQuantityRange.of(command.quantityFrom(), command.quantityTo()));
            productPriceSet.add(productPrice);
        }

        product.getPrices().clear();
        product.getPrices().addAll(productPriceSet);

        /* ========== Product Characteristics ========== */

        Set<ProductCharacteristic> productCharacteristicSet = new HashSet<>();

        for (UpdateProductCharacteristicCommand command : operation.getUpdateProductCharacteristicCommands()) {
            ProductCharacteristic productCharacteristic = new ProductCharacteristic();
            productCharacteristic.setProduct(product);
            productCharacteristic.setName(ProductCharacteristicName.of(command.name()));
            productCharacteristic.setValue(ProductCharacteristicValue.of(command.value()));
            productCharacteristicSet.add(productCharacteristic);
        }

        product.getCharacteristics().clear();
        product.getCharacteristics().addAll(productCharacteristicSet);

        /* ========== Product Faq ========== */

        Set<ProductFaq> productFaqSet = new HashSet<>();

        for (UpdateProductFaqCommand command : operation.getUpdateProductFaqCommands()) {
            ProductFaq productFaq = new ProductFaq();
            productFaq.setProduct(product);
            productFaq.setQuestion(ProductFaqQuestion.of(command.question()));
            productFaq.setAnswer(ProductFaqAnswer.of(command.answer()));
            productFaqSet.add(productFaq);
        }

        product.getFaq().clear();
        product.getFaq().addAll(productFaqSet);

        /* ========== Product Delivery Method Details ========== */

        Set<ProductDeliveryMethodDetails> productDeliveryMethodDetailsSet = new HashSet<>();

        for (UpdateProductDeliveryMethodDetailsCommand command : operation.getUpdateProductDeliveryMethodDetailsCommands()) {
            ProductDeliveryMethodDetails productDeliveryMethodDetails = new ProductDeliveryMethodDetails();
            productDeliveryMethodDetails.setProduct(product);
            productDeliveryMethodDetails.setName(ProductDeliveryMethodDetailsName.of(command.name()));
            productDeliveryMethodDetails.setValue(ProductDeliveryMethodDetailsValue.of(command.value()));
            productDeliveryMethodDetailsSet.add(productDeliveryMethodDetails);
        }

        product.getDeliveryMethodDetails().clear();
        product.getDeliveryMethodDetails().addAll(productDeliveryMethodDetailsSet);

        /* ========== Product Package Options ========== */

        Set<ProductPackageOption> productPackageOptionSet = new HashSet<>();

        for (UpdateProductPackageOptionCommand command : operation.getUpdateProductPackageOptionCommands()) {
            ProductPackageOption productPackageOption = new ProductPackageOption();
            productPackageOption.setProduct(product);
            productPackageOption.setName(ProductPackageOptionName.of(command.name()));
            productPackageOption.setPrice(ProductPackageOptionPrice.of(command.price()));
            productPackageOption.setPriceUnit(ProductPackageOptionPriceUnit.of(command.priceUnit()));
            productPackageOptionSet.add(productPackageOption);
        }

        product.getPackageOptions().clear();
        product.getPackageOptions().addAll(productPackageOptionSet);

        /* ========== Product Media ========== */

        List<Long> oldProductMediaIdList = operation.getOldProductMedia().stream().map(UpdateOldMediaDto::id).toList();
        Set<ProductMedia> allOldProductMediaList = product.getMedia();
        List<ProductMedia> newOldProductMediaList = allOldProductMediaList.stream().filter(m -> oldProductMediaIdList.contains(m.getId())).toList();
        List<ProductMedia> oldProductMediaToDeleteList = allOldProductMediaList.stream().filter(m -> !oldProductMediaIdList.contains(m.getId())).toList();
        Set<Object> mediaForDeleteSet = new HashSet<>(oldProductMediaToDeleteList);

        List<ProductMedia> productMediaList = new ArrayList<>();
        List<MultipartFile> productMediaFileSet = operation.getProductMedia();

        for (int i = 0, j = 0; i < newOldProductMediaList.size() + operation.getProductMedia().size(); i++) {
            ProductMedia oldProductMedia = i >= newOldProductMediaList.size() ? null : newOldProductMediaList.get(i);

            if (oldProductMedia == null) {
                ProductMedia newProductMedia = new ProductMedia();
                newProductMedia.setProduct(product);
                newProductMedia.setPosition(ProductMediaPosition.of(i));
                newProductMedia.setAltText(ProductMediaAltText.of(Objects.requireNonNullElse(operation.getMediaAltTexts().get(j), "")));
                MultipartFile file = productMediaFileSet.get(j);
                newProductMedia.setMimeType(ProductMediaMimeType.of(file.getContentType()));

                if (file.getContentType() == null ||
                        (!file.getContentType().startsWith("image") &&
                                !file.getContentType().startsWith("video"))) {
                    return UpdateProduct.Result.invalidMediaType(file.getContentType());
                }

                newProductMedia.setUrl(ProductMediaUrl.of(UUID.randomUUID().toString()));

                if (file.getContentType().startsWith("image")) {
                    newProductMedia.setMediaType(MediaType.IMAGE);
                    preloadContentSet.add(new PreloadContentInfo<>(newProductMedia, file, "productImages"));
                } else if (file.getContentType().startsWith("video")) {
                    newProductMedia.setMediaType(MediaType.VIDEO);
                    preloadContentSet.add(new PreloadContentInfo<>(newProductMedia, file, "productVideos"));
                }

                if (i == 0) {
                    product.setPreviewImageUrl(ProductPreviewImageUrl.of(TEMP_URL));
                }

                productMediaList.add(i, newProductMedia);

                j++;
            } else {
                productMediaList.add(i, oldProductMedia);
            }
        }

        product.getMedia().clear();
        product.getMedia().addAll(new HashSet<>(productMediaList));

        product.getProductVendorDetails().setDescription(ProductVendorDetailsDescription.of(
                operation.getUpdateProductVendorDetailsCommand().mainDescription(),
                operation.getUpdateProductVendorDetailsCommand().furtherDescription()
        ));

        Set<Long> oldProductVendorDetailsMediaIdSet = operation.getOldVendorDetailsMedia().stream().map(UpdateOldMediaDto::id).collect(Collectors.toSet());
        List<ProductVendorDetailsMedia> oldProductVendorDetailsMediaSet = product.getProductVendorDetails().getMedia()
                .stream().filter(m -> oldProductVendorDetailsMediaIdSet.contains(m.getId())).toList();

        List<ProductVendorDetailsMedia> productVendorDetailsMediaForDeleteSet = product.getProductVendorDetails().getMedia()
                .stream().filter(m -> !oldProductVendorDetailsMediaIdSet.contains(m.getId())).toList();
        mediaForDeleteSet.addAll(productVendorDetailsMediaForDeleteSet);

        List<ProductVendorDetailsMedia> productVendorDetailsMediaList = new ArrayList<>();

        for (int i = 0, j = 0; i < oldProductVendorDetailsMediaSet.size() + operation.getProductVendorDetailsMedia().size(); i++) {
            ProductVendorDetailsMedia oldProductVendorDetailsMedia = i >= oldProductVendorDetailsMediaSet.size() ? null : oldProductVendorDetailsMediaSet.get(i);

            if (oldProductVendorDetailsMedia == null) {
                ProductVendorDetailsMedia newProductVendorDetailsMedia = new ProductVendorDetailsMedia();
                newProductVendorDetailsMedia.setProductVendorDetails(product.getProductVendorDetails());
                newProductVendorDetailsMedia.setPosition(ProductVendorDetailsMediaPosition.of(i));

                MultipartFile file = operation.getProductVendorDetailsMedia().get(j);

                if (file.getContentType() == null ||
                        (!file.getContentType().startsWith("image") &&
                                !file.getContentType().startsWith("video"))) {
                    return UpdateProduct.Result.invalidMediaType(file.getContentType());
                }

                String altText = Objects.requireNonNullElse(operation.getUpdateProductVendorDetailsCommand().mediaAltTexts().get(j), "");
                newProductVendorDetailsMedia.setImage(ProductVendorDetailsMediaImage.of(UUID.randomUUID().toString(), altText));

                if (file.getContentType().startsWith("image")) {
                    newProductVendorDetailsMedia.setMediaType(MediaType.IMAGE);
                    preloadContentSet.add(new PreloadContentInfo<>(newProductVendorDetailsMedia, file, "productVendorDetailsImages"));
                } else if (file.getContentType().startsWith("video")) {
                    newProductVendorDetailsMedia.setMediaType(MediaType.VIDEO);
                    preloadContentSet.add(new PreloadContentInfo<>(newProductVendorDetailsMedia, file, "productVendorDetailsVideos"));
                }

                productVendorDetailsMediaList.add(i, newProductVendorDetailsMedia);

                j++;
            } else {
                productVendorDetailsMediaList.add(i, oldProductVendorDetailsMedia);
            }
        }

        product.getProductVendorDetails().getMedia().clear();
        product.getProductVendorDetails().getMedia().addAll(new HashSet<>(productVendorDetailsMediaList));

        try {
            productRepository.save(product);
        } catch (Exception e) {
            log.error("Error saving product: {}", e.getMessage(), e);
            return UpdateProduct.Result.errorSavingProduct();
        }

        try {
            for (PreloadContentInfo<?> contentInfo : preloadContentSet) {
                if (contentInfo.entity() instanceof ProductMedia) {
                    ProductMedia productMedia = product.getMedia().stream()
                            .filter(m -> m.getUrl().toString().equals(((ProductMedia) contentInfo.entity()).getUrl().toString()))
                            .findFirst().orElseThrow();

                    if (productMedia.getMediaType().equals(MediaType.IMAGE)) {
                        String url = fileStorageRepository.uploadImageToFolder(contentInfo.file(), contentInfo.folderName());
                        productMedia.setUrl(ProductMediaUrl.of(url));

                        if (product.getPreviewImageUrl().getValue().equals(TEMP_URL)) {
                            product.setPreviewImageUrl(ProductPreviewImageUrl.of(url));
                        }
                    }

                    if (productMedia.getMediaType().equals(MediaType.VIDEO)) {
                        String url = fileStorageRepository.uploadVideoToFolder(contentInfo.file(), contentInfo.folderName());
                        productMedia.setUrl(ProductMediaUrl.of(url));

                        if (product.getPreviewImageUrl().getValue().equals(TEMP_URL)) {
                            product.setPreviewImageUrl(ProductPreviewImageUrl.of(url));
                        }
                    }
                }

                if (contentInfo.entity() instanceof ProductVendorDetailsMedia) {
                    ProductVendorDetailsMedia productVendorDetailsMedia = product.getProductVendorDetails().getMedia().stream()
                            .filter(m -> m.getImage().getUrl().equals(((ProductVendorDetailsMedia) contentInfo.entity()).getImage().getUrl()))
                            .findFirst().orElseThrow();

                    if (productVendorDetailsMedia.getMediaType().equals(MediaType.IMAGE)) {
                        String url = fileStorageRepository.uploadImageToFolder(contentInfo.file(), contentInfo.folderName());
                        productVendorDetailsMedia.setImage(ProductVendorDetailsMediaImage.of(
                                url,
                                productVendorDetailsMedia.getImage().getAltText()
                        ));
                    }

                    if (productVendorDetailsMedia.getMediaType().equals(MediaType.VIDEO)) {
                        String url = fileStorageRepository.uploadVideoToFolder(contentInfo.file(), contentInfo.folderName());
                        productVendorDetailsMedia.setImage(ProductVendorDetailsMediaImage.of(
                                url,
                                productVendorDetailsMedia.getImage().getAltText()
                        ));
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error saving files: {}", e.getMessage(), e);
            return UpdateProduct.Result.errorSavingFiles();
        }

        try {
            List<String> linksForDelete = new ArrayList<>();

            for (Object entity : mediaForDeleteSet) {
                if (entity instanceof ProductMedia productMedia) {
                    linksForDelete.add(productMedia.getUrl().toString());
                }

                if (entity instanceof ProductVendorDetailsMedia productVendorDetailsMedia) {
                    linksForDelete.add(productVendorDetailsMedia.getImage().getUrl());
                }
            }

            fileStorageRepository.deleteAllMediaByLink(linksForDelete);
        } catch (Exception e) {
            log.error("Error deleting files: {}", e.getMessage(), e);
            return UpdateProduct.Result.errorDeletingFiles();
        }

        try {
            productRepository.save(product);
        } catch (Exception e) {
            log.error("Error saving product after uploading files: {}", e.getMessage(), e);
            return UpdateProduct.Result.errorSavingProduct();
        }

        return UpdateProduct.Result.success();
    }

    private record PreloadContentInfo<T>(
            T entity,
            MultipartFile file,
            String folderName
    ) {
    }
}