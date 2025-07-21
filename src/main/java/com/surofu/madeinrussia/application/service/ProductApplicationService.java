package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.command.product.create.*;
import com.surofu.madeinrussia.application.command.product.update.*;
import com.surofu.madeinrussia.application.dto.*;
import com.surofu.madeinrussia.application.exception.EmptyTranslationException;
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
import com.surofu.madeinrussia.infrastructure.persistence.category.CategoryView;
import com.surofu.madeinrussia.infrastructure.persistence.deliveryMethod.DeliveryMethodView;
import com.surofu.madeinrussia.infrastructure.persistence.product.ProductView;
import com.surofu.madeinrussia.infrastructure.persistence.product.SearchHintView;
import com.surofu.madeinrussia.infrastructure.persistence.product.SimilarProductView;
import com.surofu.madeinrussia.infrastructure.persistence.product.characteristic.ProductCharacteristicView;
import com.surofu.madeinrussia.infrastructure.persistence.product.deliveryMethodDetails.ProductDeliveryMethodDetailsView;
import com.surofu.madeinrussia.infrastructure.persistence.product.faq.ProductFaqView;
import com.surofu.madeinrussia.infrastructure.persistence.product.media.ProductMediaView;
import com.surofu.madeinrussia.infrastructure.persistence.product.packageOption.ProductPackageOptionView;
import com.surofu.madeinrussia.infrastructure.persistence.product.price.ProductPriceView;
import com.surofu.madeinrussia.infrastructure.persistence.product.productVendorDetails.ProductVendorDetailsView;
import com.surofu.madeinrussia.infrastructure.persistence.product.productVendorDetails.productVendorDetailsMedia.ProductVendorDetailsMediaView;
import com.surofu.madeinrussia.infrastructure.persistence.product.review.media.ProductReviewMediaView;
import com.surofu.madeinrussia.infrastructure.persistence.user.UserView;
import com.surofu.madeinrussia.infrastructure.persistence.vendor.country.VendorCountryView;
import com.surofu.madeinrussia.infrastructure.persistence.vendor.faq.VendorFaqView;
import com.surofu.madeinrussia.infrastructure.persistence.vendor.productCategory.VendorProductCategoryView;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ProductApplicationService implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final VendorCountryRepository vendorCountryRepository;
    private final VendorProductCategoryRepository vendorProductCategoryRepository;
    private final VendorFaqRepository vendorFaqRepository;
    private final ProductMediaRepository productMediaRepository;
    private final CategoryRepository categoryRepository;
    private final DeliveryMethodRepository deliveryMethodRepository;
    private final ProductCharacteristicRepository productCharacteristicRepository;
    private final ProductFaqRepository productFaqRepository;
    private final ProductPriceRepository productPriceRepository;
    private final ProductReviewMediaRepository productReviewMediaRepository;
    private final ProductVendorDetailsRepository productVendorDetailsRepository;
    private final ProductVendorDetailsMediaRepository productVendorDetailsMediaRepository;
    private final ProductDeliveryMethodDetailsRepository productDeliveryMethodDetailsRepository;
    private final ProductPackageOptionsRepository productPackageOptionsRepository;
    private final FileStorageRepository fileStorageRepository;
    private final TranslationRepository translationRepository;

    private final String TEMP_URL = "TEMP_URL";

    @Override
    @Transactional(readOnly = true)
    public GetProductById.Result getProductById(GetProductById operation) {
        Optional<ProductView> productView = productRepository.getProductViewByIdAndLang(
                operation.getProductId(), operation.getLocale().getLanguage()
        );

        if (productView.isEmpty()) {
            return GetProductById.Result.notFound(operation.getProductId());
        }

        ProductDto productDto = ProductDto.of(productView.get());
        ProductDto fullProductDto = loadFullProduct(productDto, productView.get(), operation.getLocale());

        return GetProductById.Result.success(fullProductDto);
    }

    @Override
    @Transactional(readOnly = true)
    public GetProductByArticle.Result getProductByArticle(GetProductByArticle operation) {
        Optional<ProductView> productView = productRepository.getProductViewByArticleAndLang(
                operation.getArticleCode().toString(), operation.getLocale().getLanguage());

        if (productView.isEmpty()) {
            return GetProductByArticle.Result.notFound(operation.getArticleCode());
        }
        ProductDto productDto = ProductDto.of(productView.get());

        ProductDto fullProductDto = loadFullProduct(productDto, productView.get(), operation.getLocale());

        return GetProductByArticle.Result.success(fullProductDto);
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

    // TODO: Refactor Create Product
    @Override
    @Transactional
    public CreateProduct.Result createProduct(CreateProduct operation) {
        Product product = new Product();
        product.setUser(operation.getSecurityUser().getUser());

        Map<String, HstoreTranslationDto> translationMap = new HashMap<>();
        Set<PreloadContentInfo<?>> preloadContentSet = new HashSet<>();

        /* ========== Product Title ========== */
        product.setTitle(operation.getProductTitle());
        translationMap.put(TranslationKeys.TITLE.name(), operation.getProductTitle().getTranslations());

        /* ========== Product Description ========== */
        product.setDescription(operation.getProductDescription());
        translationMap.put(TranslationKeys.MAIN_DESCRIPTION.name(), operation.getProductDescription().getMainDescriptionTranslations());
        translationMap.put(TranslationKeys.FURTHER_DESCRIPTION.name(), operation.getProductDescription().getFurtherDescriptionTranslations());

        /* ========== Product Discount ========== */
        product.setMinimumOrderQuantity(ProductMinimumOrderQuantity.of(operation.getMinimumOrderQuantity()));
        product.setDiscountExpirationDate(ProductDiscountExpirationDate.of(operation.getDiscountExpirationDate()));

        /* ========== Product Category ========== */
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
            ProductPrice productPrice = createProductPrice(command, product);
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
        for (int i = 0; i < operation.getCreateProductCharacteristicCommands().size(); i++) {
            CreateProductCharacteristicCommand command = operation.getCreateProductCharacteristicCommands().get(i);
            translationMap.put(TranslationKeys.CHARACTERISTIC_NAME.with(i), HstoreTranslationDto.of(command.nameTranslations()));
            translationMap.put(TranslationKeys.CHARACTERISTIC_VALUE.with(i), HstoreTranslationDto.of(command.valueTranslations()));
        }

        /* ========== Product Faq ========== */
        for (int i = 0; i < operation.getCreateProductFaqCommands().size(); i++) {
            CreateProductFaqCommand command = operation.getCreateProductFaqCommands().get(i);
            translationMap.put(TranslationKeys.FAQ_QUESTION.with(i), HstoreTranslationDto.of(command.questionTranslations()));
            translationMap.put(TranslationKeys.FAQ_ANSWER.with(i), HstoreTranslationDto.of(command.answerTranslations()));
        }

        /* ========== Product Delivery Method Details ========== */
        for (int i = 0; i < operation.getCreateProductDeliveryMethodDetailsCommands().size(); i++) {
            CreateProductDeliveryMethodDetailsCommand command = operation.getCreateProductDeliveryMethodDetailsCommands().get(i);
            translationMap.put(TranslationKeys.DELIVERY_METHOD_DETAILS_NAME.with(i), HstoreTranslationDto.of(command.nameTranslations()));
            translationMap.put(TranslationKeys.DELIVERY_METHOD_DETAILS_VALUE.with(i), HstoreTranslationDto.of(command.valueTranslations()));
        }

        /* ========== Product Package Options ========== */
        for (int i = 0; i < operation.getCreateProductPackageOptionCommands().size(); i++) {
            CreateProductPackageOptionCommand command = operation.getCreateProductPackageOptionCommands().get(i);
            translationMap.put(TranslationKeys.PACKAGE_OPTIONS_NAME.with(i), HstoreTranslationDto.of(command.nameTranslations()));
        }

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

            translationMap.put(TranslationKeys.VENDOR_DETAILS_MAIN_DESCRIPTION.name(),
                    HstoreTranslationDto.of(operation.getCreateProductVendorDetailsCommand().mainDescriptionTranslations()));
            translationMap.put(TranslationKeys.VENDOR_DETAILS_FURTHER_DESCRIPTION.name(),
                    HstoreTranslationDto.of(operation.getCreateProductVendorDetailsCommand().furtherDescriptionTranslations()));

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

        /* ========== Translation ========== */
        Map<String, HstoreTranslationDto> resultMap;

        try {
            resultMap = translationRepository.expend(translationMap);
        } catch (EmptyTranslationException e) {
            return CreateProduct.Result.emptyTranslation(e.getMessage());
        } catch (Exception e) {
            return CreateProduct.Result.translationError(e);
        }

        // Title
        HstoreTranslationDto translatedTitle = resultMap.get(TranslationKeys.TITLE.name());
        product.getTitle().setTranslations(translatedTitle);

        // Description
        HstoreTranslationDto translatedMainDescription = resultMap.get(TranslationKeys.MAIN_DESCRIPTION.name());
        HstoreTranslationDto translatedFurtherDescription = resultMap.get(TranslationKeys.FURTHER_DESCRIPTION.name());
        product.getDescription().setMainDescriptionTranslations(translatedMainDescription);
        product.getDescription().setFurtherDescriptionTranslations(translatedFurtherDescription);

        // Characteristics
        Set<ProductCharacteristic> productCharacteristicSet = new HashSet<>(
                operation.getCreateProductCharacteristicCommands().size()
        );

        for (int i = 0; i < operation.getCreateProductCharacteristicCommands().size(); i++) {
            CreateProductCharacteristicCommand command = operation.getCreateProductCharacteristicCommands().get(i);
            ProductCharacteristic productCharacteristic = createProductCharacteristic(command, product);
            HstoreTranslationDto translatedCharacteristicName = resultMap.get(TranslationKeys.CHARACTERISTIC_NAME.with(i));
            HstoreTranslationDto translatedCharacteristicValue = resultMap.get(TranslationKeys.CHARACTERISTIC_VALUE.with(i));
            productCharacteristic.getName().setTranslations(translatedCharacteristicName);
            productCharacteristic.getValue().setTranslations(translatedCharacteristicValue);
            productCharacteristicSet.add(productCharacteristic);
        }

        product.setCharacteristics(productCharacteristicSet);

        // Faq
        Set<ProductFaq> productFaqSet = new HashSet<>(
                operation.getCreateProductFaqCommands().size()
        );

        for (int i = 0; i < operation.getCreateProductFaqCommands().size(); i++) {
            CreateProductFaqCommand command = operation.getCreateProductFaqCommands().get(i);
            ProductFaq productFaq = createProductFaq(command, product);
            HstoreTranslationDto translatedFaqQuestion = resultMap.get(TranslationKeys.FAQ_QUESTION.with(i));
            HstoreTranslationDto translatedFaqAnswer = resultMap.get(TranslationKeys.FAQ_ANSWER.with(i));
            productFaq.getQuestion().setTranslations(translatedFaqQuestion);
            productFaq.getAnswer().setTranslations(translatedFaqAnswer);
            productFaqSet.add(productFaq);
        }

        product.setFaq(productFaqSet);

        // Delivery Method Details
        Set<ProductDeliveryMethodDetails> productDeliveryMethodDetailsSet = new HashSet<>(
                operation.getCreateProductDeliveryMethodDetailsCommands().size()
        );

        for (int i = 0; i < operation.getCreateProductDeliveryMethodDetailsCommands().size(); i++) {
            CreateProductDeliveryMethodDetailsCommand command = operation.getCreateProductDeliveryMethodDetailsCommands().get(i);
            ProductDeliveryMethodDetails productDeliveryMethodDetails = createProductDeliveryMethodDetails(command, product);
            HstoreTranslationDto translatedDeliveryMethodDetailsName = resultMap.get(TranslationKeys.DELIVERY_METHOD_DETAILS_NAME.with(i));
            HstoreTranslationDto translatedDeliveryMethodDetailsValue = resultMap.get(TranslationKeys.DELIVERY_METHOD_DETAILS_VALUE.with(i));
            productDeliveryMethodDetails.getName().setTranslations(translatedDeliveryMethodDetailsName);
            productDeliveryMethodDetails.getValue().setTranslations(translatedDeliveryMethodDetailsValue);
            productDeliveryMethodDetailsSet.add(productDeliveryMethodDetails);
        }

        product.setDeliveryMethodDetails(productDeliveryMethodDetailsSet);

        // Package Options
        Set<ProductPackageOption> productPackageOptionSet = new HashSet<>(
                operation.getCreateProductPackageOptionCommands().size()
        );

        for (int i = 0; i < operation.getCreateProductPackageOptionCommands().size(); i++) {
            CreateProductPackageOptionCommand command = operation.getCreateProductPackageOptionCommands().get(i);
            ProductPackageOption productPackageOption = createProductPackageOption(command, product);
            HstoreTranslationDto translatedPackageOptionName = resultMap.get(TranslationKeys.PACKAGE_OPTIONS_NAME.with(i));
            productPackageOption.getName().setTranslations(translatedPackageOptionName);
            productPackageOptionSet.add(productPackageOption);
        }

        product.setPackageOptions(productPackageOptionSet);

        // Vendor Details
        HstoreTranslationDto translatedVendorDetailsMainDescription = resultMap.get(TranslationKeys.VENDOR_DETAILS_MAIN_DESCRIPTION.name());
        HstoreTranslationDto translatedVendorDetailsFurtherDescription = resultMap.get(TranslationKeys.VENDOR_DETAILS_FURTHER_DESCRIPTION.name());
        product.getProductVendorDetails().getDescription().setMainDescriptionTranslations(translatedVendorDetailsMainDescription);
        product.getProductVendorDetails().getDescription().setFurtherDescriptionTranslations(translatedVendorDetailsFurtherDescription);

        /* ========== Save Data ========== */
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
    public UpdateProduct.Result updateProduct(UpdateProduct operation) {
        Optional<Product> existingProduct = productRepository.getProductById(operation.getProductId());

        if (existingProduct.isEmpty()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.productNotFound(operation.getProductId());
        }

        Product product = existingProduct.get();

        if (!product.getUser().getId().equals(operation.getSecurityUser().getUser().getId())) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.invalidOwner(operation.getProductId(), operation.getSecurityUser().getUser().getLogin());
        }

        Map<String, HstoreTranslationDto> translationMap = new HashMap<>();
        Set<PreloadContentInfo<?>> preloadContentSet = new HashSet<>();

        /* ========== Product Title ========== */
        product.setTitle(operation.getProductTitle());
        translationMap.put(TranslationKeys.TITLE.name(), operation.getProductTitle().getTranslations());

        /* ========== Product Description ========== */
        product.setDescription(ProductDescription.of(
                operation.getProductDescription().getMainDescription(),
                operation.getProductDescription().getFurtherDescription()
        ));

        translationMap.put(TranslationKeys.MAIN_DESCRIPTION.name(), operation.getProductDescription().getMainDescriptionTranslations());
        translationMap.put(TranslationKeys.FURTHER_DESCRIPTION.name(), operation.getProductDescription().getFurtherDescriptionTranslations());

        /* ========== Product Discount ========== */
        product.setMinimumOrderQuantity(ProductMinimumOrderQuantity.of(operation.getMinimumOrderQuantity()));
        product.setDiscountExpirationDate(ProductDiscountExpirationDate.of(operation.getDiscountExpirationDate()));

        /* ========== Product Category ========== */
        Category productCategory = product.getCategory();

        if (!productCategory.getId().equals(operation.getCategoryId())) {
            Optional<Category> newCategory = categoryRepository.getCategoryById(operation.getCategoryId());

            if (newCategory.isEmpty()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
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
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
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
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return UpdateProduct.Result.similarProductNotFound(firstNotExistsSimilarProductId.get());
            }

            List<Product> similarProductList = productRepository.findAllByIds(operation.getSimilarProductIds());
            product.setSimilarProducts(new HashSet<>(similarProductList));
        }

        /* ========== Product Prices ========== */
        Set<ProductPrice> productPriceSet = new HashSet<>();

        for (UpdateProductPriceCommand command : operation.getUpdateProductPriceCommands()) {
            ProductPrice productPrice = createProductPrice(command, product);
            productPriceSet.add(productPrice);
        }

        product.getPrices().clear();
        product.getPrices().addAll(productPriceSet);

        /* ========== Product Characteristics ========== */
        for (int i = 0; i < operation.getUpdateProductCharacteristicCommands().size(); i++) {
            UpdateProductCharacteristicCommand command = operation.getUpdateProductCharacteristicCommands().get(i);
            translationMap.put(TranslationKeys.CHARACTERISTIC_NAME.with(i), HstoreTranslationDto.of(command.nameTranslations()));
            translationMap.put(TranslationKeys.CHARACTERISTIC_VALUE.with(i), HstoreTranslationDto.of(command.valueTranslations()));
        }

        /* ========== Product Faq ========== */
        for (int i = 0; i < operation.getUpdateProductFaqCommands().size(); i++) {
            UpdateProductFaqCommand command = operation.getUpdateProductFaqCommands().get(i);
            translationMap.put(TranslationKeys.FAQ_QUESTION.with(i), HstoreTranslationDto.of(command.questionTranslations()));
            translationMap.put(TranslationKeys.FAQ_ANSWER.with(i), HstoreTranslationDto.of(command.answerTranslations()));
        }

        /* ========== Product Delivery Method Details ========== */
        for (int i = 0; i < operation.getUpdateProductDeliveryMethodDetailsCommands().size(); i++) {
            UpdateProductDeliveryMethodDetailsCommand command = operation.getUpdateProductDeliveryMethodDetailsCommands().get(i);
            translationMap.put(TranslationKeys.DELIVERY_METHOD_DETAILS_NAME.with(i), HstoreTranslationDto.of(command.nameTranslations()));
            translationMap.put(TranslationKeys.DELIVERY_METHOD_DETAILS_VALUE.with(i), HstoreTranslationDto.of(command.valueTranslations()));
        }

        /* ========== Product Package Options ========== */
        for (int i = 0; i < operation.getUpdateProductPackageOptionCommands().size(); i++) {
            UpdateProductPackageOptionCommand command = operation.getUpdateProductPackageOptionCommands().get(i);
            translationMap.put(TranslationKeys.PACKAGE_OPTIONS_NAME.with(i), HstoreTranslationDto.of(command.nameTranslations()));
        }

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

        /* ========== Product Vendor Details ========== */
        product.getProductVendorDetails().setDescription(ProductVendorDetailsDescription.of(
                operation.getUpdateProductVendorDetailsCommand().mainDescription(),
                operation.getUpdateProductVendorDetailsCommand().furtherDescription()
        ));

        translationMap.put(TranslationKeys.VENDOR_DETAILS_MAIN_DESCRIPTION.name(),
                HstoreTranslationDto.of(operation.getUpdateProductVendorDetailsCommand().mainDescriptionTranslations()));
        translationMap.put(TranslationKeys.VENDOR_DETAILS_FURTHER_DESCRIPTION.name(),
                HstoreTranslationDto.of(operation.getUpdateProductVendorDetailsCommand().furtherDescriptionTranslations()));

        // Vendor Details Media
        Set<Long> oldProductVendorDetailsMediaIdSet = operation.getOldVendorDetailsMedia().stream().map(UpdateOldMediaDto::id).collect(Collectors.toSet());
        List<ProductVendorDetailsMedia> oldProductVendorDetailsMediaSet = product.getProductVendorDetails().getMedia()
                .stream().filter(m -> oldProductVendorDetailsMediaIdSet.contains(m.getId())).toList();
        List<ProductVendorDetailsMedia> productVendorDetailsMediaForDeleteSet = product.getProductVendorDetails().getMedia()
                .stream().filter(m -> !oldProductVendorDetailsMediaIdSet.contains(m.getId())).toList();
        mediaForDeleteSet.addAll(productVendorDetailsMediaForDeleteSet);

        List<ProductVendorDetailsMedia> productVendorDetailsMediaList = new ArrayList<>();

        for (int i = 0, j = 0; i < oldProductVendorDetailsMediaSet.size() + operation.getProductVendorDetailsMedia().size(); i++) {
            ProductVendorDetailsMedia oldProductVendorDetailsMedia = i >= oldProductVendorDetailsMediaSet.size()
                    ? null
                    : oldProductVendorDetailsMediaSet.get(i);

            if (oldProductVendorDetailsMedia == null) {
                ProductVendorDetailsMedia newProductVendorDetailsMedia = new ProductVendorDetailsMedia();
                newProductVendorDetailsMedia.setProductVendorDetails(product.getProductVendorDetails());
                newProductVendorDetailsMedia.setPosition(ProductVendorDetailsMediaPosition.of(i));

                MultipartFile file = operation.getProductVendorDetailsMedia().get(j);

                if (file.getContentType() == null ||
                        (!file.getContentType().startsWith("image") &&
                                !file.getContentType().startsWith("video"))) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return UpdateProduct.Result.invalidMediaType(file.getContentType());
                }

                String altText = Objects.requireNonNullElse(
                        operation.getUpdateProductVendorDetailsCommand().mediaAltTexts().get(j), "");
                newProductVendorDetailsMedia.setImage(
                        ProductVendorDetailsMediaImage.of(UUID.randomUUID().toString(), altText));

                if (file.getContentType().startsWith("image")) {
                    newProductVendorDetailsMedia.setMediaType(MediaType.IMAGE);
                    preloadContentSet.add(new PreloadContentInfo<>(
                            newProductVendorDetailsMedia, file, "productVendorDetailsImages"));
                } else if (file.getContentType().startsWith("video")) {
                    newProductVendorDetailsMedia.setMediaType(MediaType.VIDEO);
                    preloadContentSet.add(new PreloadContentInfo<>(
                            newProductVendorDetailsMedia, file, "productVendorDetailsVideos"));
                }

                productVendorDetailsMediaList.add(i, newProductVendorDetailsMedia);

                j++;
            } else {
                productVendorDetailsMediaList.add(i, oldProductVendorDetailsMedia);
            }
        }

        product.getProductVendorDetails().getMedia().clear();
        product.getProductVendorDetails().getMedia().addAll(new HashSet<>(productVendorDetailsMediaList));

        /* ========== Translation ========== */
        Map<String, HstoreTranslationDto> resultMap;

        try {
            resultMap = translationRepository.expend(translationMap);
        } catch (EmptyTranslationException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.emptyTranslations(e.getMessage());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.translationError(e);
        }

        // Title
        HstoreTranslationDto translatedTitle = resultMap.get(TranslationKeys.TITLE.name());
        product.getTitle().setTranslations(translatedTitle);

        // Description
        HstoreTranslationDto translatedMainDescription = resultMap.get(TranslationKeys.MAIN_DESCRIPTION.name());
        HstoreTranslationDto translatedFurtherDescription = resultMap.get(TranslationKeys.FURTHER_DESCRIPTION.name());
        product.getDescription().setMainDescriptionTranslations(translatedMainDescription);
        product.getDescription().setFurtherDescriptionTranslations(translatedFurtherDescription);

        // Characteristics
        Set<ProductCharacteristic> productCharacteristicSet = new HashSet<>(
                operation.getUpdateProductCharacteristicCommands().size());

        for (int i = 0; i < operation.getUpdateProductCharacteristicCommands().size(); i++) {
            UpdateProductCharacteristicCommand command = operation.getUpdateProductCharacteristicCommands().get(i);
            ProductCharacteristic productCharacteristic = createProductCharacteristic(command, product);
            HstoreTranslationDto translationName = resultMap.get(TranslationKeys.CHARACTERISTIC_NAME.with(i));
            HstoreTranslationDto translationValue = resultMap.get(TranslationKeys.CHARACTERISTIC_VALUE.with(i));
            productCharacteristic.getName().setTranslations(translationName);
            productCharacteristic.getValue().setTranslations(translationValue);
            productCharacteristicSet.add(productCharacteristic);
        }

        product.getCharacteristics().clear();
        product.getCharacteristics().addAll(productCharacteristicSet);

        // Faq
        Set<ProductFaq> productFaqSet = new HashSet<>(operation.getUpdateProductFaqCommands().size());

        for (int i = 0; i < operation.getUpdateProductFaqCommands().size(); i++) {
            UpdateProductFaqCommand command = operation.getUpdateProductFaqCommands().get(i);
            ProductFaq productFaq = createProductFaq(command, product);
            HstoreTranslationDto translationQuestion = resultMap.get(TranslationKeys.FAQ_QUESTION.with(i));
            HstoreTranslationDto translationAnswer = resultMap.get(TranslationKeys.FAQ_ANSWER.with(i));
            productFaq.getQuestion().setTranslations(translationQuestion);
            productFaq.getAnswer().setTranslations(translationAnswer);
            productFaqSet.add(productFaq);
        }

        product.getFaq().clear();
        product.getFaq().addAll(productFaqSet);

        // Delivery Method Details
        Set<ProductDeliveryMethodDetails> productDeliveryMethodDetailsSet = new HashSet<>(
                operation.getUpdateProductDeliveryMethodDetailsCommands().size()
        );

        for (int i = 0; i < operation.getUpdateProductDeliveryMethodDetailsCommands().size(); i++) {
            UpdateProductDeliveryMethodDetailsCommand command = operation.getUpdateProductDeliveryMethodDetailsCommands().get(i);
            ProductDeliveryMethodDetails productDeliveryMethodDetails = createProductDeliveryMethodDetails(command, product);
            HstoreTranslationDto translationName = resultMap.get(TranslationKeys.DELIVERY_METHOD_DETAILS_NAME.with(i));
            HstoreTranslationDto translationValue = resultMap.get(TranslationKeys.DELIVERY_METHOD_DETAILS_VALUE.with(i));
            productDeliveryMethodDetails.getName().setTranslations(translationName);
            productDeliveryMethodDetails.getValue().setTranslations(translationValue);
            productDeliveryMethodDetailsSet.add(productDeliveryMethodDetails);
        }

        product.getDeliveryMethodDetails().clear();
        product.getDeliveryMethodDetails().addAll(productDeliveryMethodDetailsSet);

        // Package Options
        Set<ProductPackageOption> productPackageOptionSet = new HashSet<>(operation.getUpdateProductPackageOptionCommands().size());

        for (int i = 0; i < operation.getUpdateProductPackageOptionCommands().size(); i++) {
            UpdateProductPackageOptionCommand command = operation.getUpdateProductPackageOptionCommands().get(i);
            ProductPackageOption productPackageOption = createProductPackageOption(command, product);
            HstoreTranslationDto translationName = resultMap.get(TranslationKeys.PACKAGE_OPTIONS_NAME.with(i));
            productPackageOption.getName().setTranslations(translationName);
            productPackageOptionSet.add(productPackageOption);
        }

        product.getPackageOptions().clear();
        product.getPackageOptions().addAll(productPackageOptionSet);

        // Vendor Details
        HstoreTranslationDto translatedVendorDetailsMainDescription = resultMap.get(TranslationKeys.VENDOR_DETAILS_MAIN_DESCRIPTION.name());
        HstoreTranslationDto translatedVendorDetailsFurtherDescription = resultMap.get(TranslationKeys.VENDOR_DETAILS_FURTHER_DESCRIPTION.name());
        product.getProductVendorDetails().getDescription().setMainDescriptionTranslations(translatedVendorDetailsMainDescription);
        product.getProductVendorDetails().getDescription().setFurtherDescriptionTranslations(translatedVendorDetailsFurtherDescription);

        /* ========== Save Data ========== */
        try {
            productRepository.save(product);
        } catch (Exception e) {
            log.error("Error saving product: {}", e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
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
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
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
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.errorDeletingFiles();
        }

        try {
            productRepository.save(product);
        } catch (Exception e) {
            log.error("Error saving product after uploading files: {}", e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.errorSavingProduct();
        }

        return UpdateProduct.Result.success();
    }

    @Override
    public GetSearchHints.Result getSearchHints(GetSearchHints operation) {
        List<SearchHintView> searchHintViews = productRepository.findHintViews(operation.getSearchTerm());

        Map<CategoryHintDto, List<ProductHintDto>> groupedProductHint = searchHintViews.stream()
                .collect(Collectors.groupingBy(
                        hint -> CategoryHintDto.builder()
                                .id(hint.getCategoryId())
                                .name(hint.getCategoryName())
                                .image(hint.getCategoryImage())
                                .build(),
                        LinkedHashMap::new,
                        Collectors.mapping(
                                hint -> ProductHintDto.builder()
                                        .id(hint.getProductId())
                                        .title(hint.getProductTitle())
                                        .image(hint.getProductImage())
                                        .build(),
                                Collectors.toList()
                        )
                ));

        List<SearchHintDto> groupedSearchHints = groupedProductHint.entrySet().stream()
                .map(entry -> SearchHintDto.builder()
                        .category(entry.getKey())
                        .products(entry.getValue())
                        .build()
                ).toList();

        return GetSearchHints.Result.success(groupedSearchHints);
    }

    protected ProductDto loadFullProduct(ProductDto productDto, ProductView view, Locale locale) {
        // Vendor
        Optional<UserView> userView = userRepository.getViewById(view.getUserId());
        if (userView.isPresent()) {
            VendorDto vendorDto = VendorDto.of(userView.get());

            // Vendor Countries
            List<VendorCountryView> vendorCountryViewList = vendorCountryRepository.getAllViewsByVendorDetailsIdAndLang(
                    userView.get().getVendorDetails().getId(),
                    locale.getLanguage()
            );
            List<VendorCountryDto> vendorCountryDtoList = vendorCountryViewList.stream()
                    .map(VendorCountryDto::of)
                    .toList();

            // Vendor Product Categories
            List<VendorProductCategoryView> vendorProductCategoryViewList = vendorProductCategoryRepository.getAllViewsByVendorDetailsIdAndLang(
                    userView.get().getVendorDetails().getId(),
                    locale.getLanguage()
            );
            List<VendorProductCategoryDto> vendorProductCategoryDtoList = vendorProductCategoryViewList.stream()
                    .map(VendorProductCategoryDto::of)
                    .toList();

            // Vendor Faq
            List<VendorFaqView> vendorFaqViewList = vendorFaqRepository.getAllViewsByVendorDetailsIdAndLang(
                    userView.get().getVendorDetails().getId(),
                    locale.getLanguage()
            );
            List<VendorFaqDto> vendorFaqDtoList = vendorFaqViewList.stream()
                    .map(VendorFaqDto::of)
                    .toList();

            vendorDto.getVendorDetails().setCountries(vendorCountryDtoList);
            vendorDto.getVendorDetails().setProductCategories(vendorProductCategoryDtoList);
            vendorDto.getVendorDetails().setFaq(vendorFaqDtoList);
            productDto.setUser(vendorDto);
        }

        // Delivery Methods
        List<DeliveryMethodView> deliveryMethodViewList = deliveryMethodRepository.getAllDeliveryMethodViewsByProductIdLang(
                productDto.getId(),
                locale.getLanguage()
        );
        List<DeliveryMethodDto> deliveryMethodDtoList = deliveryMethodViewList.stream().map(DeliveryMethodDto::of).toList();
        productDto.setDeliveryMethods(deliveryMethodDtoList);

        // Category
        Optional<CategoryView> categoryView = categoryRepository.getCategoryViewByIdAndLang(
                view.getCategoryId(),
                locale.getLanguage()
        );

        if (categoryView.isPresent()) {
            CategoryDto categoryDto = CategoryDto.ofWithoutChildren(categoryView.get());
            productDto.setCategory(categoryDto);
        }

        // Media
        List<ProductMediaView> productMediaList = productMediaRepository.getAllViewsByProductId(
                productDto.getId()
        );
        List<ProductMediaDto> productMediaDtoList = productMediaList.stream()
                .map(ProductMediaDto::of)
                .toList();
        productDto.setMedia(productMediaDtoList);

        // Similar Products
        List<SimilarProductView> similarProductViewList = productRepository.getAllSimilarProductViewsByProductIdAndLang(
                productDto.getId(),
                locale.getLanguage()
        );
        List<SimilarProductDto> similarProductDtoList = similarProductViewList.stream()
                .map(SimilarProductDto::of)
                .toList();
        productDto.setSimilarProducts(similarProductDtoList);

        // Characteristics
        List<ProductCharacteristicView> productCharacteristicList = productCharacteristicRepository.findAllViewsByProductIdAndLang(
                productDto.getId(),
                locale.getLanguage()
        );
        List<ProductCharacteristicDto> productCharacteristicDtoList = productCharacteristicList.stream()
                .map(ProductCharacteristicDto::of)
                .toList();
        productDto.setCharacteristics(productCharacteristicDtoList);

        // Faq
        List<ProductFaqView> productFaqList = productFaqRepository.findAllViewsByProductIdAndLang(
                productDto.getId(),
                locale.getLanguage()
        );
        List<ProductFaqDto> productFaqDtoList = productFaqList.stream()
                .map(ProductFaqDto::of)
                .toList();
        productDto.setFaq(productFaqDtoList);

        // Prices
        List<ProductPriceView> productPriceList = productPriceRepository.findAllViewsByProductId(
                productDto.getId()
        );
        List<ProductPriceDto> productPriceDtoList = productPriceList.stream()
                .map(ProductPriceDto::of)
                .toList();
        productDto.setPrices(productPriceDtoList);

        // Reviews Media
        List<ProductReviewMediaView> productReviewMediaList = productReviewMediaRepository.getAllViewsByProductId(
                productDto.getId()
        );
        List<ProductReviewMediaDto> productReviewMediaDtoList = productReviewMediaList.stream()
                .map(ProductReviewMediaDto::of)
                .toList();
        productDto.setReviewsMedia(productReviewMediaDtoList);

        // About Vendor
        ProductVendorDetailsView productVendorDetailsView = productVendorDetailsRepository.getViewByProductIdAndLang(
                productDto.getId(),
                locale.getLanguage()
        ).orElse(null);
        ProductVendorDetailsDto productVendorDetailsDto = ProductVendorDetailsDto.of(productVendorDetailsView);
        productDto.setAboutVendor(productVendorDetailsDto);

        // About Vendor Media
        if (productVendorDetailsView != null) {
            List<ProductVendorDetailsMediaView> productVendorDetailsMediaViewList = productVendorDetailsMediaRepository.getAllViewsByProductVendorDetailsId(
                    productVendorDetailsView.getId()
            );
            List<ProductVendorDetailsMediaDto> productVendorDetailsMediaDtoList = productVendorDetailsMediaViewList.stream()
                    .map(ProductVendorDetailsMediaDto::of).toList();
            productVendorDetailsDto.setMedia(productVendorDetailsMediaDtoList);
        }

        // Delivery Method Details
        List<ProductDeliveryMethodDetailsView> productDeliveryMethodDetailsViewList = productDeliveryMethodDetailsRepository.getAllViewsByProductIdAndLang(
                productDto.getId(),
                locale.getLanguage()
        );
        List<ProductDeliveryMethodDetailsDto> productDeliveryMethodDetailsDtoList = productDeliveryMethodDetailsViewList.stream()
                .map(ProductDeliveryMethodDetailsDto::of).toList();
        productDto.setDeliveryMethodsDetails(productDeliveryMethodDetailsDtoList);

        // Packaging Options
        List<ProductPackageOptionView> productPackageOptionViewList = productPackageOptionsRepository.getAllViewsByProductIdAndLang(
                productDto.getId(),
                locale.getLanguage()
        );
        List<ProductPackageOptionDto> productPackageOptionDtoList = productPackageOptionViewList.stream()
                .map(ProductPackageOptionDto::of).toList();
        productDto.setPackagingOptions(productPackageOptionDtoList);

        return productDto;
    }

    // Creators - Create commands
    private ProductCharacteristic createProductCharacteristic(CreateProductCharacteristicCommand command, Product product) {
        return createProductCharacteristic(
                product,
                ProductCharacteristicName.of(command.name()),
                ProductCharacteristicValue.of(command.value())
        );
    }

    private ProductFaq createProductFaq(CreateProductFaqCommand command, Product product) {
        return createProductFaq(
                product,
                ProductFaqQuestion.of(command.question()),
                ProductFaqAnswer.of(command.answer())
        );
    }

    private ProductDeliveryMethodDetails createProductDeliveryMethodDetails(CreateProductDeliveryMethodDetailsCommand command, Product product) {
        return createProductDeliveryMethodDetails(
                product,
                ProductDeliveryMethodDetailsName.of(command.name()),
                ProductDeliveryMethodDetailsValue.of(command.value())
        );
    }

    private ProductPackageOption createProductPackageOption(CreateProductPackageOptionCommand command, Product product) {
        return createProductPackageOption(
                product,
                ProductPackageOptionName.of(command.name()),
                ProductPackageOptionPrice.of(command.price()),
                ProductPackageOptionPriceUnit.of(command.priceUnit())
        );
    }

    private ProductPrice createProductPrice(CreateProductPriceCommand command, Product product) {
        return createProductPrice(
                product,
                ProductPriceOriginalPrice.of(command.price()),
                ProductPriceDiscount.of(command.discount()),
                ProductPriceCurrency.of(command.currency()),
                ProductPriceUnit.of(command.unit()),
                ProductPriceQuantityRange.of(command.quantityFrom(), command.quantityTo())
        );
    }

    // Creators - Update commands
    private ProductCharacteristic createProductCharacteristic(UpdateProductCharacteristicCommand command, Product product) {
        return createProductCharacteristic(
                product,
                ProductCharacteristicName.of(command.name()),
                ProductCharacteristicValue.of(command.value())
        );
    }

    private ProductFaq createProductFaq(UpdateProductFaqCommand command, Product product) {
        return createProductFaq(
                product,
                ProductFaqQuestion.of(command.question()),
                ProductFaqAnswer.of(command.answer())
        );
    }

    private ProductDeliveryMethodDetails createProductDeliveryMethodDetails(UpdateProductDeliveryMethodDetailsCommand command, Product product) {
        return createProductDeliveryMethodDetails(
                product,
                ProductDeliveryMethodDetailsName.of(command.name()),
                ProductDeliveryMethodDetailsValue.of(command.value())
        );
    }

    private ProductPackageOption createProductPackageOption(UpdateProductPackageOptionCommand command, Product product) {
        return createProductPackageOption(
                product,
                ProductPackageOptionName.of(command.name()),
                ProductPackageOptionPrice.of(command.price()),
                ProductPackageOptionPriceUnit.of(command.priceUnit())
        );
    }

    private ProductPrice createProductPrice(UpdateProductPriceCommand command, Product product) {
        return createProductPrice(
                product,
                ProductPriceOriginalPrice.of(command.price()),
                ProductPriceDiscount.of(command.discount()),
                ProductPriceCurrency.of(command.currency()),
                ProductPriceUnit.of(command.unit()),
                ProductPriceQuantityRange.of(command.quantityFrom(), command.quantityTo())
        );
    }

    // Creators - Root
    private ProductPrice createProductPrice(
            Product product,
            ProductPriceOriginalPrice originalPrice,
            ProductPriceDiscount discount,
            ProductPriceCurrency currency,
            ProductPriceUnit unit,
            ProductPriceQuantityRange quantityRange
    ) {
        ProductPrice productPrice = new ProductPrice();
        productPrice.setProduct(product);
        productPrice.setOriginalPrice(originalPrice);
        productPrice.setDiscount(discount);
        productPrice.setCurrency(currency);
        productPrice.setUnit(unit);
        productPrice.setQuantityRange(quantityRange);
        return productPrice;
    }

    private ProductCharacteristic createProductCharacteristic(
            Product product,
            ProductCharacteristicName name,
            ProductCharacteristicValue value
    ) {
        ProductCharacteristic productCharacteristic = new ProductCharacteristic();
        productCharacteristic.setProduct(product);
        productCharacteristic.setName(name);
        productCharacteristic.setValue(value);
        return productCharacteristic;
    }

    private ProductFaq createProductFaq(
            Product product,
            ProductFaqQuestion question,
            ProductFaqAnswer answer
    ) {
        ProductFaq productFaq = new ProductFaq();
        productFaq.setProduct(product);
        productFaq.setQuestion(question);
        productFaq.setAnswer(answer);
        return productFaq;
    }

    private ProductDeliveryMethodDetails createProductDeliveryMethodDetails(
            Product product,
            ProductDeliveryMethodDetailsName name,
            ProductDeliveryMethodDetailsValue value
    ) {
        ProductDeliveryMethodDetails productDeliveryMethodDetails = new ProductDeliveryMethodDetails();
        productDeliveryMethodDetails.setProduct(product);
        productDeliveryMethodDetails.setName(name);
        productDeliveryMethodDetails.setValue(value);
        return productDeliveryMethodDetails;
    }

    private ProductPackageOption createProductPackageOption(
            Product product,
            ProductPackageOptionName name,
            ProductPackageOptionPrice price,
            ProductPackageOptionPriceUnit unit
    ) {
        ProductPackageOption productPackageOption = new ProductPackageOption();
        productPackageOption.setProduct(product);
        productPackageOption.setName(name);
        productPackageOption.setPrice(price);
        productPackageOption.setPriceUnit(unit);
        return productPackageOption;
    }

    private enum TranslationKeys {
        TITLE,
        MAIN_DESCRIPTION,
        FURTHER_DESCRIPTION,
        CHARACTERISTIC_NAME,
        CHARACTERISTIC_VALUE,
        FAQ_QUESTION,
        FAQ_ANSWER,
        DELIVERY_METHOD_DETAILS_NAME,
        DELIVERY_METHOD_DETAILS_VALUE,
        PACKAGE_OPTIONS_NAME,
        VENDOR_DETAILS_MAIN_DESCRIPTION,
        VENDOR_DETAILS_FURTHER_DESCRIPTION;

        public String with(int index) {
            return this.name() + "_" + index;
        }
    }

    private record PreloadContentInfo<T>(
            T entity,
            MultipartFile file,
            String folderName
    ) {
    }
}