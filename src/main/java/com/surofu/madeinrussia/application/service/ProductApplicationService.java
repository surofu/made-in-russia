package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.cache.CategoryCacheManager;
import com.surofu.madeinrussia.application.cache.GeneralCacheService;
import com.surofu.madeinrussia.application.cache.ProductCacheManager;
import com.surofu.madeinrussia.application.cache.ProductSummaryCacheManager;
import com.surofu.madeinrussia.application.command.product.create.*;
import com.surofu.madeinrussia.application.command.product.update.*;
import com.surofu.madeinrussia.application.dto.DeliveryMethodDto;
import com.surofu.madeinrussia.application.dto.SearchHintDto;
import com.surofu.madeinrussia.application.dto.category.CategoryDto;
import com.surofu.madeinrussia.application.dto.category.CategoryHintDto;
import com.surofu.madeinrussia.application.dto.product.*;
import com.surofu.madeinrussia.application.dto.translation.HstoreTranslationDto;
import com.surofu.madeinrussia.application.dto.vendor.VendorCountryDto;
import com.surofu.madeinrussia.application.dto.vendor.VendorDto;
import com.surofu.madeinrussia.application.dto.vendor.VendorFaqDto;
import com.surofu.madeinrussia.application.dto.vendor.VendorProductCategoryDto;
import com.surofu.madeinrussia.application.enums.FileStorageFolders;
import com.surofu.madeinrussia.application.exception.EmptyTranslationException;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.model.media.MediaType;
import com.surofu.madeinrussia.core.model.moderation.ApproveStatus;
import com.surofu.madeinrussia.core.model.product.*;
import com.surofu.madeinrussia.core.model.product.characteristic.ProductCharacteristic;
import com.surofu.madeinrussia.core.model.product.characteristic.ProductCharacteristicName;
import com.surofu.madeinrussia.core.model.product.characteristic.ProductCharacteristicValue;
import com.surofu.madeinrussia.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetails;
import com.surofu.madeinrussia.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetailsName;
import com.surofu.madeinrussia.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetailsValue;
import com.surofu.madeinrussia.core.model.product.faq.ProductFaq;
import com.surofu.madeinrussia.core.model.product.faq.ProductFaqAnswer;
import com.surofu.madeinrussia.core.model.product.faq.ProductFaqQuestion;
import com.surofu.madeinrussia.core.model.product.media.*;
import com.surofu.madeinrussia.core.model.product.packageOption.ProductPackageOption;
import com.surofu.madeinrussia.core.model.product.packageOption.ProductPackageOptionName;
import com.surofu.madeinrussia.core.model.product.packageOption.ProductPackageOptionPrice;
import com.surofu.madeinrussia.core.model.product.packageOption.ProductPackageOptionPriceUnit;
import com.surofu.madeinrussia.core.model.product.price.*;
import com.surofu.madeinrussia.core.model.user.UserRole;
import com.surofu.madeinrussia.core.repository.*;
import com.surofu.madeinrussia.core.service.product.ProductService;
import com.surofu.madeinrussia.core.service.product.operation.*;
import com.surofu.madeinrussia.infrastructure.persistence.category.CategoryView;
import com.surofu.madeinrussia.infrastructure.persistence.deliveryMethod.DeliveryMethodView;
import com.surofu.madeinrussia.infrastructure.persistence.product.ProductView;
import com.surofu.madeinrussia.infrastructure.persistence.product.ProductWithTranslationsView;
import com.surofu.madeinrussia.infrastructure.persistence.product.SearchHintView;
import com.surofu.madeinrussia.infrastructure.persistence.product.SimilarProductView;
import com.surofu.madeinrussia.infrastructure.persistence.product.characteristic.ProductCharacteristicView;
import com.surofu.madeinrussia.infrastructure.persistence.product.characteristic.ProductCharacteristicWithTranslationsView;
import com.surofu.madeinrussia.infrastructure.persistence.product.deliveryMethodDetails.ProductDeliveryMethodDetailsView;
import com.surofu.madeinrussia.infrastructure.persistence.product.deliveryMethodDetails.ProductDeliveryMethodDetailsWithTranslationsView;
import com.surofu.madeinrussia.infrastructure.persistence.product.faq.ProductFaqView;
import com.surofu.madeinrussia.infrastructure.persistence.product.faq.ProductFaqWithTranslationsView;
import com.surofu.madeinrussia.infrastructure.persistence.product.media.ProductMediaView;
import com.surofu.madeinrussia.infrastructure.persistence.product.media.ProductMediaWithTranslationsView;
import com.surofu.madeinrussia.infrastructure.persistence.product.packageOption.ProductPackageOptionView;
import com.surofu.madeinrussia.infrastructure.persistence.product.packageOption.ProductPackageOptionWithTranslationsView;
import com.surofu.madeinrussia.infrastructure.persistence.product.price.ProductPriceView;
import com.surofu.madeinrussia.infrastructure.persistence.product.review.media.ProductReviewMediaView;
import com.surofu.madeinrussia.infrastructure.persistence.user.UserView;
import com.surofu.madeinrussia.infrastructure.persistence.vendor.country.VendorCountryView;
import com.surofu.madeinrussia.infrastructure.persistence.vendor.faq.VendorFaqView;
import com.surofu.madeinrussia.infrastructure.persistence.vendor.productCategory.VendorProductCategoryView;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
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
    private final ProductDeliveryMethodDetailsRepository productDeliveryMethodDetailsRepository;
    private final ProductPackageOptionsRepository productPackageOptionsRepository;
    private final FileStorageRepository fileStorageRepository;
    private final TranslationRepository translationRepository;
    private final TaskExecutor appTaskExecutor;
    private final ProductCacheManager productCacheManager;
    private final LocalizationManager localizationManager;

    @PersistenceContext
    private final EntityManager entityManager;

    private final ProductSummaryCacheManager productSummaryCacheManager;
    private final GeneralCacheService generalCacheService;
    private final FaqApplicationService faqApplicationService;
    private final CategoryCacheManager categoryCacheManager;

    @Override
    @Transactional(readOnly = true)
    public GetProductById.Result getProductById(GetProductById operation) {
        // Check cache
        ProductDto cachedProduct = productCacheManager.getProduct(operation.getProductId(), operation.getLocale().getLanguage());

        if (cachedProduct != null) {
            return GetProductById.Result.success(cachedProduct);
        }

        // Process
        List<ApproveStatus> approveStatuses = new ArrayList<>();
        approveStatuses.add(ApproveStatus.APPROVED);

        if (operation.getSecurityUser() != null && (
                operation.getSecurityUser().getUser().getId().equals(operation.getProductId()) ||
                        operation.getSecurityUser().getUser().getRole().equals(UserRole.ROLE_ADMIN)
        )) {
            approveStatuses.add(ApproveStatus.PENDING);
            approveStatuses.add(ApproveStatus.REJECTED);
        }

        Optional<ProductView> productView = productRepository.getProductViewByIdAndLangAndApproveStatuses(
                operation.getProductId(), operation.getLocale().getLanguage(), approveStatuses
        );

        if (productView.isEmpty()) {
            return GetProductById.Result.notFound(operation.getProductId());
        }

        ProductDto productDto = ProductDto.of(productView.get());
        ProductDto fullProductDto = loadFullProduct(productDto, productView.get(), operation.getLocale());

        if (fullProductDto.getDaysBeforeDiscountExpires() == null ||
                (fullProductDto.getDeliveryMethodsDetails() != null && fullProductDto.getDaysBeforeDiscountExpires() <= 0)) {
            for (ProductPriceDto price : fullProductDto.getPrices()) {
                price.setDiscount(BigDecimal.ZERO);
                price.setDiscountedPrice(price.getOriginalPrice());
            }
        }

        for (ProductPriceDto price : fullProductDto.getPrices()) {
            if (price.getDiscount().equals(BigDecimal.ZERO)) {
                price.setDiscountedPrice(price.getOriginalPrice());
            }
        }

        return GetProductById.Result.success(fullProductDto);
    }

    @Override
    @Transactional(readOnly = true)
    public GetProductWithTranslationsById.Result getProductWithTranslationsByProductId(GetProductWithTranslationsById operation) {
        Optional<ProductWithTranslationsView> view = productRepository.getProductWithTranslationsByProductIdAndLang(
                operation.getId(), operation.getLocale().getLanguage());

        if (view.isEmpty()) {
            return GetProductWithTranslationsById.Result.notFound(operation.getId());
        }

        ProductWithTranslationsDto dto = ProductWithTranslationsDto.of(view.get());
        ProductWithTranslationsDto fullDto = loadFullProduct(dto, view.get(), operation.getLocale());
        return GetProductWithTranslationsById.Result.success(fullDto);
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

        if (!productRepository.existsById(productId)) {
            return GetProductCharacteristicsByProductId.Result.notFound(productId);
        }

        List<ProductCharacteristic> productCharacteristics = productCharacteristicRepository.getAllByProductId(productId);
        List<ProductCharacteristicDto> productCharacteristicDtos = productCharacteristics.stream().map(ProductCharacteristicDto::of).toList();
        return GetProductCharacteristicsByProductId.Result.success(productCharacteristicDtos);

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public GetProductFaqByProductId.Result getProductFaqByProductId(GetProductFaqByProductId operation) {
        Long productId = operation.getProductId();

        if (!productRepository.existsById(productId)) {
            return GetProductFaqByProductId.Result.notFound(productId);
        }

        List<ProductFaq> productFaq = productFaqRepository.getAllByProductId(productId);
        List<ProductFaqDto> productFaqDtos = productFaq.stream().map(ProductFaqDto::of).toList();
        return GetProductFaqByProductId.Result.success(productFaqDtos);

    }

    @Override
    @Transactional
    public CreateProduct.Result createProduct(CreateProduct operation) {
        entityManager.setFlushMode(FlushModeType.COMMIT);

        Product product = new Product();
        product.setUser(operation.getSecurityUser().getUser());

        Map<String, HstoreTranslationDto> translationMap = new HashMap<>();

        product.setTitle(operation.getProductTitle());
        translationMap.put(TranslationKeys.TITLE.name(), operation.getProductTitle().getTranslations());

        product.setDescription(operation.getProductDescription());
        translationMap.put(TranslationKeys.MAIN_DESCRIPTION.name(), operation.getProductDescription().getMainDescriptionTranslations());

        if (operation.getProductDescription().getFurtherDescriptionTranslations() != null) {
            translationMap.put(TranslationKeys.FURTHER_DESCRIPTION.name(), operation.getProductDescription().getFurtherDescriptionTranslations());
        }

        product.setMinimumOrderQuantity(ProductMinimumOrderQuantity.of(operation.getMinimumOrderQuantity()));
        product.setDiscountExpirationDate(ProductDiscountExpirationDate.of(operation.getDiscountExpirationDate()));

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

        Optional<Long> firstNotExistsDeliveryMethodId = deliveryMethodRepository.firstNotExists(operation.getDeliveryMethodIds());
        if (firstNotExistsDeliveryMethodId.isPresent()) {
            return CreateProduct.Result.deliveryMethodNotFound(firstNotExistsDeliveryMethodId.get());
        }

        List<DeliveryMethod> deliveryMethodList = deliveryMethodRepository.getAllDeliveryMethodsByIds(operation.getDeliveryMethodIds());
        product.setDeliveryMethods(new HashSet<>(deliveryMethodList));

        List<ProductPrice> productPriceList = new ArrayList<>();
        List<String> unitList = new ArrayList<>();

        for (int i = 0; i < operation.getCreateProductPriceCommands().size(); i++) {
            CreateProductPriceCommand command = operation.getCreateProductPriceCommands().get(i);
            ProductPrice productPrice = createProductPrice(command, product);
            productPriceList.add(productPrice);
            unitList.add(command.unit());
        }

        List<HstoreTranslationDto> unitTranslationList;
        try {
            unitTranslationList = translationRepository.expand(unitList.toArray(String[]::new));
        } catch (Exception e) {
            return CreateProduct.Result.translationError(e);
        }

        for (int i = 0; i < unitTranslationList.size(); i++) {
            productPriceList.get(i).getUnit().setTranslations(unitTranslationList.get(i));
        }

        product.setPrices(new HashSet<>(productPriceList));

        Optional<Long> firstNotExistsSimilarProductId = productRepository.firstNotExists(operation.getSimilarProductIds());
        if (firstNotExistsSimilarProductId.isPresent()) {
            return CreateProduct.Result.similarProductNotFound(firstNotExistsSimilarProductId.get());
        }

        List<Product> similarProducts = productRepository.findAllByIds(operation.getSimilarProductIds());
        product.setSimilarProducts(new HashSet<>(similarProducts));

        for (int i = 0; i < operation.getCreateProductCharacteristicCommands().size(); i++) {
            CreateProductCharacteristicCommand command = operation.getCreateProductCharacteristicCommands().get(i);
            translationMap.put(TranslationKeys.CHARACTERISTIC_NAME.with(i), HstoreTranslationDto.ofNullable(command.nameTranslations()));
            translationMap.put(TranslationKeys.CHARACTERISTIC_VALUE.with(i), HstoreTranslationDto.ofNullable(command.valueTranslations()));
        }

        for (int i = 0; i < operation.getCreateProductFaqCommands().size(); i++) {
            CreateProductFaqCommand command = operation.getCreateProductFaqCommands().get(i);
            translationMap.put(TranslationKeys.FAQ_QUESTION.with(i), HstoreTranslationDto.ofNullable(command.questionTranslations()));
            translationMap.put(TranslationKeys.FAQ_ANSWER.with(i), HstoreTranslationDto.ofNullable(command.answerTranslations()));
        }

        for (int i = 0; i < operation.getCreateProductDeliveryMethodDetailsCommands().size(); i++) {
            CreateProductDeliveryMethodDetailsCommand command = operation.getCreateProductDeliveryMethodDetailsCommands().get(i);
            translationMap.put(TranslationKeys.DELIVERY_METHOD_DETAILS_NAME.with(i), HstoreTranslationDto.ofNullable(command.nameTranslations()));
            translationMap.put(TranslationKeys.DELIVERY_METHOD_DETAILS_VALUE.with(i), HstoreTranslationDto.ofNullable(command.valueTranslations()));
        }

        for (int i = 0; i < operation.getCreateProductPackageOptionCommands().size(); i++) {
            CreateProductPackageOptionCommand command = operation.getCreateProductPackageOptionCommands().get(i);
            translationMap.put(TranslationKeys.PACKAGE_OPTIONS_NAME.with(i), HstoreTranslationDto.ofNullable(command.nameTranslations()));
        }

        for (int i = 0; i < operation.getCreateProductMediaAltTextCommands().size(); i++) {
            CreateProductMediaAltTextCommand command = operation.getCreateProductMediaAltTextCommands().get(i);
            translationMap.put(TranslationKeys.MEDIA_ALT_TEXT.with(i), HstoreTranslationDto.ofNullable(command.translations()));
        }

        Map<String, HstoreTranslationDto> resultMap;
        try {
            resultMap = translationRepository.expand(translationMap);
        } catch (EmptyTranslationException e) {
            return CreateProduct.Result.emptyTranslation(e.getMessage());
        } catch (Exception e) {
            return CreateProduct.Result.translationError(e);
        }

        HstoreTranslationDto translatedTitle = resultMap.get(TranslationKeys.TITLE.name());
        product.getTitle().setTranslations(translatedTitle);

        HstoreTranslationDto translatedMainDescription = resultMap.get(TranslationKeys.MAIN_DESCRIPTION.name());
        HstoreTranslationDto translatedFurtherDescription = resultMap.get(TranslationKeys.FURTHER_DESCRIPTION.name());
        product.getDescription().setMainDescriptionTranslations(translatedMainDescription);
        product.getDescription().setFurtherDescriptionTranslations(translatedFurtherDescription);

        Set<ProductCharacteristic> productCharacteristicSet = new HashSet<>(operation.getCreateProductCharacteristicCommands().size());
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

        Set<ProductFaq> productFaqSet = new HashSet<>(operation.getCreateProductFaqCommands().size());
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

        Set<ProductDeliveryMethodDetails> productDeliveryMethodDetailsSet = new HashSet<>(operation.getCreateProductDeliveryMethodDetailsCommands().size());
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

        Set<ProductPackageOption> productPackageOptionSet = new HashSet<>(operation.getCreateProductPackageOptionCommands().size());
        for (int i = 0; i < operation.getCreateProductPackageOptionCommands().size(); i++) {
            CreateProductPackageOptionCommand command = operation.getCreateProductPackageOptionCommands().get(i);
            ProductPackageOption productPackageOption = createProductPackageOption(command, product);
            HstoreTranslationDto translatedPackageOptionName = resultMap.get(TranslationKeys.PACKAGE_OPTIONS_NAME.with(i));
            productPackageOption.getName().setTranslations(translatedPackageOptionName);
            productPackageOptionSet.add(productPackageOption);
        }
        product.setPackageOptions(productPackageOptionSet);

        List<CompletableFuture<String>> allMediaFutureList = new ArrayList<>(operation.getProductMedia().size() + operation.getProductVendorDetailsMedia().size());
        List<ProductMedia> productMediaList = new ArrayList<>(operation.getProductMedia().size());

        for (int i = 0; i < operation.getProductMedia().size(); i++) {
            MultipartFile file = operation.getProductMedia().get(i);
            ProductMedia productMedia = new ProductMedia();
            productMedia.setProduct(product);
            productMedia.setMimeType(ProductMediaMimeType.of(file.getContentType()));
            productMedia.setPosition(ProductMediaPosition.of(i));

            ProductMediaAltText altText = ProductMediaAltText.of(file.getOriginalFilename());
            HstoreTranslationDto altTextTranslations;

            if (i < operation.getCreateProductMediaAltTextCommands().size()) {
                HstoreTranslationDto translatedAltText = resultMap.get(TranslationKeys.MEDIA_ALT_TEXT.with(i));
                altTextTranslations = translatedAltText != null ? translatedAltText :
                        new HstoreTranslationDto(file.getOriginalFilename(), file.getOriginalFilename(), file.getOriginalFilename());
            } else {
                altTextTranslations = new HstoreTranslationDto(file.getOriginalFilename(), file.getOriginalFilename(), file.getOriginalFilename());
            }

            altText.setTranslations(altTextTranslations);
            productMedia.setAltText(altText);

            if (file.getContentType() == null) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return CreateProduct.Result.invalidMediaType("null");
            }

            String folderName;
            if (file.getContentType().startsWith("image/")) {
                productMedia.setMediaType(MediaType.IMAGE);
                folderName = "productImages";
            } else if (file.getContentType().startsWith("video/")) {
                productMedia.setMediaType(MediaType.VIDEO);
                folderName = "productVideos";
            } else {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return CreateProduct.Result.invalidMediaType(file.getContentType());
            }

            CompletableFuture<String> productMediaFuture = CompletableFuture.supplyAsync(
                    () -> {
                        try {
                            return fileStorageRepository.uploadImageToFolder(file, folderName);
                        } catch (Exception e) {
                            throw new CompletionException(e);
                        }
                    },
                    appTaskExecutor
            );

            productMediaList.add(productMedia);
            allMediaFutureList.add(productMediaFuture);
        }

        product.setMedia(new HashSet<>(productMediaList));

        CompletableFuture<Void> allMediaFuture = CompletableFuture.allOf(allMediaFutureList.toArray(CompletableFuture[]::new));

        try {
            allMediaFuture.join();
            for (int i = 0; i < operation.getProductMedia().size(); i++) {
                String url = allMediaFutureList.get(i).get();
                productMediaList.get(i).setUrl(ProductMediaUrl.of(url));
                if (i == 0) {
                    product.setPreviewImageUrl(ProductPreviewImageUrl.of(url));
                }
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CreateProduct.Result.errorSavingFiles(e);
        }

        try {
            saveProduct(product);
            productCharacteristicRepository.saveAll(productCharacteristicSet);
            productFaqRepository.saveAll(productFaqSet);
            productPriceRepository.saveAll(productPriceList);
            productDeliveryMethodDetailsRepository.saveAll(productDeliveryMethodDetailsSet);
            productPackageOptionsRepository.saveAll(productPackageOptionSet);
            productMediaRepository.saveAll(productMediaList);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CreateProduct.Result.errorSavingProduct(e);
        }

        productSummaryCacheManager.clearAll();
        generalCacheService.clear();
        return CreateProduct.Result.success();
    }

    @Override
    @Transactional
    public UpdateProduct.Result updateProduct(UpdateProduct operation) {
        entityManager.setFlushMode(FlushModeType.COMMIT);

        Optional<Product> existingProduct = productRepository.getProductById(operation.getProductId());

        if (existingProduct.isEmpty()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.productNotFound(operation.getProductId());
        }

        Product product = existingProduct.get();

        if (
                !product.getUser().getId().equals(operation.getSecurityUser().getUser().getId())
                        && operation.getSecurityUser().getUser().getRole() != UserRole.ROLE_ADMIN
        ) {
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
        List<ProductPrice> productPriceList = new ArrayList<>();
        List<String> unitList = new ArrayList<>();

        for (UpdateProductPriceCommand command : operation.getUpdateProductPriceCommands()) {
            ProductPrice productPrice = createProductPrice(command, product);
            productPriceList.add(productPrice);
            unitList.add(command.unit());
        }

        List<HstoreTranslationDto> unitTranslationList;

        try {
            unitTranslationList = translationRepository.expand(unitList.toArray(String[]::new));
        } catch (Exception e) {
            return UpdateProduct.Result.translationError(e);
        }

        for (int i = 0; i < unitTranslationList.size(); i++) {
            productPriceList.get(i).getUnit().setTranslations(unitTranslationList.get(i));
        }

        List<ProductPrice> oldProductPrices = productPriceRepository.getAllByProductId(product.getId());

        try {
            productPriceRepository.deleteAll(oldProductPrices);
            productPriceRepository.saveAll(productPriceList);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.errorSavingProduct(e);
        }

        product.setPrices(new HashSet<>(productPriceList));

        /* ========== Product Characteristics ========== */
        for (int i = 0; i < operation.getUpdateProductCharacteristicCommands().size(); i++) {
            UpdateProductCharacteristicCommand command = operation.getUpdateProductCharacteristicCommands().get(i);
            translationMap.put(TranslationKeys.CHARACTERISTIC_NAME.with(i), HstoreTranslationDto.ofNullable(command.nameTranslations()));
            translationMap.put(TranslationKeys.CHARACTERISTIC_VALUE.with(i), HstoreTranslationDto.ofNullable(command.valueTranslations()));
        }

        /* ========== Product Faq ========== */
        for (int i = 0; i < operation.getUpdateProductFaqCommands().size(); i++) {
            UpdateProductFaqCommand command = operation.getUpdateProductFaqCommands().get(i);
            translationMap.put(TranslationKeys.FAQ_QUESTION.with(i), HstoreTranslationDto.ofNullable(command.questionTranslations()));
            translationMap.put(TranslationKeys.FAQ_ANSWER.with(i), HstoreTranslationDto.ofNullable(command.answerTranslations()));
        }

        /* ========== Product Delivery Method Details ========== */
        for (int i = 0; i < operation.getUpdateProductDeliveryMethodDetailsCommands().size(); i++) {
            UpdateProductDeliveryMethodDetailsCommand command = operation.getUpdateProductDeliveryMethodDetailsCommands().get(i);
            translationMap.put(TranslationKeys.DELIVERY_METHOD_DETAILS_NAME.with(i), HstoreTranslationDto.ofNullable(command.nameTranslations()));
            translationMap.put(TranslationKeys.DELIVERY_METHOD_DETAILS_VALUE.with(i), HstoreTranslationDto.ofNullable(command.valueTranslations()));
        }

        /* ========== Product Package Options ========== */
        for (int i = 0; i < operation.getUpdateProductPackageOptionCommands().size(); i++) {
            UpdateProductPackageOptionCommand command = operation.getUpdateProductPackageOptionCommands().get(i);
            translationMap.put(TranslationKeys.PACKAGE_OPTIONS_NAME.with(i), HstoreTranslationDto.ofNullable(command.nameTranslations()));
        }

        /* ========== Product Media ========== */
        List<Long> oldProductMediaIdList = operation.getOldProductMedia().stream().map(UpdateOldMediaDto::id).toList();
        Set<ProductMedia> allOldProductMediaList = product.getMedia();
        List<ProductMedia> newOldProductMediaList = allOldProductMediaList.stream().filter(m -> oldProductMediaIdList.contains(m.getId())).toList();
        List<ProductMedia> oldProductMediaToDeleteList = allOldProductMediaList.stream().filter(m -> !oldProductMediaIdList.contains(m.getId())).toList();
        Set<Object> mediaForDeleteSet = new HashSet<>(oldProductMediaToDeleteList);

        List<ProductMedia> productMediaList = new ArrayList<>();
        List<MultipartFile> productMediaFileSet = operation.getProductMedia();

        String TEMP_URL = "TEMP_URL";
        for (int i = 0, j = 0; i < newOldProductMediaList.size() + operation.getProductMedia().size(); i++) {
            ProductMedia oldProductMedia = i >= newOldProductMediaList.size() ? null : newOldProductMediaList.get(i);

            if (oldProductMedia == null) {
                MultipartFile file = productMediaFileSet.get(j);
                ProductMedia newProductMedia = new ProductMedia();
                newProductMedia.setProduct(product);
                newProductMedia.setPosition(ProductMediaPosition.of(i));

                newProductMedia.setAltText(ProductMediaAltText.of(file.getOriginalFilename()));
                if (operation.getUpdateProductMediaAltTextCommands() != null && operation.getUpdateProductMediaAltTextCommands().size() > j) {
                    newProductMedia.setAltText(ProductMediaAltText.of(operation.getUpdateProductMediaAltTextCommands().get(j).altText()));
                }
                newProductMedia.getAltText().setTranslations(new HstoreTranslationDto(file.getOriginalFilename(), file.getOriginalFilename(), file.getOriginalFilename()));
                newProductMedia.setMimeType(ProductMediaMimeType.of(file.getContentType()));

                if (operation.getUpdateProductMediaAltTextCommands() != null && operation.getUpdateProductMediaAltTextCommands().size() > i) {
                    translationMap.put(TranslationKeys.MEDIA_ALT_TEXT.with(i),
                            HstoreTranslationDto.ofNullable(operation.getUpdateProductMediaAltTextCommands().get(j).translations()));
                }

                if (file.getContentType() == null ||
                        (!file.getContentType().startsWith("image") &&
                                !file.getContentType().startsWith("video"))) {
                    return UpdateProduct.Result.invalidMediaType(file.getContentType());
                }

                newProductMedia.setUrl(ProductMediaUrl.of(UUID.randomUUID().toString()));

                if (file.getContentType().startsWith("image")) {
                    newProductMedia.setMediaType(MediaType.IMAGE);
                    preloadContentSet.add(new PreloadContentInfo<>(newProductMedia, file, FileStorageFolders.PRODUCT_IMAGES.getValue()));
                } else if (file.getContentType().startsWith("video")) {
                    newProductMedia.setMediaType(MediaType.VIDEO);
                    preloadContentSet.add(new PreloadContentInfo<>(newProductMedia, file, FileStorageFolders.PRODUCT_VIDEOS.getValue()));
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

        /* ========== Translation ========== */
        Map<String, HstoreTranslationDto> resultMap;

        try {
            resultMap = translationRepository.expand(translationMap);
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

        List<ProductCharacteristic> oldProductCharacteristics = productCharacteristicRepository.getAllByProductId(product.getId());

        try {
            productCharacteristicRepository.deleteAll(oldProductCharacteristics);
            productCharacteristicRepository.saveAll(productCharacteristicSet);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.errorSavingProduct(e);
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

        List<ProductFaq> oldProductFaq = productFaqRepository.getAllByProductId(product.getId());

        try {
            productFaqRepository.deleteAll(oldProductFaq);
            productFaqRepository.saveAll(productFaqSet);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.errorSavingProduct(e);
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

        List<ProductDeliveryMethodDetails> oldProductDeliveryMethodDetails = productDeliveryMethodDetailsRepository.getAllByProductId(product.getId());

        try {
            productDeliveryMethodDetailsRepository.deleteAll(oldProductDeliveryMethodDetails);
            productDeliveryMethodDetailsRepository.saveAll(productDeliveryMethodDetailsSet);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.errorSavingProduct(e);
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

        List<ProductPackageOption> oldProductPackageOptions = productPackageOptionsRepository.getAllByProductId(product.getId());

        try {
            productPackageOptionsRepository.deleteAll(oldProductPackageOptions);
            productPackageOptionsRepository.saveAll(productPackageOptionSet);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.errorSavingProduct(e);
        }

        product.getPackageOptions().clear();
        product.getPackageOptions().addAll(productPackageOptionSet);

        // Media
        if (operation.getUpdateProductMediaAltTextCommands() != null) {
            for (int i = 0; i < operation.getUpdateProductMediaAltTextCommands().size(); i++) {
                if (productMediaList.size() > i) {
                    ProductMedia productMedia = productMediaList.get(i);
                    HstoreTranslationDto translatedMediaAltText = resultMap.get(TranslationKeys.MEDIA_ALT_TEXT.with(i));
                    productMedia.getAltText().setTranslations(translatedMediaAltText);
                }
            }
        }

        List<ProductMedia> oldProductMedia = productMediaRepository.getAllByProductId(product.getId());

        List<ProductMedia> mergedProductMedia = productMediaList.stream()
                .filter(m -> !oldProductMedia.contains(m))
                .toList();

        try {
            productMediaRepository.deleteAll(oldProductMedia);
            productMediaRepository.saveAll(productMediaList);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.errorSavingProduct(e);
        }

        product.setMedia(new HashSet<>(mergedProductMedia));

        /* ========== Save Data ========== */
        try {
            productRepository.save(product);
            productRepository.flush();
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.errorSavingProduct(e);
        }

        try {
            for (PreloadContentInfo<?> contentInfo : preloadContentSet) {
                if (contentInfo.entity() instanceof ProductMedia) {
                    ProductMedia productMedia = productMediaList.stream()
                            .filter(m -> m.getUrl() != null && m.getUrl().toString().equals(((ProductMedia) contentInfo.entity()).getUrl().toString()))
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
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.errorSavingFiles(e);
        }

        try {
            List<String> linksForDelete = new ArrayList<>();

            for (Object entity : mediaForDeleteSet) {
                if (entity instanceof ProductMedia productMedia) {
                    linksForDelete.add(productMedia.getUrl().toString());
                }
            }

            fileStorageRepository.deleteMediaByLink(linksForDelete.toArray(new String[0]));
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.errorDeletingFiles(e);
        }

        try {
            productRepository.save(product);
            productSummaryCacheManager.clearAll();
            productCacheManager.clearById(operation.getProductId());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.errorSavingProduct(e);
        }

        generalCacheService.clear();

        return UpdateProduct.Result.success();
    }

    @Override
    @Transactional(readOnly = true)
    public GetSearchHints.Result getSearchHints(GetSearchHints operation) {
        List<SearchHintView> searchHintViews = productRepository.findHintViews(
                operation.getSearchTerm(), operation.getVendorId(), operation.getLocale());

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

    @Transactional(readOnly = true)
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
        CategoryDto categoryDtoFromCache = categoryCacheManager.getCategory(view.getCategoryId(), locale);

        if (categoryDtoFromCache != null) {
            productDto.setCategory(categoryDtoFromCache);
        } else {
            Optional<CategoryView> categoryView = categoryRepository.getCategoryViewByIdAndLang(
                    view.getCategoryId(),
                    locale.getLanguage()
            );

            if (categoryView.isPresent()) {
                CategoryDto categoryDto = CategoryDto.ofWithoutChildren(categoryView.get());
                productDto.setCategory(categoryDto);
            }
        }

        // Media
        List<ProductMediaView> productMediaList = productMediaRepository.getAllViewsByProductIdAndLang(
                productDto.getId(),
                locale.getLanguage()
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
                productDto.getId(), locale
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

        return localizationManager.localizePrice(productDto, locale);
    }

    @Override
    @Transactional
    public DeleteProductById.Result deleteProductById(DeleteProductById operation) {
        Optional<Product> product = productRepository.getProductById(operation.getProductId());

        if (product.isEmpty()) {
            return DeleteProductById.Result.notFound(operation.getProductId());
        }

        try {
            productRepository.delete(product.get());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DeleteProductById.Result.deleteError(e);
        }

        productSummaryCacheManager.clearAll();
        productCacheManager.clearById(operation.getProductId());
        generalCacheService.clear();
        return DeleteProductById.Result.success(operation.getProductId());
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    protected ProductWithTranslationsDto loadFullProduct(ProductWithTranslationsDto productDto, ProductWithTranslationsView view, Locale locale) {
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
        List<ProductMediaWithTranslationsView> productMediaList = productMediaRepository.getAllViewsWithTranslationsByProductIdAndLang(
                productDto.getId(), locale.getLanguage());
        List<ProductMediaWithTranslationsDto> productMediaDtoList = productMediaList.stream()
                .map(ProductMediaWithTranslationsDto::of)
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
        List<ProductCharacteristicWithTranslationsView> productCharacteristicList = productCharacteristicRepository
                .findAllViewsWithTranslationsByProductIdAndLang(productDto.getId(), locale.getLanguage());
        List<ProductCharacteristicWithTranslationsDto> productCharacteristicDtoList = productCharacteristicList.stream()
                .map(ProductCharacteristicWithTranslationsDto::of)
                .toList();
        productDto.setCharacteristics(productCharacteristicDtoList);

        // Faq
        List<ProductFaqWithTranslationsView> productFaqList = productFaqRepository
                .findAllWithTranslationsByProductIdAndLang(productDto.getId(), locale.getLanguage());
        List<ProductFaqWithTranslationDto> productFaqDtoList = productFaqList.stream()
                .map(ProductFaqWithTranslationDto::of)
                .toList();
        productDto.setFaq(productFaqDtoList);

        // Prices
        List<ProductPriceView> productPriceList = productPriceRepository.findAllViewsByProductId(
                productDto.getId(), locale
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

        // Delivery Method Details
        List<ProductDeliveryMethodDetailsWithTranslationsView> productDeliveryMethodDetailsViewList = productDeliveryMethodDetailsRepository
                .getAllViewsWithTranslationsByProductIdAndLang(productDto.getId(), locale.getLanguage());
        List<ProductDeliveryMethodDetailsWithTranslationsDto> productDeliveryMethodDetailsDtoList = productDeliveryMethodDetailsViewList.stream()
                .map(ProductDeliveryMethodDetailsWithTranslationsDto::of).toList();
        productDto.setDeliveryMethodsDetails(productDeliveryMethodDetailsDtoList);

        // Packaging Options
        List<ProductPackageOptionWithTranslationsView> productPackageOptionViewList = productPackageOptionsRepository
                .getAllViewsWithTranslationsByProductIdAndLang(productDto.getId(), locale.getLanguage());
        List<ProductPackageOptionWithTranslationsDto> productPackageOptionDtoList = productPackageOptionViewList.stream()
                .map(ProductPackageOptionWithTranslationsDto::of).toList();
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void saveProduct(Product product) {
        productRepository.save(product);
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
        MEDIA_ALT_TEXT,
        VENDOR_DETAILS_MEDIA_ALT_TEXT,
        PRICE_UNIT;

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