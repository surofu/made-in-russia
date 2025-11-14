package com.surofu.exporteru.application.service;

import com.surofu.exporteru.application.cache.CategoryCacheManager;
import com.surofu.exporteru.application.cache.GeneralCacheService;
import com.surofu.exporteru.application.cache.ProductCacheManager;
import com.surofu.exporteru.application.cache.ProductSummaryCacheManager;
import com.surofu.exporteru.application.command.product.create.*;
import com.surofu.exporteru.application.command.product.update.*;
import com.surofu.exporteru.application.dto.DeliveryMethodDto;
import com.surofu.exporteru.application.dto.SearchHintDto;
import com.surofu.exporteru.application.dto.category.CategoryDto;
import com.surofu.exporteru.application.dto.category.CategoryHintDto;
import com.surofu.exporteru.application.dto.product.*;
import com.surofu.exporteru.application.dto.translation.HstoreTranslationDto;
import com.surofu.exporteru.application.dto.translation.TranslationDto;
import com.surofu.exporteru.application.dto.vendor.VendorCountryDto;
import com.surofu.exporteru.application.dto.vendor.VendorDto;
import com.surofu.exporteru.application.dto.vendor.VendorFaqDto;
import com.surofu.exporteru.application.dto.vendor.VendorProductCategoryDto;
import com.surofu.exporteru.application.enums.FileStorageFolders;
import com.surofu.exporteru.application.exception.EmptyTranslationException;
import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.model.category.Category;
import com.surofu.exporteru.core.model.category.CategorySlug;
import com.surofu.exporteru.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.exporteru.core.model.media.MediaType;
import com.surofu.exporteru.core.model.moderation.ApproveStatus;
import com.surofu.exporteru.core.model.product.*;
import com.surofu.exporteru.core.model.product.characteristic.ProductCharacteristic;
import com.surofu.exporteru.core.model.product.characteristic.ProductCharacteristicName;
import com.surofu.exporteru.core.model.product.characteristic.ProductCharacteristicValue;
import com.surofu.exporteru.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetails;
import com.surofu.exporteru.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetailsName;
import com.surofu.exporteru.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetailsValue;
import com.surofu.exporteru.core.model.product.faq.ProductFaq;
import com.surofu.exporteru.core.model.product.faq.ProductFaqAnswer;
import com.surofu.exporteru.core.model.product.faq.ProductFaqQuestion;
import com.surofu.exporteru.core.model.product.media.*;
import com.surofu.exporteru.core.model.product.packageOption.ProductPackageOption;
import com.surofu.exporteru.core.model.product.packageOption.ProductPackageOptionName;
import com.surofu.exporteru.core.model.product.packageOption.ProductPackageOptionPrice;
import com.surofu.exporteru.core.model.product.packageOption.ProductPackageOptionPriceUnit;
import com.surofu.exporteru.core.model.product.price.*;
import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.model.user.UserRole;
import com.surofu.exporteru.core.repository.*;
import com.surofu.exporteru.core.service.product.ProductService;
import com.surofu.exporteru.core.service.product.operation.*;
import com.surofu.exporteru.infrastructure.persistence.category.CategoryView;
import com.surofu.exporteru.infrastructure.persistence.deliveryMethod.DeliveryMethodView;
import com.surofu.exporteru.infrastructure.persistence.product.ProductView;
import com.surofu.exporteru.infrastructure.persistence.product.ProductWithTranslationsView;
import com.surofu.exporteru.infrastructure.persistence.product.SearchHintView;
import com.surofu.exporteru.infrastructure.persistence.product.SimilarProductView;
import com.surofu.exporteru.infrastructure.persistence.product.characteristic.ProductCharacteristicView;
import com.surofu.exporteru.infrastructure.persistence.product.characteristic.ProductCharacteristicWithTranslationsView;
import com.surofu.exporteru.infrastructure.persistence.product.deliveryMethodDetails.ProductDeliveryMethodDetailsView;
import com.surofu.exporteru.infrastructure.persistence.product.deliveryMethodDetails.ProductDeliveryMethodDetailsWithTranslationsView;
import com.surofu.exporteru.infrastructure.persistence.product.faq.ProductFaqView;
import com.surofu.exporteru.infrastructure.persistence.product.faq.ProductFaqWithTranslationsView;
import com.surofu.exporteru.infrastructure.persistence.product.media.ProductMediaView;
import com.surofu.exporteru.infrastructure.persistence.product.media.ProductMediaWithTranslationsView;
import com.surofu.exporteru.infrastructure.persistence.product.packageOption.ProductPackageOptionView;
import com.surofu.exporteru.infrastructure.persistence.product.packageOption.ProductPackageOptionWithTranslationsView;
import com.surofu.exporteru.infrastructure.persistence.product.price.ProductPriceView;
import com.surofu.exporteru.infrastructure.persistence.product.review.media.ProductReviewMediaView;
import com.surofu.exporteru.infrastructure.persistence.user.UserView;
import com.surofu.exporteru.infrastructure.persistence.vendor.country.VendorCountryView;
import com.surofu.exporteru.infrastructure.persistence.vendor.faq.VendorFaqView;
import com.surofu.exporteru.infrastructure.persistence.vendor.productCategory.VendorProductCategoryView;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

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

        Optional<Product> productWithUserOptional = productRepository.getProductWithUserById(operation.getProductId());

        if (productWithUserOptional.isEmpty()) {
            return GetProductById.Result.notFound(operation.getProductId());
        }

        Product productWithUser = productWithUserOptional.get();

        if (operation.getSecurityUser() != null) {
            Long currentUserId = operation.getSecurityUser().getUser().getId();
            Long ownerId = productWithUser.getUser().getId();

            if (currentUserId.equals(ownerId) ||
                    operation.getSecurityUser().getUser().getRole().equals(UserRole.ROLE_ADMIN)) {
                approveStatuses.add(ApproveStatus.PENDING);
                approveStatuses.add(ApproveStatus.REJECTED);
            }
        }

        Optional<ProductView> productView = productRepository.getProductViewByIdAndLangAndApproveStatuses(
                operation.getProductId(), operation.getLocale().getLanguage(), approveStatuses
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
    public GetProductWithTranslationsById.Result getProductWithTranslationsByProductId(GetProductWithTranslationsById operation) {
        Optional<Product> productWithUserOptional = productRepository.getProductWithUserById(operation.getId());

        if (productWithUserOptional.isEmpty()) {
            return GetProductWithTranslationsById.Result.notFound(operation.getId());
        }

        Product productWithUser = productWithUserOptional.get();

        if (operation.getSecurityUser() != null) {
            Long currentUserId = operation.getSecurityUser().getUser().getId();
            Long ownerId = productWithUser.getUser().getId();

            if (!currentUserId.equals(ownerId) && !operation.getSecurityUser().getUser().getRole().equals(UserRole.ROLE_ADMIN)) {
                return GetProductWithTranslationsById.Result.notFound(operation.getId());
            }
        }

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

        // Валидация входных данных
        CreateProduct.Result validationResult = validateOperation(operation);
        if (validationResult != null) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return validationResult;
        }

        Product product = new Product();
        product.setUser(operation.getSecurityUser().getUser());

        // Подготовка переводов
        Map<String, HstoreTranslationDto> translationMap = prepareTranslations(operation);

        // Установка базовых свойств
        setBasicProductProperties(product, operation);

        // Установка категории
        Optional<Category> category = categoryRepository.getCategoryById(operation.getCategoryId());
        if (category.isEmpty()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CreateProduct.Result.categoryNotFound(operation.getCategoryId());
        }
        product.setCategory(category.get());

        // Установка методов доставки
        CreateProduct.Result deliveryMethodResult = setDeliveryMethods(product, operation);
        if (deliveryMethodResult != null) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return deliveryMethodResult;
        }

        // Создание цен продукта
        CreateProduct.Result priceResult = createProductPrices(product, operation);
        if (priceResult != null) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return priceResult;
        }

        // Установка похожих продуктов
        CreateProduct.Result similarProductsResult = setSimilarProducts(product, operation);
        if (similarProductsResult != null) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return similarProductsResult;
        }

        // Расширение переводов
        Map<String, HstoreTranslationDto> resultMap;
        try {
            resultMap = translationRepository.expand(translationMap);
        } catch (EmptyTranslationException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CreateProduct.Result.emptyTranslation(e.getMessage());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CreateProduct.Result.translationError(e);
        }

        // Применение переводов
        applyTranslations(product, operation, resultMap);

        // Загрузка медиа
        CreateProduct.Result mediaResult = uploadProductMedia(product, operation, resultMap);
        if (mediaResult != null) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return mediaResult;
        }

        // Сохранение продукта и связанных сущностей
        try {
            saveProductAndRelatedEntities(product);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CreateProduct.Result.errorSavingProduct(e);
        }

        productSummaryCacheManager.clearAll();
        generalCacheService.clear();
        return CreateProduct.Result.success();
    }

    private CreateProduct.Result validateOperation(CreateProduct operation) {
        if (operation.getCategoryId() == null) {
            throw new IllegalArgumentException("Категория товара не может быть пустой");
        }

        if (operation.getDeliveryMethodIds() == null || operation.getDeliveryMethodIds().isEmpty()) {
            throw new IllegalArgumentException("Способы доставки товара не могут быть пустыми");
        }

        return null;
    }

    private Map<String, HstoreTranslationDto> prepareTranslations(CreateProduct operation) {
        Map<String, HstoreTranslationDto> translationMap = new HashMap<>();

        translationMap.put(TranslationKeys.TITLE.name(), operation.getProductTitle().getTranslations());
        translationMap.put(TranslationKeys.MAIN_DESCRIPTION.name(),
                operation.getProductDescription().getMainDescriptionTranslations());

        if (StringUtils.trimToNull(operation.getProductDescription().getFurtherDescription()) != null) {
            if (isTranslationsPresent(operation.getProductDescription().getFurtherDescriptionTranslations())) {
                translationMap.put(TranslationKeys.FURTHER_DESCRIPTION.name(),
                        operation.getProductDescription().getFurtherDescriptionTranslations());
            }
        }

        addCharacteristicTranslations(translationMap, operation.getCreateProductCharacteristicCommands());
        addFaqTranslations(translationMap, operation.getCreateProductFaqCommands());
        addDeliveryMethodDetailsTranslations(translationMap, operation.getCreateProductDeliveryMethodDetailsCommands());
        addPackageOptionTranslations(translationMap, operation.getCreateProductPackageOptionCommands());
        addMediaAltTextTranslations(translationMap, operation.getCreateProductMediaAltTextCommands());

        return translationMap;
    }

    private void addCharacteristicTranslations(Map<String, HstoreTranslationDto> translationMap,
                                               List<CreateProductCharacteristicCommand> commands) {
        for (int i = 0; i < commands.size(); i++) {
            CreateProductCharacteristicCommand command = commands.get(i);
            if (isTranslationsPresent(command.nameTranslations()) && isTranslationsPresent(command.valueTranslations())) {
                translationMap.put(TranslationKeys.CHARACTERISTIC_NAME.with(i),
                        HstoreTranslationDto.ofNullable(command.nameTranslations()));
                translationMap.put(TranslationKeys.CHARACTERISTIC_VALUE.with(i),
                        HstoreTranslationDto.ofNullable(command.valueTranslations()));
            }
        }
    }

    private void addFaqTranslations(Map<String, HstoreTranslationDto> translationMap,
                                    List<CreateProductFaqCommand> commands) {
        for (int i = 0; i < commands.size(); i++) {
            CreateProductFaqCommand command = commands.get(i);
            translationMap.put(TranslationKeys.FAQ_QUESTION.with(i),
                    HstoreTranslationDto.ofNullable(command.questionTranslations()));
            translationMap.put(TranslationKeys.FAQ_ANSWER.with(i),
                    HstoreTranslationDto.ofNullable(command.answerTranslations()));
        }
    }

    private void addDeliveryMethodDetailsTranslations(Map<String, HstoreTranslationDto> translationMap,
                                                      List<CreateProductDeliveryMethodDetailsCommand> commands) {
        for (int i = 0; i < commands.size(); i++) {
            CreateProductDeliveryMethodDetailsCommand command = commands.get(i);
            if (isTranslationsPresent(command.nameTranslations()) && isTranslationsPresent(command.valueTranslations())) {
                translationMap.put(TranslationKeys.DELIVERY_METHOD_DETAILS_NAME.with(i),
                        HstoreTranslationDto.ofNullable(command.nameTranslations()));
                translationMap.put(TranslationKeys.DELIVERY_METHOD_DETAILS_VALUE.with(i),
                        HstoreTranslationDto.ofNullable(command.valueTranslations()));
            }
        }
    }

    private void addPackageOptionTranslations(Map<String, HstoreTranslationDto> translationMap,
                                              List<CreateProductPackageOptionCommand> commands) {
        for (int i = 0; i < commands.size(); i++) {
            CreateProductPackageOptionCommand command = commands.get(i);
            if (isTranslationsPresent(command.nameTranslations())) {
                translationMap.put(TranslationKeys.PACKAGE_OPTIONS_NAME.with(i),
                        HstoreTranslationDto.ofNullable(command.nameTranslations()));
            }
        }
    }

    private void addMediaAltTextTranslations(Map<String, HstoreTranslationDto> translationMap,
                                             List<CreateProductMediaAltTextCommand> commands) {
        for (int i = 0; i < commands.size(); i++) {
            CreateProductMediaAltTextCommand command = commands.get(i);
            if (isTranslationsPresent(command.translations())) {
                translationMap.put(TranslationKeys.MEDIA_ALT_TEXT.with(i),
                        HstoreTranslationDto.ofNullable(command.translations()));
            }
        }
    }

    private void setBasicProductProperties(Product product, CreateProduct operation) {
        product.setTitle(operation.getProductTitle());
        product.setDescription(operation.getProductDescription());
        product.setMinimumOrderQuantity(ProductMinimumOrderQuantity.of(operation.getMinimumOrderQuantity()));
        product.setDiscountExpirationDate(ProductDiscountExpirationDate.of(operation.getDiscountExpirationDate()));
    }

    private CreateProduct.Result setDeliveryMethods(Product product, CreateProduct operation) {
        Optional<Long> firstNotExistsDeliveryMethodId =
                deliveryMethodRepository.firstNotExists(operation.getDeliveryMethodIds());
        if (firstNotExistsDeliveryMethodId.isPresent()) {
            return CreateProduct.Result.deliveryMethodNotFound(firstNotExistsDeliveryMethodId.get());
        }

        List<DeliveryMethod> deliveryMethodList =
                deliveryMethodRepository.getAllDeliveryMethodsByIds(operation.getDeliveryMethodIds());
        product.setDeliveryMethods(new HashSet<>(deliveryMethodList));
        return null;
    }

    private CreateProduct.Result createProductPrices(Product product, CreateProduct operation) {
        List<ProductPrice> productPriceList = new ArrayList<>();
        List<String> unitList = new ArrayList<>();

        for (CreateProductPriceCommand command : operation.getCreateProductPriceCommands()) {
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
        return null;
    }

    private CreateProduct.Result setSimilarProducts(Product product, CreateProduct operation) {
        Optional<Long> firstNotExistsSimilarProductId =
                productRepository.firstNotExists(operation.getSimilarProductIds());
        if (firstNotExistsSimilarProductId.isPresent()) {
            return CreateProduct.Result.similarProductNotFound(firstNotExistsSimilarProductId.get());
        }

        List<Product> similarProducts = productRepository.findAllByIds(operation.getSimilarProductIds());
        product.setSimilarProducts(new HashSet<>(similarProducts));
        return null;
    }

    private void applyTranslations(Product product, CreateProduct operation,
                                   Map<String, HstoreTranslationDto> resultMap) {
        applyTitleAndDescriptionTranslations(product, resultMap);
        applyCharacteristicTranslations(product, operation, resultMap);
        applyFaqTranslations(product, operation, resultMap);
        applyDeliveryMethodDetailsTranslations(product, operation, resultMap);
        applyPackageOptionTranslations(product, operation, resultMap);
    }

    private void applyTitleAndDescriptionTranslations(Product product,
                                                      Map<String, HstoreTranslationDto> resultMap) {
        HstoreTranslationDto translatedTitle = resultMap.get(TranslationKeys.TITLE.name());
        product.getTitle().setTranslations(translatedTitle);

        HstoreTranslationDto translatedMainDescription = resultMap.get(TranslationKeys.MAIN_DESCRIPTION.name());
        HstoreTranslationDto translatedFurtherDescription = resultMap.get(TranslationKeys.FURTHER_DESCRIPTION.name());
        product.getDescription().setMainDescriptionTranslations(translatedMainDescription);
        product.getDescription().setFurtherDescriptionTranslations(translatedFurtherDescription);
    }

    private void applyCharacteristicTranslations(Product product, CreateProduct operation,
                                                 Map<String, HstoreTranslationDto> resultMap) {
        Set<ProductCharacteristic> characteristicSet = new HashSet<>();
        List<CreateProductCharacteristicCommand> commands = operation.getCreateProductCharacteristicCommands();

        for (int i = 0; i < commands.size(); i++) {
            if (!resultMap.containsKey(TranslationKeys.CHARACTERISTIC_NAME.with(i))) {
                continue;
            }

            CreateProductCharacteristicCommand command = commands.get(i);
            ProductCharacteristic characteristic = createProductCharacteristic(command, product);

            HstoreTranslationDto translatedName = resultMap.get(TranslationKeys.CHARACTERISTIC_NAME.with(i));
            HstoreTranslationDto translatedValue = resultMap.get(TranslationKeys.CHARACTERISTIC_VALUE.with(i));

            characteristic.getName().setTranslations(translatedName);
            characteristic.getValue().setTranslations(translatedValue);
            characteristicSet.add(characteristic);
        }
        product.setCharacteristics(characteristicSet);
    }

    private void applyFaqTranslations(Product product, CreateProduct operation,
                                      Map<String, HstoreTranslationDto> resultMap) {
        Set<ProductFaq> faqSet = new HashSet<>();
        List<CreateProductFaqCommand> commands = operation.getCreateProductFaqCommands();

        for (int i = 0; i < commands.size(); i++) {
            if (!resultMap.containsKey(TranslationKeys.FAQ_QUESTION.with(i))) {
                continue;
            }

            CreateProductFaqCommand command = commands.get(i);
            ProductFaq faq = createProductFaq(command, product);

            HstoreTranslationDto translatedQuestion = resultMap.get(TranslationKeys.FAQ_QUESTION.with(i));
            HstoreTranslationDto translatedAnswer = resultMap.get(TranslationKeys.FAQ_ANSWER.with(i));

            faq.getQuestion().setTranslations(translatedQuestion);
            faq.getAnswer().setTranslations(translatedAnswer);
            faqSet.add(faq);
        }
        product.setFaq(faqSet);
    }

    private void applyDeliveryMethodDetailsTranslations(Product product, CreateProduct operation,
                                                        Map<String, HstoreTranslationDto> resultMap) {
        Set<ProductDeliveryMethodDetails> detailsSet = new HashSet<>();
        List<CreateProductDeliveryMethodDetailsCommand> commands =
                operation.getCreateProductDeliveryMethodDetailsCommands();

        for (int i = 0; i < commands.size(); i++) {
            if (!resultMap.containsKey(TranslationKeys.DELIVERY_METHOD_DETAILS_NAME.with(i))) {
                continue;
            }

            CreateProductDeliveryMethodDetailsCommand command = commands.get(i);
            ProductDeliveryMethodDetails details = createProductDeliveryMethodDetails(command, product);

            HstoreTranslationDto translatedName = resultMap.get(TranslationKeys.DELIVERY_METHOD_DETAILS_NAME.with(i));
            HstoreTranslationDto translatedValue = resultMap.get(TranslationKeys.DELIVERY_METHOD_DETAILS_VALUE.with(i));

            details.getName().setTranslations(translatedName);
            details.getValue().setTranslations(translatedValue);
            detailsSet.add(details);
        }
        product.setDeliveryMethodDetails(detailsSet);
    }

    private void applyPackageOptionTranslations(Product product, CreateProduct operation,
                                                Map<String, HstoreTranslationDto> resultMap) {
        Set<ProductPackageOption> optionSet = new HashSet<>();
        List<CreateProductPackageOptionCommand> commands = operation.getCreateProductPackageOptionCommands();

        for (int i = 0; i < commands.size(); i++) {
            if (!resultMap.containsKey(TranslationKeys.PACKAGE_OPTIONS_NAME.with(i))) {
                continue;
            }

            CreateProductPackageOptionCommand command = commands.get(i);
            ProductPackageOption option = createProductPackageOption(command, product);

            HstoreTranslationDto translatedName = resultMap.get(TranslationKeys.PACKAGE_OPTIONS_NAME.with(i));
            option.getName().setTranslations(translatedName);
            optionSet.add(option);
        }
        product.setPackageOptions(optionSet);
    }

    private CreateProduct.Result uploadProductMedia(Product product, CreateProduct operation,
                                                    Map<String, HstoreTranslationDto> resultMap) {
        List<CompletableFuture<String>> mediaFutureList = new ArrayList<>(operation.getProductMedia().size());
        List<ProductMedia> productMediaList = new ArrayList<>(operation.getProductMedia().size());

        for (int i = 0; i < operation.getProductMedia().size(); i++) {
            MultipartFile file = operation.getProductMedia().get(i);

            if (file.getContentType() == null) {
                return CreateProduct.Result.invalidMediaType("null");
            }

            ProductMedia productMedia;

            try {
                productMedia = createProductMedia(file, product, i, operation, resultMap);
            } catch (IllegalArgumentException e) {
                return CreateProduct.Result.invalidMediaType(file.getContentType());
            }

            FileStorageFolders folderName = determineMediaFolder(file);
            if (folderName == null) {
                return CreateProduct.Result.invalidMediaType(file.getContentType());
            }

            CompletableFuture<String> mediaFuture = uploadMediaAsync(file, folderName);
            productMediaList.add(productMedia);
            mediaFutureList.add(mediaFuture);
        }

        product.setMedia(new HashSet<>(productMediaList));

        return waitForMediaUploads(productMediaList, mediaFutureList, product);
    }

    private ProductMedia createProductMedia(MultipartFile file, Product product, int index,
                                            CreateProduct operation, Map<String, HstoreTranslationDto> resultMap) throws IllegalArgumentException {
        ProductMedia productMedia = new ProductMedia();
        productMedia.setProduct(product);
        productMedia.setMimeType(ProductMediaMimeType.of(file.getContentType()));
        productMedia.setPosition(ProductMediaPosition.of(index));

        ProductMediaAltText altText = ProductMediaAltText.of(file.getOriginalFilename());
        HstoreTranslationDto altTextTranslations = getAltTextTranslations(file, index, operation, resultMap);
        altText.setTranslations(altTextTranslations);
        productMedia.setAltText(altText);

        String contentType = file.getContentType();

        if (contentType == null) {
            throw new IllegalArgumentException("Empty content type");
        }

        if (contentType.startsWith("image/")) {
            productMedia.setMediaType(MediaType.IMAGE);
        } else if (contentType.startsWith("video/")) {
            productMedia.setMediaType(MediaType.VIDEO);
        } else {
            throw new IllegalArgumentException("Unsupported content type: " + contentType);
        }

        return productMedia;
    }

    private HstoreTranslationDto getAltTextTranslations(MultipartFile file, int index,
                                                        CreateProduct operation,
                                                        Map<String, HstoreTranslationDto> resultMap) {
        if (index < operation.getCreateProductMediaAltTextCommands().size()) {
            HstoreTranslationDto translatedAltText = resultMap.get(TranslationKeys.MEDIA_ALT_TEXT.with(index));
            if (translatedAltText != null) {
                return translatedAltText;
            }
        }

        String filename = file.getOriginalFilename();
        return new HstoreTranslationDto(filename, filename, filename);
    }

    private FileStorageFolders determineMediaFolder(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType == null) {
            return null;
        }

        if (contentType.startsWith("image/")) {
            return FileStorageFolders.PRODUCT_IMAGES;
        } else if (contentType.startsWith("video/")) {
            return FileStorageFolders.PRODUCT_VIDEOS;
        }
        return null;
    }

    private CompletableFuture<String> uploadMediaAsync(MultipartFile file, FileStorageFolders folderName) {
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return fileStorageRepository.uploadImageToFolder(file, folderName.getValue());
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                },
                appTaskExecutor
        );
    }

    private CreateProduct.Result waitForMediaUploads(List<ProductMedia> productMediaList,
                                                     List<CompletableFuture<String>> mediaFutureList,
                                                     Product product) {
        CompletableFuture<Void> allMediaFuture =
                CompletableFuture.allOf(mediaFutureList.toArray(CompletableFuture[]::new));

        try {
            allMediaFuture.join();
            for (int i = 0; i < mediaFutureList.size(); i++) {
                String url = mediaFutureList.get(i).get();
                productMediaList.get(i).setUrl(ProductMediaUrl.of(url));
                if (i == 0) {
                    product.setPreviewImageUrl(ProductPreviewImageUrl.of(url));
                }
            }
            return null;
        } catch (Exception e) {
            return CreateProduct.Result.errorSavingFiles(e);
        }
    }

    private void saveProductAndRelatedEntities(Product product) {
        productRepository.save(product);
        productCharacteristicRepository.saveAll(product.getCharacteristics());
        productFaqRepository.saveAll(product.getFaq());
        productPriceRepository.saveAll(product.getPrices());
        productDeliveryMethodDetailsRepository.saveAll(product.getDeliveryMethodDetails());
        productPackageOptionsRepository.saveAll(product.getPackageOptions());
        productMediaRepository.saveAll(product.getMedia());
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

        // Проверка прав доступа
        if (!hasPermissionToUpdate(product, operation.getSecurityUser())) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.invalidOwner(operation.getProductId(), operation.getSecurityUser().getUser().getLogin());
        }

        Map<String, HstoreTranslationDto> translationMap = new HashMap<>();

        // Обновление основных полей
        updateBasicFields(product, operation, translationMap);

        // Обновление категории
        UpdateProduct.Result categoryResult = updateCategory(product, operation);
        if (categoryResult != UpdateProduct.Result.Success.INSTANCE) {
            return categoryResult;
        }

        // Обновление методов доставки
        UpdateProduct.Result deliveryResult = updateDeliveryMethods(product, operation);
        if (deliveryResult != UpdateProduct.Result.Success.INSTANCE) {
            return deliveryResult;
        }

        // Обновление похожих продуктов
        UpdateProduct.Result similarProductsResult = updateSimilarProducts(product, operation);
        if (similarProductsResult != UpdateProduct.Result.Success.INSTANCE) {
            return similarProductsResult;
        }

        // Обновление цен
        UpdateProduct.Result pricesResult = updateProductPrices(product, operation);
        if (pricesResult != UpdateProduct.Result.Success.INSTANCE) {
            return pricesResult;
        }

        // Подготовка переводов для характеристик, FAQ и т.д.
        prepareTranslations(operation, translationMap);

        // Обработка медиа с правильными позициями
        MediaUpdateResult mediaResult = prepareMediaUpdate(product, operation, translationMap);
        if (mediaResult.error() != null) {
            return mediaResult.error();
        }

        // Получение переводов
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

        // Применение переводов
        applyTranslations(product, resultMap);

        // Обновление связанных сущностей
        UpdateProduct.Result relatedEntitiesResult = updateRelatedEntities(product, operation, resultMap);
        if (relatedEntitiesResult != UpdateProduct.Result.Success.INSTANCE) {
            return relatedEntitiesResult;
        }

        // Применение переводов к медиа
        applyMediaTranslations(mediaResult.productMediaList(), resultMap);

        // Сохранение медиа с правильными позициями
        UpdateProduct.Result mediaSaveResult = saveMediaChanges(mediaResult, product);
        if (mediaSaveResult != UpdateProduct.Result.Success.INSTANCE) {
            return mediaSaveResult;
        }

        // Сохранение продукта
        try {
            productRepository.save(product);
            productRepository.flush();
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.errorSavingProduct(e);
        }

        // Загрузка файлов и обновление URL
        UpdateProduct.Result fileUploadResult = uploadMediaFiles(mediaResult, product);
        if (fileUploadResult != UpdateProduct.Result.Success.INSTANCE) {
            return fileUploadResult;
        }

        // Удаление старых файлов
        UpdateProduct.Result fileDeletionResult = deleteOldMediaFiles(mediaResult.mediaToDelete());
        if (fileDeletionResult != UpdateProduct.Result.Success.INSTANCE) {
            return fileDeletionResult;
        }

        // Финальное сохранение и очистка кеша
        return finalizeUpdate(product, operation.getProductId());
    }

    private boolean hasPermissionToUpdate(Product product, SecurityUser securityUser) {
        return product.getUser().getId().equals(securityUser.getUser().getId())
                || securityUser.getUser().getRole() == UserRole.ROLE_ADMIN;
    }

    private void updateBasicFields(Product product, UpdateProduct operation, Map<String, HstoreTranslationDto> translationMap) {
        product.setTitle(operation.getProductTitle());
        translationMap.put(TranslationKeys.TITLE.name(), operation.getProductTitle().getTranslations());

        product.setDescription(ProductDescription.of(
                operation.getProductDescription().getMainDescription(),
                operation.getProductDescription().getFurtherDescription()
        ));

        translationMap.put(TranslationKeys.MAIN_DESCRIPTION.name(),
                operation.getProductDescription().getMainDescriptionTranslations());

        if (StringUtils.trimToNull(operation.getProductDescription().getFurtherDescription()) != null) {
            translationMap.put(TranslationKeys.FURTHER_DESCRIPTION.name(),
                    operation.getProductDescription().getFurtherDescriptionTranslations());
        }

        product.setMinimumOrderQuantity(ProductMinimumOrderQuantity.of(operation.getMinimumOrderQuantity()));
        product.setDiscountExpirationDate(ProductDiscountExpirationDate.of(operation.getDiscountExpirationDate()));
    }

    private UpdateProduct.Result updateCategory(Product product, UpdateProduct operation) {
        Category productCategory = product.getCategory();
        if (!productCategory.getId().equals(operation.getCategoryId())) {
            Optional<Category> newCategory = categoryRepository.getCategoryById(operation.getCategoryId());
            if (newCategory.isEmpty()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return UpdateProduct.Result.categoryNotFound(operation.getCategoryId());
            }
            product.setCategory(newCategory.get());
        }
        return UpdateProduct.Result.success();
    }

    private UpdateProduct.Result updateDeliveryMethods(Product product, UpdateProduct operation) {
        Set<Long> currentIds = product.getDeliveryMethods().stream()
                .map(DeliveryMethod::getId)
                .collect(Collectors.toSet());

        Set<Long> newIds = new HashSet<>(operation.getDeliveryMethodIds());

        if (!currentIds.equals(newIds)) {
            Optional<Long> notExists = deliveryMethodRepository.firstNotExists(operation.getDeliveryMethodIds());
            if (notExists.isPresent()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return UpdateProduct.Result.deliveryMethodNotFound(notExists.get());
            }

            List<DeliveryMethod> deliveryMethods = deliveryMethodRepository
                    .getAllDeliveryMethodsByIds(operation.getDeliveryMethodIds());
            product.setDeliveryMethods(new HashSet<>(deliveryMethods));
        }
        return UpdateProduct.Result.success();
    }

    private UpdateProduct.Result updateSimilarProducts(Product product, UpdateProduct operation) {
        Set<Long> currentIds = product.getSimilarProducts().stream()
                .map(Product::getId)
                .collect(Collectors.toSet());

        Set<Long> newIds = new HashSet<>(operation.getSimilarProductIds());

        if (!currentIds.equals(newIds)) {
            Optional<Long> notExists = productRepository.firstNotExists(operation.getSimilarProductIds());
            if (notExists.isPresent()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return UpdateProduct.Result.similarProductNotFound(notExists.get());
            }

            List<Product> similarProducts = productRepository.findAllByIds(operation.getSimilarProductIds());
            product.setSimilarProducts(new HashSet<>(similarProducts));
        }
        return UpdateProduct.Result.success();
    }

    private UpdateProduct.Result updateProductPrices(Product product, UpdateProduct operation) {
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

        List<ProductPrice> oldPrices = productPriceRepository.getAllByProductId(product.getId());

        try {
            productPriceRepository.deleteAll(oldPrices);
            productPriceRepository.saveAll(productPriceList);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.errorSavingProduct(e);
        }

        product.getPrices().clear();
        product.getPrices().addAll(productPriceList);

        return UpdateProduct.Result.success();
    }

    private void prepareTranslations(UpdateProduct operation, Map<String, HstoreTranslationDto> translationMap) {
        // Характеристики
        for (int i = 0; i < operation.getUpdateProductCharacteristicCommands().size(); i++) {
            UpdateProductCharacteristicCommand command = operation.getUpdateProductCharacteristicCommands().get(i);
            translationMap.put(TranslationKeys.CHARACTERISTIC_NAME.with(i),
                    HstoreTranslationDto.ofNullable(command.nameTranslations()));
            translationMap.put(TranslationKeys.CHARACTERISTIC_VALUE.with(i),
                    HstoreTranslationDto.ofNullable(command.valueTranslations()));
        }

        // FAQ
        for (int i = 0; i < operation.getUpdateProductFaqCommands().size(); i++) {
            UpdateProductFaqCommand command = operation.getUpdateProductFaqCommands().get(i);
            translationMap.put(TranslationKeys.FAQ_QUESTION.with(i),
                    HstoreTranslationDto.ofNullable(command.questionTranslations()));
            translationMap.put(TranslationKeys.FAQ_ANSWER.with(i),
                    HstoreTranslationDto.ofNullable(command.answerTranslations()));
        }

        // Детали методов доставки
        for (int i = 0; i < operation.getUpdateProductDeliveryMethodDetailsCommands().size(); i++) {
            UpdateProductDeliveryMethodDetailsCommand command = operation.getUpdateProductDeliveryMethodDetailsCommands().get(i);
            translationMap.put(TranslationKeys.DELIVERY_METHOD_DETAILS_NAME.with(i),
                    HstoreTranslationDto.ofNullable(command.nameTranslations()));
            translationMap.put(TranslationKeys.DELIVERY_METHOD_DETAILS_VALUE.with(i),
                    HstoreTranslationDto.ofNullable(command.valueTranslations()));
        }

        // Опции упаковки
        for (int i = 0; i < operation.getUpdateProductPackageOptionCommands().size(); i++) {
            UpdateProductPackageOptionCommand command = operation.getUpdateProductPackageOptionCommands().get(i);
            translationMap.put(TranslationKeys.PACKAGE_OPTIONS_NAME.with(i),
                    HstoreTranslationDto.ofNullable(command.nameTranslations()));
        }
    }

    private record MediaUpdateResult(
            List<ProductMedia> productMediaList,
            Set<ProductMedia> mediaToDelete,
            Set<PreloadContentInfo<?>> preloadContentSet,
            UpdateProduct.Result error
    ) {}

    private MediaUpdateResult prepareMediaUpdate(Product product, UpdateProduct operation,
                                                 Map<String, HstoreTranslationDto> translationMap) {

        // Получаем ID старых медиа, которые нужно сохранить
        List<Long> oldMediaIdsToKeep = operation.getOldProductMedia().stream()
                .map(UpdateOldMediaDto::id)
                .toList();

        Set<ProductMedia> allCurrentMedia = product.getMedia();

        // Создаем Map для быстрого доступа к старым медиа по ID
        Map<Long, ProductMedia> oldMediaMap = allCurrentMedia.stream()
                .filter(m -> oldMediaIdsToKeep.contains(m.getId()))
                .collect(Collectors.toMap(ProductMedia::getId, m -> m));

        // Медиа для удаления (те, которых нет в списке для сохранения)
        Set<ProductMedia> mediaToDelete = allCurrentMedia.stream()
                .filter(m -> !oldMediaIdsToKeep.contains(m.getId()))
                .collect(Collectors.toSet());

        List<ProductMedia> productMediaList = new ArrayList<>();
        Set<PreloadContentInfo<?>> preloadContentSet = new HashSet<>();
        List<MultipartFile> newMediaFiles = operation.getProductMedia();

        int currentPosition = 0;
        int newMediaIndex = 0;

        // Обрабатываем старые медиа в порядке, указанном в operation.getOldProductMedia()
        for (UpdateOldMediaDto oldMediaDto : operation.getOldProductMedia()) {
            ProductMedia oldMedia = oldMediaMap.get(oldMediaDto.id());
            if (oldMedia != null) {
                // Обновляем позицию для старого медиа
                oldMedia.setPosition(ProductMediaPosition.of(currentPosition));
                productMediaList.add(oldMedia);
                currentPosition++;
            }
        }

        // Добавляем новые медиа после старых
        for (MultipartFile file : newMediaFiles) {
            ProductMedia newMedia = new ProductMedia();
            newMedia.setProduct(product);
            newMedia.setPosition(ProductMediaPosition.of(currentPosition));

            String filename = file.getOriginalFilename();
            newMedia.setAltText(ProductMediaAltText.of(filename));
            newMedia.getAltText().setTranslations(
                    new HstoreTranslationDto(filename, filename, filename));

            // Применяем кастомный alt text, если он предоставлен
            if (operation.getUpdateProductMediaAltTextCommands() != null
                    && newMediaIndex < operation.getUpdateProductMediaAltTextCommands().size()) {
                UpdateProductMediaAltTextCommand altTextCommand =
                        operation.getUpdateProductMediaAltTextCommands().get(newMediaIndex);
                newMedia.setAltText(ProductMediaAltText.of(altTextCommand.altText()));

                translationMap.put(TranslationKeys.MEDIA_ALT_TEXT.with(currentPosition),
                        HstoreTranslationDto.ofNullable(altTextCommand.translations()));
            }

            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("image") && !contentType.startsWith("video"))) {
                return new MediaUpdateResult(null, null, null,
                        UpdateProduct.Result.invalidMediaType(contentType));
            }

            newMedia.setMimeType(ProductMediaMimeType.of(contentType));
            newMedia.setUrl(ProductMediaUrl.of(UUID.randomUUID().toString()));

            if (contentType.startsWith("image")) {
                newMedia.setMediaType(MediaType.IMAGE);
                preloadContentSet.add(new PreloadContentInfo<>(newMedia, file,
                        FileStorageFolders.PRODUCT_IMAGES.getValue()));
            } else if (contentType.startsWith("video")) {
                newMedia.setMediaType(MediaType.VIDEO);
                preloadContentSet.add(new PreloadContentInfo<>(newMedia, file,
                        FileStorageFolders.PRODUCT_VIDEOS.getValue()));
            }

            productMediaList.add(newMedia);
            newMediaIndex++;
            currentPosition++;
        }

        // Устанавливаем временный preview image URL, если есть медиа
        if (!productMediaList.isEmpty()) {
            product.setPreviewImageUrl(ProductPreviewImageUrl.of("TEMP_URL"));
        }

        return new MediaUpdateResult(productMediaList, mediaToDelete, preloadContentSet, null);
    }

    private void applyTranslations(Product product, Map<String, HstoreTranslationDto> resultMap) {
        // Заголовок
        HstoreTranslationDto translatedTitle = resultMap.get(TranslationKeys.TITLE.name());
        product.getTitle().setTranslations(translatedTitle);

        // Описание
        HstoreTranslationDto translatedMainDescription = resultMap.get(TranslationKeys.MAIN_DESCRIPTION.name());
        product.getDescription().setMainDescriptionTranslations(translatedMainDescription);

        if (resultMap.containsKey(TranslationKeys.FURTHER_DESCRIPTION.name())) {
            HstoreTranslationDto translatedFurtherDescription = resultMap.get(TranslationKeys.FURTHER_DESCRIPTION.name());
            product.getDescription().setFurtherDescriptionTranslations(translatedFurtherDescription);
        }
    }

    private UpdateProduct.Result updateRelatedEntities(Product product, UpdateProduct operation,
                                                       Map<String, HstoreTranslationDto> resultMap) {

        UpdateProduct.Result characteristicsResult = updateCharacteristics(product, operation, resultMap);
        if (characteristicsResult != UpdateProduct.Result.Success.INSTANCE) {
            return characteristicsResult;
        }

        UpdateProduct.Result faqResult = updateFaq(product, operation, resultMap);
        if (faqResult != UpdateProduct.Result.Success.INSTANCE) {
            return faqResult;
        }

        UpdateProduct.Result deliveryDetailsResult = updateDeliveryMethodDetails(product, operation, resultMap);
        if (deliveryDetailsResult != UpdateProduct.Result.Success.INSTANCE) {
            return deliveryDetailsResult;
        }

        UpdateProduct.Result packageOptionsResult = updatePackageOptions(product, operation, resultMap);
        if (packageOptionsResult != UpdateProduct.Result.Success.INSTANCE) {
            return packageOptionsResult;
        }

        return UpdateProduct.Result.success();
    }

    private UpdateProduct.Result updateCharacteristics(Product product, UpdateProduct operation,
                                                       Map<String, HstoreTranslationDto> resultMap) {

        Set<ProductCharacteristic> characteristics = new HashSet<>();

        for (int i = 0; i < operation.getUpdateProductCharacteristicCommands().size(); i++) {
            UpdateProductCharacteristicCommand command = operation.getUpdateProductCharacteristicCommands().get(i);
            ProductCharacteristic characteristic = createProductCharacteristic(command, product);

            HstoreTranslationDto translationName = resultMap.get(TranslationKeys.CHARACTERISTIC_NAME.with(i));
            HstoreTranslationDto translationValue = resultMap.get(TranslationKeys.CHARACTERISTIC_VALUE.with(i));

            characteristic.getName().setTranslations(translationName);
            characteristic.getValue().setTranslations(translationValue);
            characteristics.add(characteristic);
        }

        List<ProductCharacteristic> oldCharacteristics =
                productCharacteristicRepository.getAllByProductId(product.getId());

        try {
            productCharacteristicRepository.deleteAll(oldCharacteristics);
            productCharacteristicRepository.saveAll(characteristics);
            product.getCharacteristics().clear();
            product.getCharacteristics().addAll(characteristics);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.errorSavingProduct(e);
        }

        return UpdateProduct.Result.success();
    }

    private UpdateProduct.Result updateFaq(Product product, UpdateProduct operation,
                                           Map<String, HstoreTranslationDto> resultMap) {

        Set<ProductFaq> faqSet = new HashSet<>();

        for (int i = 0; i < operation.getUpdateProductFaqCommands().size(); i++) {
            UpdateProductFaqCommand command = operation.getUpdateProductFaqCommands().get(i);
            ProductFaq faq = createProductFaq(command, product);

            HstoreTranslationDto translationQuestion = resultMap.get(TranslationKeys.FAQ_QUESTION.with(i));
            HstoreTranslationDto translationAnswer = resultMap.get(TranslationKeys.FAQ_ANSWER.with(i));

            faq.getQuestion().setTranslations(translationQuestion);
            faq.getAnswer().setTranslations(translationAnswer);
            faqSet.add(faq);
        }

        List<ProductFaq> oldFaq = productFaqRepository.getAllByProductId(product.getId());

        try {
            productFaqRepository.deleteAll(oldFaq);
            productFaqRepository.saveAll(faqSet);
            product.getFaq().clear();
            product.getFaq().addAll(faqSet);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.errorSavingProduct(e);
        }

        return UpdateProduct.Result.success();
    }

    private UpdateProduct.Result updateDeliveryMethodDetails(Product product, UpdateProduct operation,
                                                             Map<String, HstoreTranslationDto> resultMap) {

        Set<ProductDeliveryMethodDetails> detailsSet = new HashSet<>();

        for (int i = 0; i < operation.getUpdateProductDeliveryMethodDetailsCommands().size(); i++) {
            UpdateProductDeliveryMethodDetailsCommand command =
                    operation.getUpdateProductDeliveryMethodDetailsCommands().get(i);
            ProductDeliveryMethodDetails details = createProductDeliveryMethodDetails(command, product);

            HstoreTranslationDto translationName =
                    resultMap.get(TranslationKeys.DELIVERY_METHOD_DETAILS_NAME.with(i));
            HstoreTranslationDto translationValue =
                    resultMap.get(TranslationKeys.DELIVERY_METHOD_DETAILS_VALUE.with(i));

            details.getName().setTranslations(translationName);
            details.getValue().setTranslations(translationValue);
            detailsSet.add(details);
        }

        List<ProductDeliveryMethodDetails> oldDetails =
                productDeliveryMethodDetailsRepository.getAllByProductId(product.getId());

        try {
            productDeliveryMethodDetailsRepository.deleteAll(oldDetails);
            productDeliveryMethodDetailsRepository.saveAll(detailsSet);
            product.getDeliveryMethodDetails().clear();
            product.getDeliveryMethodDetails().addAll(detailsSet);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.errorSavingProduct(e);
        }

        return UpdateProduct.Result.success();
    }

    private UpdateProduct.Result updatePackageOptions(Product product, UpdateProduct operation,
                                                      Map<String, HstoreTranslationDto> resultMap) {

        Set<ProductPackageOption> optionsSet = new HashSet<>();

        for (int i = 0; i < operation.getUpdateProductPackageOptionCommands().size(); i++) {
            UpdateProductPackageOptionCommand command = operation.getUpdateProductPackageOptionCommands().get(i);
            ProductPackageOption option = createProductPackageOption(command, product);

            HstoreTranslationDto translationName = resultMap.get(TranslationKeys.PACKAGE_OPTIONS_NAME.with(i));
            option.getName().setTranslations(translationName);
            optionsSet.add(option);
        }

        List<ProductPackageOption> oldOptions =
                productPackageOptionsRepository.getAllByProductId(product.getId());

        try {
            productPackageOptionsRepository.deleteAll(oldOptions);
            productPackageOptionsRepository.saveAll(optionsSet);
            product.getPackageOptions().clear();
            product.getPackageOptions().addAll(optionsSet);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.errorSavingProduct(e);
        }

        return UpdateProduct.Result.success();
    }

    private void applyMediaTranslations(List<ProductMedia> mediaList, Map<String, HstoreTranslationDto> resultMap) {
        for (int i = 0; i < mediaList.size(); i++) {
            ProductMedia media = mediaList.get(i);
            String translationKey = TranslationKeys.MEDIA_ALT_TEXT.with(i);

            if (resultMap.containsKey(translationKey)) {
                HstoreTranslationDto translatedAltText = resultMap.get(translationKey);
                media.getAltText().setTranslations(translatedAltText);
            }
        }
    }

    private UpdateProduct.Result saveMediaChanges(MediaUpdateResult mediaResult, Product product) {
        try {
            // Удаляем старые медиа
            productMediaRepository.deleteAll(mediaResult.mediaToDelete().stream()
                    .filter(Objects::nonNull)
                    .toList());

            // Сохраняем новые и обновленные медиа
            productMediaRepository.saveAll(mediaResult.productMediaList().stream()
                    .filter(Objects::nonNull)
                    .toList());

            // Обновляем коллекцию медиа в продукте
            product.getMedia().clear();
            product.getMedia().addAll(mediaResult.productMediaList());

            if (!mediaResult.productMediaList().isEmpty()) {
                ProductMedia firstMedia = mediaResult.productMediaList().get(0);
                product.setPreviewImageUrl(ProductPreviewImageUrl.of(firstMedia.getUrl().toString()));
            }

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.errorSavingProduct(e);
        }

        return UpdateProduct.Result.success();
    }

    private UpdateProduct.Result uploadMediaFiles(MediaUpdateResult mediaResult, Product product) {
        String TEMP_URL = "TEMP_URL";

        try {
            for (PreloadContentInfo<?> contentInfo : mediaResult.preloadContentSet()) {
                if (!(contentInfo.entity() instanceof ProductMedia pm)) {
                    continue;
                }

                ProductMedia productMedia = mediaResult.productMediaList().stream()
                        .filter(m -> m.getUrl() != null &&
                                m.getUrl().toString().equals(pm.getUrl().toString()))
                        .findFirst()
                        .orElseThrow();

                String url;
                if (productMedia.getMediaType().equals(MediaType.IMAGE)) {
                    url = fileStorageRepository.uploadImageToFolder(
                            contentInfo.file(), contentInfo.folderName());
                } else if (productMedia.getMediaType().equals(MediaType.VIDEO)) {
                    url = fileStorageRepository.uploadVideoToFolder(
                            contentInfo.file(), contentInfo.folderName());
                } else {
                    continue;
                }

                productMedia.setUrl(ProductMediaUrl.of(url));

                // Устанавливаем preview image из первого загруженного медиа
                if (product.getPreviewImageUrl().getValue().equals(TEMP_URL)) {
                    product.setPreviewImageUrl(ProductPreviewImageUrl.of(url));
                }
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.errorSavingFiles(e);
        }

        return UpdateProduct.Result.success();
    }

    private UpdateProduct.Result deleteOldMediaFiles(Set<ProductMedia> mediaToDelete) {
        try {
            List<String> linksForDelete = mediaToDelete.stream()
                    .filter(Objects::nonNull)
                    .map(media -> media.getUrl().toString())
                    .toList();

            if (!linksForDelete.isEmpty()) {
                fileStorageRepository.deleteMediaByLink(linksForDelete.toArray(new String[0]));
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.errorDeletingFiles(e);
        }

        return UpdateProduct.Result.success();
    }

    private UpdateProduct.Result finalizeUpdate(Product product, Long productId) {
        product.setApproveStatus(ApproveStatus.PENDING);

        try {
            productRepository.save(product);
            productSummaryCacheManager.clearAll();
            productCacheManager.clearById(productId);
            generalCacheService.clear();
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.errorSavingProduct(e);
        }

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
                                .fullSlug(getFullSlug(hint.getCategorySlug()))
                                .image(hint.getCategoryImage())
                                .build(),
                        LinkedHashMap::new,
                        Collectors.mapping(
                                hint -> {
                                    if (hint.getProductId() != null) {
                                        return ProductHintDto.builder()
                                                .id(hint.getProductId())
                                                .title(hint.getProductTitle())
                                                .image(hint.getProductImage())
                                                .build();
                                    }
                                    return null;
                                },
                                Collectors.toList()
                        )
                ));

        List<SearchHintDto> groupedSearchHints = groupedProductHint.entrySet().stream()
                .map(entry -> SearchHintDto.builder()
                        .category(entry.getKey())
                        .products(entry.getValue().stream().filter(Objects::nonNull).collect(Collectors.toList()))
                        .build()
                )
                .toList();

        return GetSearchHints.Result.success(groupedSearchHints);
    }

    private String getFullSlug(String slug) {
        StringBuilder fullSlug = new StringBuilder(getSlugWithoutLevel(slug));
        String parentSlug = slug;

        while (true) {
            Optional<Category> category = categoryRepository.getCategoryBySlug(CategorySlug.of(parentSlug));

            if (category.isEmpty() || category.get().getParent() == null) {
                break;
            }

            parentSlug = category.get().getParent().getSlug().toString();
            fullSlug.insert(0, getSlugWithoutLevel(parentSlug) + "/");
        }

        return fullSlug.toString();
    }

    private String getSlugWithoutLevel(String slug) {
        return slug.split("_")[1];
    }

    @Transactional(readOnly = true)
    protected ProductDto loadFullProduct(ProductDto productDto, ProductView view, Locale locale) {
        // Vendor
        Optional<User> userOptional = userRepository.getUserById(view.getUserId());
        if (userOptional.isPresent() && userOptional.get().getVendorDetails() != null) {
            VendorDto vendorDto = VendorDto.of(userOptional.get(), locale);

            // Vendor Countries
            List<VendorCountryView> vendorCountryViewList = vendorCountryRepository.getAllViewsByVendorDetailsIdAndLang(
                    userOptional.get().getVendorDetails().getId(),
                    locale.getLanguage()
            );
            List<VendorCountryDto> vendorCountryDtoList = vendorCountryViewList.stream()
                    .map(VendorCountryDto::of)
                    .toList();

            // Vendor Product Categories
            List<VendorProductCategoryView> vendorProductCategoryViewList = vendorProductCategoryRepository.getAllViewsByVendorDetailsIdAndLang(
                    userOptional.get().getVendorDetails().getId(),
                    locale.getLanguage()
            );
            List<VendorProductCategoryDto> vendorProductCategoryDtoList = vendorProductCategoryViewList.stream()
                    .map(VendorProductCategoryDto::of)
                    .toList();

            // Vendor Faq
            List<VendorFaqView> vendorFaqViewList = vendorFaqRepository.getAllViewsByVendorDetailsIdAndLang(
                    userOptional.get().getVendorDetails().getId(),
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

        log.info("Before localizePrice");
        return localizationManager.localizePrice(productDto, locale);
    }

    @Override
    @Transactional
    public DeleteProductById.Result deleteProductById(DeleteProductById operation) {
        Optional<Product> product = productRepository.getProductByIdWithAnyApproveStatus(operation.getProductId());

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
            VendorDto vendorDto = VendorDto.of(userView.get(), locale);

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
//        List<SimilarProductView> similarProductViewList = productRepository.getAllSimilarProductViewsByProductIdAndLang(
//                productDto.getId(),
//                locale.getLanguage()
//        );
//        List<SimilarProductDto> similarProductDtoList = similarProductViewList.stream()
//                .map(SimilarProductDto::of)
//                .toList();
//        productDto.setSimilarProducts(similarProductDtoList);

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

    // TODO: isTranslationsPresent. Make dynamic translation
    private boolean isTranslationsPresent(TranslationDto translationDto) {
        if (translationDto == null) {
            return false;
        }

        String en = StringUtils.trimToNull(translationDto.en());
        String ru = StringUtils.trimToNull(translationDto.ru());
        String zh = StringUtils.trimToNull(translationDto.zh());

        return en != null || ru != null || zh != null;
    }

    private boolean isTranslationsPresent(HstoreTranslationDto translationDto) {
        if (translationDto == null) {
            return false;
        }

        String en = StringUtils.trimToNull(translationDto.textEn());
        String ru = StringUtils.trimToNull(translationDto.textRu());
        String zh = StringUtils.trimToNull(translationDto.textZh());

        return en != null || ru != null || zh != null;
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