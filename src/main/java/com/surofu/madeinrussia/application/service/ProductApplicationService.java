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
import com.surofu.madeinrussia.infrastructure.persistence.translation.TranslationResponse;
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
        product.setTitle(operation.getProductTitle());
        product.setDescription(operation.getProductDescription());

        try {
            TranslationResponse translationResponseEn = translationRepository.translateToEn(
                    operation.getProductTitle().toString(),
                    operation.getProductDescription().getMainDescription(),
                    operation.getProductDescription().getFurtherDescription()
            );
            TranslationResponse translationResponseRu = translationRepository.translateToRu(
                    operation.getProductTitle().toString(),
                    operation.getProductDescription().getMainDescription(),
                    operation.getProductDescription().getFurtherDescription()
            );
            TranslationResponse translationResponseZh = translationRepository.translateToZh(
                    operation.getProductTitle().toString(),
                    operation.getProductDescription().getMainDescription(),
                    operation.getProductDescription().getFurtherDescription()
            );
            HstoreTranslationDto titleTranslationDto = new HstoreTranslationDto(
                    translationResponseEn.getTranslations()[0].getText(),
                    translationResponseRu.getTranslations()[0].getText(),
                    translationResponseZh.getTranslations()[0].getText()
            );

            HstoreTranslationDto mainDescriptionTranslationDto = new HstoreTranslationDto(
                    translationResponseEn.getTranslations()[1].getText(),
                    translationResponseRu.getTranslations()[1].getText(),
                    translationResponseZh.getTranslations()[1].getText()
            );

            HstoreTranslationDto furtherDescriptionTranslationDto = new HstoreTranslationDto(
                    translationResponseEn.getTranslations()[2].getText(),
                    translationResponseRu.getTranslations()[2].getText(),
                    translationResponseZh.getTranslations()[2].getText()
            );

            product.getTitle().setTranslations(titleTranslationDto);
            product.getDescription().setMainDescriptionTranslations(mainDescriptionTranslationDto);
            product.getDescription().setFurtherDescriptionTranslations(furtherDescriptionTranslationDto);
        } catch (Exception e) {
            return CreateProduct.Result.translationError(e);
        }

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

        List<String> productCns = operation.getCreateProductCharacteristicCommands().stream()
                .map(CreateProductCharacteristicCommand::name).toList();
        List<String> productCvs = operation.getCreateProductCharacteristicCommands().stream()
                .map(CreateProductCharacteristicCommand::value).toList();

        List<String> productCharacteristicsStringsToTranslate = new ArrayList<>(
                productCns.size() + productCvs.size()
        );
        productCharacteristicsStringsToTranslate.addAll(productCns);
        productCharacteristicsStringsToTranslate.addAll(productCvs);

        try {
            TranslationResponse enTranslationResponse = translationRepository.translateToEn(productCharacteristicsStringsToTranslate.toArray(String[]::new));
            TranslationResponse ruTranslationResponse = translationRepository.translateToRu(productCharacteristicsStringsToTranslate.toArray(String[]::new));
            TranslationResponse zhTranslationResponse = translationRepository.translateToZh(productCharacteristicsStringsToTranslate.toArray(String[]::new));

            for (int i = 0; i < operation.getCreateProductCharacteristicCommands().size(); i++) {
                CreateProductCharacteristicCommand command = operation.getCreateProductCharacteristicCommands().get(i);
                ProductCharacteristic productCharacteristic = new ProductCharacteristic();
                productCharacteristic.setProduct(product);
                productCharacteristic.setName(ProductCharacteristicName.of(command.name()));
                productCharacteristic.setValue(ProductCharacteristicValue.of(command.value()));

                productCharacteristic.getName().setTranslations(new HstoreTranslationDto(
                        enTranslationResponse.getTranslations()[i].getText(),
                        ruTranslationResponse.getTranslations()[i].getText(),
                        zhTranslationResponse.getTranslations()[i].getText()
                ));

                productCharacteristic.getValue().setTranslations(new HstoreTranslationDto(
                        enTranslationResponse.getTranslations()[productCns.size() + i].getText(),
                        ruTranslationResponse.getTranslations()[productCns.size() + i].getText(),
                        zhTranslationResponse.getTranslations()[productCns.size() + i].getText()
                ));

                productCharacteristicSet.add(productCharacteristic);
            }
        } catch (Exception e) {
            return CreateProduct.Result.translationError(e);
        }

        product.setCharacteristics(productCharacteristicSet);

        /* ========== Product Faq ========== */

        Set<ProductFaq> productFaqSet = new HashSet<>();

        List<String> productQuestions = operation.getCreateProductFaqCommands().stream()
                .map(CreateProductFaqCommand::question).toList();
        List<String> productAnswers = operation.getCreateProductFaqCommands().stream()
                .map(CreateProductFaqCommand::answer).toList();

        List<String> productFaqStrings = new ArrayList<>(productQuestions.size() + productAnswers.size());
        productFaqStrings.addAll(productQuestions);
        productFaqStrings.addAll(productAnswers);

        try {
            TranslationResponse enTranslationResponse = translationRepository.translateToEn(productFaqStrings.toArray(String[]::new));
            TranslationResponse ruTranslationResponse = translationRepository.translateToRu(productFaqStrings.toArray(String[]::new));
            TranslationResponse zhTranslationResponse = translationRepository.translateToZh(productFaqStrings.toArray(String[]::new));

            for (int i = 0; i < operation.getCreateProductFaqCommands().size(); i++) {
                CreateProductFaqCommand command = operation.getCreateProductFaqCommands().get(i);
                ProductFaq productFaq = new ProductFaq();
                productFaq.setProduct(product);
                productFaq.setQuestion(ProductFaqQuestion.of(command.question()));
                productFaq.setAnswer(ProductFaqAnswer.of(command.answer()));

                productFaq.getQuestion().setTranslations(new HstoreTranslationDto(
                        enTranslationResponse.getTranslations()[i].getText(),
                        ruTranslationResponse.getTranslations()[i].getText(),
                        zhTranslationResponse.getTranslations()[i].getText()
                ));

                productFaq.getAnswer().setTranslations(new HstoreTranslationDto(
                        enTranslationResponse.getTranslations()[productQuestions.size() + i].getText(),
                        ruTranslationResponse.getTranslations()[productQuestions.size() + i].getText(),
                        zhTranslationResponse.getTranslations()[productQuestions.size() + i].getText()
                ));

                productFaqSet.add(productFaq);
            }
        } catch (Exception e) {
            return CreateProduct.Result.translationError(e);
        }

        product.setFaq(productFaqSet);

        /* ========== Product Delivery Method Details ========== */

        Set<ProductDeliveryMethodDetails> productDeliveryMethodDetailsSet = new HashSet<>();

        List<String> productDmdNames = operation.getCreateProductDeliveryMethodDetailsCommands().stream()
                .map(CreateProductDeliveryMethodDetailsCommand::name).toList();
        List<String> productDmdValues = operation.getCreateProductDeliveryMethodDetailsCommands().stream()
                .map(CreateProductDeliveryMethodDetailsCommand::value).toList();

        List<String> productDmdStrings = new ArrayList<>(operation.getCreateProductDeliveryMethodDetailsCommands().size() * 2);
        productDmdStrings.addAll(productDmdNames);
        productDmdStrings.addAll(productDmdValues);

        try {
            TranslationResponse enTranslationResponse = translationRepository.translateToEn(productDmdStrings.toArray(String[]::new));
            TranslationResponse ruTranslationResponse = translationRepository.translateToRu(productDmdStrings.toArray(String[]::new));
            TranslationResponse zhTranslationResponse = translationRepository.translateToZh(productDmdStrings.toArray(String[]::new));

            for (int i = 0; i < operation.getCreateProductDeliveryMethodDetailsCommands().size(); i++) {
                CreateProductDeliveryMethodDetailsCommand command = operation.getCreateProductDeliveryMethodDetailsCommands().get(i);
                ProductDeliveryMethodDetails productDeliveryMethodDetails = new ProductDeliveryMethodDetails();
                productDeliveryMethodDetails.setProduct(product);
                productDeliveryMethodDetails.setName(ProductDeliveryMethodDetailsName.of(command.name()));
                productDeliveryMethodDetails.setValue(ProductDeliveryMethodDetailsValue.of(command.value()));

                productDeliveryMethodDetails.getName().setTranslations(new HstoreTranslationDto(
                        enTranslationResponse.getTranslations()[i].getText(),
                        ruTranslationResponse.getTranslations()[i].getText(),
                        zhTranslationResponse.getTranslations()[i].getText()
                ));

                productDeliveryMethodDetails.getValue().setTranslations(new HstoreTranslationDto(
                        enTranslationResponse.getTranslations()[operation.getCreateProductDeliveryMethodDetailsCommands().size() + i].getText(),
                        ruTranslationResponse.getTranslations()[operation.getCreateProductDeliveryMethodDetailsCommands().size() + i].getText(),
                        zhTranslationResponse.getTranslations()[operation.getCreateProductDeliveryMethodDetailsCommands().size() + i].getText()
                ));

                productDeliveryMethodDetailsSet.add(productDeliveryMethodDetails);
            }
        } catch (Exception e) {
            return CreateProduct.Result.translationError(e);
        }

        product.setDeliveryMethodDetails(productDeliveryMethodDetailsSet);

        /* ========== Product Package Options ========== */

        Set<ProductPackageOption> productPackageOptionSet = new HashSet<>();

        List<String> productPoNames = operation.getCreateProductPackageOptionCommands().stream()
                .map(CreateProductPackageOptionCommand::name).toList();

        try {
            TranslationResponse enTranslationResponse = translationRepository.translateToEn(productPoNames.toArray(String[]::new));
            TranslationResponse ruTranslationResponse = translationRepository.translateToRu(productPoNames.toArray(String[]::new));
            TranslationResponse zhTranslationResponse = translationRepository.translateToZh(productPoNames.toArray(String[]::new));

            for (int i = 0; i < operation.getCreateProductPackageOptionCommands().size(); i++) {
                CreateProductPackageOptionCommand command = operation.getCreateProductPackageOptionCommands().get(i);
                ProductPackageOption productPackageOption = new ProductPackageOption();
                productPackageOption.setProduct(product);
                productPackageOption.setName(ProductPackageOptionName.of(command.name()));
                productPackageOption.setPrice(ProductPackageOptionPrice.of(command.price()));
                productPackageOption.setPriceUnit(ProductPackageOptionPriceUnit.of(command.priceUnit()));

                productPackageOption.getName().setTranslations(new HstoreTranslationDto(
                        enTranslationResponse.getTranslations()[i].getText(),
                        ruTranslationResponse.getTranslations()[i].getText(),
                        zhTranslationResponse.getTranslations()[i].getText()
                ));

                productPackageOptionSet.add(productPackageOption);
            }
        } catch (Exception e) {
            return CreateProduct.Result.translationError(e);
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

            try {
                TranslationResponse enTranslationResponse = translationRepository.translateToEn(
                        operation.getCreateProductVendorDetailsCommand().mainDescription(),
                        operation.getCreateProductVendorDetailsCommand().furtherDescription()
                );
                TranslationResponse ruTranslationResponse = translationRepository.translateToRu(
                        operation.getCreateProductVendorDetailsCommand().mainDescription(),
                        operation.getCreateProductVendorDetailsCommand().furtherDescription()
                );
                TranslationResponse zhTranslationResponse = translationRepository.translateToZh(
                        operation.getCreateProductVendorDetailsCommand().mainDescription(),
                        operation.getCreateProductVendorDetailsCommand().furtherDescription()
                );

                productVendorDetails.getDescription().setMainDescriptionTranslations(new HstoreTranslationDto(
                        enTranslationResponse.getTranslations()[0].getText(),
                        ruTranslationResponse.getTranslations()[0].getText(),
                        zhTranslationResponse.getTranslations()[0].getText()
                ));

                productVendorDetails.getDescription().setFurtherDescriptionTranslations(new HstoreTranslationDto(
                        enTranslationResponse.getTranslations()[1].getText(),
                        ruTranslationResponse.getTranslations()[1].getText(),
                        zhTranslationResponse.getTranslations()[1].getText()
                ));
            } catch (Exception e) {
                return CreateProduct.Result.translationError(e);
            }

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

    // TODO: Refactor Update Product
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

        // Title

        String titleTranslationEn = product.getTitle().getTranslations().textEn();
        String titleTranslationRu = product.getTitle().getTranslations().textRu();
        String titleTranslationZh = product.getTitle().getTranslations().textZh();

        if (
                (titleTranslationEn == null || titleTranslationEn.isEmpty()) &&
                        (titleTranslationRu == null || titleTranslationRu.isEmpty()) &&
                        (titleTranslationZh == null || titleTranslationZh.isEmpty())
        ) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.emptyTranslations("Title");
        }

        try {
            if (titleTranslationEn == null || titleTranslationEn.isEmpty()) {
                if (titleTranslationRu != null && !titleTranslationRu.isEmpty()) {
                    TranslationResponse translationResponse = translationRepository.translateToEn(titleTranslationRu);
                    titleTranslationEn = translationResponse.getTranslations()[0].getText();
                } else {
                    TranslationResponse translationResponse = translationRepository.translateToEn(titleTranslationZh);
                    titleTranslationEn = translationResponse.getTranslations()[0].getText();
                }
            }

            if (titleTranslationRu == null || titleTranslationRu.isEmpty()) {
                if (titleTranslationEn != null && !titleTranslationEn.isEmpty()) {
                    TranslationResponse translationResponse = translationRepository.translateToRu(titleTranslationEn);
                    titleTranslationRu = translationResponse.getTranslations()[0].getText();
                } else {
                    TranslationResponse translationResponse = translationRepository.translateToRu(titleTranslationZh);
                    titleTranslationRu = translationResponse.getTranslations()[0].getText();
                }
            }

            if (titleTranslationZh == null || titleTranslationZh.isEmpty()) {
                if (titleTranslationRu != null && !titleTranslationRu.isEmpty()) {
                    TranslationResponse translationResponse = translationRepository.translateToZh(titleTranslationRu);
                    titleTranslationZh = translationResponse.getTranslations()[0].getText();
                } else {
                    TranslationResponse translationResponse = translationRepository.translateToZh(titleTranslationEn);
                    titleTranslationZh = translationResponse.getTranslations()[0].getText();
                }
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.translationError(e);
        }

        product.setTitle(operation.getProductTitle());
        product.getTitle().setTranslations(new HstoreTranslationDto(
                titleTranslationEn,
                titleTranslationRu,
                titleTranslationZh
        ));

        product.setMinimumOrderQuantity(ProductMinimumOrderQuantity.of(operation.getMinimumOrderQuantity()));
        product.setDiscountExpirationDate(ProductDiscountExpirationDate.of(operation.getDiscountExpirationDate()));

        Set<PreloadContentInfo<?>> preloadContentSet = new HashSet<>();

        /* ========== Product Description ========== */

        product.setDescription(ProductDescription.of(
                operation.getProductDescription().getMainDescription(),
                operation.getProductDescription().getFurtherDescription()
        ));

        String productMdEn = operation.getProductDescription().getMainDescriptionTranslations().textEn();
        String productMdRu = operation.getProductDescription().getMainDescriptionTranslations().textRu();
        String productMdZh = operation.getProductDescription().getMainDescriptionTranslations().textZh();

        if (
                (productMdEn == null || productMdEn.isEmpty()) &&
                        (productMdRu == null || productMdRu.isEmpty()) &&
                        (productMdZh == null || productMdZh.isEmpty())
        ) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.emptyTranslations("Main Description");
        }

        /* ========== Product Main Description ========== */

        try {
            if (productMdEn == null || productMdEn.isEmpty()) {
                if (productMdRu != null && !productMdRu.isEmpty()) {
                    TranslationResponse translationResponse = translationRepository.translateToEn(productMdRu);
                    productMdEn = translationResponse.getTranslations()[0].getText();
                } else {
                    TranslationResponse translationResponse = translationRepository.translateToEn(productMdZh);
                    productMdEn = translationResponse.getTranslations()[0].getText();
                }
            }

            if (productMdRu == null || productMdRu.isEmpty()) {
                if (productMdEn != null && !productMdEn.isEmpty()) {
                    TranslationResponse translationResponse = translationRepository.translateToRu(productMdEn);
                    productMdRu = translationResponse.getTranslations()[0].getText();
                } else {
                    TranslationResponse translationResponse = translationRepository.translateToRu(productMdZh);
                    productMdRu = translationResponse.getTranslations()[0].getText();
                }
            }

            if (productMdZh == null || productMdZh.isEmpty()) {
                if (productMdRu != null && !productMdRu.isEmpty()) {
                    TranslationResponse translationResponse = translationRepository.translateToZh(productMdRu);
                    productMdZh = translationResponse.getTranslations()[0].getText();
                } else {
                    TranslationResponse translationResponse = translationRepository.translateToZh(productMdEn);
                    productMdZh = translationResponse.getTranslations()[0].getText();
                }
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.translationError(e);
        }

        product.getDescription().setMainDescriptionTranslations(new HstoreTranslationDto(
                productMdEn,
                productMdRu,
                productMdZh
        ));

        /* ========== Product Further Description ========== */

        String productFdEn = operation.getProductDescription().getFurtherDescriptionTranslations().textEn();
        String productFdRu = operation.getProductDescription().getFurtherDescriptionTranslations().textRu();
        String productFdZh = operation.getProductDescription().getFurtherDescriptionTranslations().textZh();

        if ((productFdEn == null || productFdEn.isEmpty()) &&
                (productFdRu == null || productFdRu.isEmpty()) &&
                (productFdZh == null || productFdZh.isEmpty())) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.emptyTranslations("Product Further Description");
        }

        try {
            if (productFdEn == null || productFdEn.isEmpty()) {
                if (productFdRu != null && !productFdRu.isEmpty()) {
                    TranslationResponse translationResponse = translationRepository.translateToEn(productFdRu);
                    productFdEn = translationResponse.getTranslations()[0].getText();
                } else {
                    TranslationResponse translationResponse = translationRepository.translateToEn(productFdZh);
                    productFdEn = translationResponse.getTranslations()[0].getText();
                }
            }

            if (productFdRu == null || productFdRu.isEmpty()) {
                if (productFdEn != null && !productFdEn.isEmpty()) {
                    TranslationResponse translationResponse = translationRepository.translateToRu(productFdEn);
                    productFdRu = translationResponse.getTranslations()[0].getText();
                } else {
                    TranslationResponse translationResponse = translationRepository.translateToRu(productFdZh);
                    productFdRu = translationResponse.getTranslations()[0].getText();
                }
            }

            if (productFdZh == null || productFdZh.isEmpty()) {
                if (productFdRu != null && !productFdRu.isEmpty()) {
                    TranslationResponse translationResponse = translationRepository.translateToZh(productFdRu);
                    productFdZh = translationResponse.getTranslations()[0].getText();
                } else {
                    TranslationResponse translationResponse = translationRepository.translateToZh(productFdEn);
                    productFdZh = translationResponse.getTranslations()[0].getText();
                }
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.translationError(e);
        }

        product.getDescription().setFurtherDescriptionTranslations(new HstoreTranslationDto(
                productFdEn,
                productFdRu,
                productFdZh
        ));

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

        try {
            for (UpdateProductCharacteristicCommand command : operation.getUpdateProductCharacteristicCommands()) {
                ProductCharacteristic productCharacteristic = new ProductCharacteristic();
                productCharacteristic.setProduct(product);
                productCharacteristic.setName(ProductCharacteristicName.of(command.name()));
                productCharacteristic.setValue(ProductCharacteristicValue.of(command.value()));

                // Name
                String nameEn = command.nameTranslations().en(),
                        nameRu = command.nameTranslations().ru(),
                        nameZh = command.nameTranslations().zh();

                if (command.nameTranslations() == null || command.valueTranslation() == null) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return UpdateProduct.Result.emptyTranslations("Characteristics name or value");
                }

                if (
                        (command.nameTranslations().en() == null || command.nameTranslations().en().isEmpty()) &&
                                (command.nameTranslations().ru() == null || command.nameTranslations().ru().isEmpty()) &&
                                (command.nameTranslations().zh() == null || command.nameTranslations().zh().isEmpty())
                ) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return UpdateProduct.Result.emptyTranslations("Characteristics name");
                }

                if (
                        (command.valueTranslation().en() == null || command.valueTranslation().en().isEmpty()) &&
                                (command.valueTranslation().ru() == null || command.valueTranslation().ru().isEmpty()) &&
                                (command.valueTranslation().zh() == null || command.valueTranslation().zh().isEmpty())
                ) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return UpdateProduct.Result.emptyTranslations("Characteristics value");
                }

                if (command.nameTranslations().ru() == null || command.nameTranslations().ru().isEmpty()) {
                    if (command.nameTranslations().en() != null && !command.nameTranslations().en().isEmpty()) {
                        TranslationResponse translationResponse = translationRepository.translateToRu(command.nameTranslations().en());
                        nameRu = translationResponse.getTranslations()[0].getText();
                    } else {
                        TranslationResponse translationResponse = translationRepository.translateToRu(command.nameTranslations().zh());
                        nameRu = translationResponse.getTranslations()[0].getText();
                    }
                }

                if (command.nameTranslations().en() == null || command.nameTranslations().en().isEmpty()) {
                    if (command.nameTranslations().ru() != null && !command.nameTranslations().ru().isEmpty()) {
                        TranslationResponse translationResponse = translationRepository.translateToEn(command.nameTranslations().ru());
                        nameEn = translationResponse.getTranslations()[0].getText();
                    } else {
                        TranslationResponse translationResponse = translationRepository.translateToEn(command.nameTranslations().zh());
                        nameEn = translationResponse.getTranslations()[0].getText();
                    }
                }

                if (command.nameTranslations().zh() == null || command.nameTranslations().zh().isEmpty()) {
                    if (command.nameTranslations().ru() != null && !command.nameTranslations().ru().isEmpty()) {
                        TranslationResponse translationResponse = translationRepository.translateToZh(command.nameTranslations().ru());
                        nameZh = translationResponse.getTranslations()[0].getText();
                    } else {
                        TranslationResponse translationResponse = translationRepository.translateToZh(command.nameTranslations().en());
                        nameZh = translationResponse.getTranslations()[0].getText();
                    }
                }

                productCharacteristic.getName().setTranslations(new HstoreTranslationDto(nameEn, nameRu, nameZh));

                // Value

                String valueEn = command.valueTranslation().en(),
                        valueRu = command.valueTranslation().ru(),
                        valueZh = command.valueTranslation().zh();

                if (command.valueTranslation().ru() == null || command.valueTranslation().ru().isEmpty()) {
                    if (command.valueTranslation().en() != null && !command.valueTranslation().en().isEmpty()) {
                        TranslationResponse translationResponse = translationRepository.translateToRu(command.valueTranslation().en());
                        valueRu = translationResponse.getTranslations()[0].getText();
                    } else {
                        TranslationResponse translationResponse = translationRepository.translateToRu(command.valueTranslation().zh());
                        valueRu = translationResponse.getTranslations()[0].getText();
                    }
                }

                if (command.valueTranslation().en() == null || command.valueTranslation().en().isEmpty()) {
                    if (command.valueTranslation().ru() != null && !command.valueTranslation().ru().isEmpty()) {
                        TranslationResponse translationResponse = translationRepository.translateToEn(command.valueTranslation().ru());
                        valueEn = translationResponse.getTranslations()[0].getText();
                    } else {
                        TranslationResponse translationResponse = translationRepository.translateToEn(command.valueTranslation().zh());
                        valueEn = translationResponse.getTranslations()[0].getText();
                    }
                }

                if (command.valueTranslation().zh() == null || command.valueTranslation().zh().isEmpty()) {
                    if (command.valueTranslation().ru() != null && !command.valueTranslation().ru().isEmpty()) {
                        TranslationResponse translationResponse = translationRepository.translateToZh(command.valueTranslation().ru());
                        valueZh = translationResponse.getTranslations()[0].getText();
                    } else {
                        TranslationResponse translationResponse = translationRepository.translateToZh(command.valueTranslation().en());
                        valueZh = translationResponse.getTranslations()[0].getText();
                    }
                }

                productCharacteristic.getValue().setTranslations(new HstoreTranslationDto(valueEn, valueRu, valueZh));

                productCharacteristicSet.add(productCharacteristic);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.translationError(e);
        }

        product.getCharacteristics().clear();
        product.getCharacteristics().addAll(productCharacteristicSet);

        /* ========== Product Faq ========== */

        Set<ProductFaq> productFaqSet = new HashSet<>();

        try {
            for (UpdateProductFaqCommand command : operation.getUpdateProductFaqCommands()) {
                ProductFaq productFaq = new ProductFaq();
                productFaq.setProduct(product);
                productFaq.setQuestion(ProductFaqQuestion.of(command.question()));
                productFaq.setAnswer(ProductFaqAnswer.of(command.answer()));

                // Question
                String questionRu = command.questionTranslations().ru(),
                        questionEn = command.questionTranslations().en(),
                        questionZh = command.questionTranslations().zh();

                if (command.questionTranslations() == null || command.answerTranslation() == null) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return UpdateProduct.Result.emptyTranslations("Faq question or answer");
                }

                if (
                        (command.questionTranslations().en() == null || command.questionTranslations().en().isEmpty()) &&
                                (command.questionTranslations().ru() == null || command.questionTranslations().ru().isEmpty()) &&
                                (command.questionTranslations().zh() == null || command.questionTranslations().zh().isEmpty())
                ) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return UpdateProduct.Result.emptyTranslations("Faq question");
                }

                if (
                        (command.answerTranslation().en() == null || command.answerTranslation().en().isEmpty()) &&
                                (command.answerTranslation().ru() == null || command.answerTranslation().ru().isEmpty()) &&
                                (command.answerTranslation().zh() == null || command.answerTranslation().zh().isEmpty())
                ) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return UpdateProduct.Result.emptyTranslations("Faq answer");
                }

                if (command.questionTranslations().ru() == null || command.questionTranslations().ru().isEmpty()) {
                    if (command.questionTranslations().en() != null && !command.questionTranslations().en().isEmpty()) {
                        TranslationResponse translationResponse = translationRepository.translateToRu(command.questionTranslations().en());
                        questionRu = translationResponse.getTranslations()[0].getText();
                    } else {
                        TranslationResponse translationResponse = translationRepository.translateToRu(command.questionTranslations().zh());
                        questionRu = translationResponse.getTranslations()[0].getText();
                    }
                }

                if (command.questionTranslations().en() == null || command.questionTranslations().en().isEmpty()) {
                    if (command.questionTranslations().ru() != null && !command.questionTranslations().ru().isEmpty()) {
                        TranslationResponse translationResponse = translationRepository.translateToEn(command.questionTranslations().ru());
                        questionEn = translationResponse.getTranslations()[0].getText();
                    } else {
                        TranslationResponse translationResponse = translationRepository.translateToEn(command.questionTranslations().zh());
                        questionEn = translationResponse.getTranslations()[0].getText();
                    }
                }

                if (command.questionTranslations().zh() == null || command.questionTranslations().zh().isEmpty()) {
                    if (command.questionTranslations().ru() != null && !command.questionTranslations().ru().isEmpty()) {
                        TranslationResponse translationResponse = translationRepository.translateToZh(command.questionTranslations().ru());
                        questionZh = translationResponse.getTranslations()[0].getText();
                    } else {
                        TranslationResponse translationResponse = translationRepository.translateToZh(command.questionTranslations().en());
                        questionZh = translationResponse.getTranslations()[0].getText();
                    }
                }

                productFaq.getQuestion().setTranslations(new HstoreTranslationDto(questionEn, questionRu, questionZh));

                // Answer

                String answerRu = command.answerTranslation().ru(),
                        answerEn = command.answerTranslation().en(),
                        answerZh = command.answerTranslation().zh();

                if (command.answerTranslation().ru() == null || command.answerTranslation().ru().isEmpty()) {
                    if (command.answerTranslation().en() != null && !command.answerTranslation().en().isEmpty()) {
                        TranslationResponse translationResponse = translationRepository.translateToRu(command.answerTranslation().en());
                        answerRu = translationResponse.getTranslations()[0].getText();
                    } else {
                        TranslationResponse translationResponse = translationRepository.translateToRu(command.answerTranslation().zh());
                        answerRu = translationResponse.getTranslations()[0].getText();
                    }
                }

                if (command.answerTranslation().en() == null || command.answerTranslation().en().isEmpty()) {
                    if (command.answerTranslation().ru() != null && !command.answerTranslation().ru().isEmpty()) {
                        TranslationResponse translationResponse = translationRepository.translateToEn(command.answerTranslation().ru());
                        answerEn = translationResponse.getTranslations()[0].getText();
                    } else {
                        TranslationResponse translationResponse = translationRepository.translateToEn(command.answerTranslation().zh());
                        answerEn = translationResponse.getTranslations()[0].getText();
                    }
                }

                if (command.answerTranslation().zh() == null || command.answerTranslation().zh().isEmpty()) {
                    if (command.answerTranslation().ru() != null && !command.answerTranslation().ru().isEmpty()) {
                        TranslationResponse translationResponse = translationRepository.translateToZh(command.answerTranslation().ru());
                        answerZh = translationResponse.getTranslations()[0].getText();
                    } else {
                        TranslationResponse translationResponse = translationRepository.translateToZh(command.answerTranslation().en());
                        answerZh = translationResponse.getTranslations()[0].getText();
                    }
                }

                productFaq.getAnswer().setTranslations(new HstoreTranslationDto(answerEn, answerRu, answerZh));

                productFaqSet.add(productFaq);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.translationError(e);
        }

        product.getFaq().clear();
        product.getFaq().addAll(productFaqSet);

        /* ========== Product Delivery Method Details ========== */

        Set<ProductDeliveryMethodDetails> productDeliveryMethodDetailsSet = new HashSet<>();

        try {
            for (UpdateProductDeliveryMethodDetailsCommand command : operation.getUpdateProductDeliveryMethodDetailsCommands()) {
                ProductDeliveryMethodDetails productDeliveryMethodDetails = new ProductDeliveryMethodDetails();
                productDeliveryMethodDetails.setProduct(product);
                productDeliveryMethodDetails.setName(ProductDeliveryMethodDetailsName.of(command.name()));
                productDeliveryMethodDetails.setValue(ProductDeliveryMethodDetailsValue.of(command.value()));

                // Name
                String nameRu = command.nameTranslations().ru(),
                        nameEn = command.nameTranslations().en(),
                        nameZh = command.nameTranslations().zh();

                if (command.nameTranslations() == null || command.valueTranslation() == null) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return UpdateProduct.Result.emptyTranslations("Delivery Method Details name or value");
                }

                if (
                        (command.nameTranslations().ru() == null || command.nameTranslations().ru().isEmpty()) &&
                                (command.nameTranslations().en() == null || command.nameTranslations().en().isEmpty()) &&
                                (command.nameTranslations().zh() == null || command.nameTranslations().zh().isEmpty())
                ) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return UpdateProduct.Result.emptyTranslations("Delivery Method Details name");
                }

                if (
                        (command.valueTranslation().ru() == null || command.valueTranslation().ru().isEmpty()) &&
                                (command.valueTranslation().en() == null || command.valueTranslation().en().isEmpty()) &&
                                (command.valueTranslation().zh() == null || command.valueTranslation().zh().isEmpty())
                ) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return UpdateProduct.Result.emptyTranslations("Delivery Method Details value");
                }

                if (command.nameTranslations().ru() == null || command.nameTranslations().ru().isEmpty()) {
                    if (command.nameTranslations().en() != null && !command.nameTranslations().en().isEmpty()) {
                        TranslationResponse translationResponse = translationRepository.translateToRu(command.nameTranslations().en());
                        nameRu = translationResponse.getTranslations()[0].getText();
                    } else {
                        TranslationResponse translationResponse = translationRepository.translateToRu(command.nameTranslations().zh());
                        nameRu = translationResponse.getTranslations()[0].getText();
                    }
                }

                if (command.nameTranslations().en() == null || command.nameTranslations().en().isEmpty()) {
                    if (command.nameTranslations().ru() != null && !command.nameTranslations().ru().isEmpty()) {
                        TranslationResponse translationResponse = translationRepository.translateToEn(command.nameTranslations().ru());
                        nameEn = translationResponse.getTranslations()[0].getText();
                    } else {
                        TranslationResponse translationResponse = translationRepository.translateToEn(command.nameTranslations().zh());
                        nameEn = translationResponse.getTranslations()[0].getText();
                    }
                }

                if (command.nameTranslations().zh() == null || command.nameTranslations().zh().isEmpty()) {
                    if (command.nameTranslations().ru() != null && !command.nameTranslations().ru().isEmpty()) {
                        TranslationResponse translationResponse = translationRepository.translateToZh(command.nameTranslations().ru());
                        nameZh = translationResponse.getTranslations()[0].getText();
                    } else {
                        TranslationResponse translationResponse = translationRepository.translateToZh(command.nameTranslations().en());
                        nameZh = translationResponse.getTranslations()[0].getText();
                    }
                }

                productDeliveryMethodDetails.getName().setTranslations(new HstoreTranslationDto(nameEn, nameRu, nameZh));

                // Value
                String valueRu = command.valueTranslation().ru(),
                        valueEn = command.valueTranslation().en(),
                        valueZh = command.valueTranslation().zh();

                if (command.valueTranslation().ru() == null || command.valueTranslation().ru().isEmpty()) {
                    if (command.valueTranslation().en() != null && !command.valueTranslation().en().isEmpty()) {
                        TranslationResponse translationResponse = translationRepository.translateToRu(command.valueTranslation().en());
                        valueRu = translationResponse.getTranslations()[0].getText();
                    } else {
                        TranslationResponse translationResponse = translationRepository.translateToRu(command.valueTranslation().zh());
                        valueRu = translationResponse.getTranslations()[0].getText();
                    }
                }

                if (command.valueTranslation().en() == null || command.valueTranslation().en().isEmpty()) {
                    if (command.valueTranslation().ru() != null && !command.valueTranslation().ru().isEmpty()) {
                        TranslationResponse translationResponse = translationRepository.translateToEn(command.valueTranslation().ru());
                        valueEn = translationResponse.getTranslations()[0].getText();
                    } else {
                        TranslationResponse translationResponse = translationRepository.translateToEn(command.valueTranslation().zh());
                        valueEn = translationResponse.getTranslations()[0].getText();
                    }
                }

                if (command.valueTranslation().zh() == null || command.valueTranslation().zh().isEmpty()) {
                    if (command.valueTranslation().ru() != null && !command.valueTranslation().ru().isEmpty()) {
                        TranslationResponse translationResponse = translationRepository.translateToZh(command.valueTranslation().ru());
                        valueZh = translationResponse.getTranslations()[0].getText();
                    } else {
                        TranslationResponse translationResponse = translationRepository.translateToZh(command.valueTranslation().en());
                        valueZh = translationResponse.getTranslations()[0].getText();
                    }
                }

                productDeliveryMethodDetails.getValue().setTranslations(new HstoreTranslationDto(valueEn, valueRu, valueZh));

                productDeliveryMethodDetailsSet.add(productDeliveryMethodDetails);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.translationError(e);
        }

        product.getDeliveryMethodDetails().clear();
        product.getDeliveryMethodDetails().addAll(productDeliveryMethodDetailsSet);

        /* ========== Product Package Options ========== */

        Set<ProductPackageOption> productPackageOptionSet = new HashSet<>();

        try {
            for (UpdateProductPackageOptionCommand command : operation.getUpdateProductPackageOptionCommands()) {
                ProductPackageOption productPackageOption = new ProductPackageOption();
                productPackageOption.setProduct(product);
                productPackageOption.setName(ProductPackageOptionName.of(command.name()));
                productPackageOption.setPrice(ProductPackageOptionPrice.of(command.price()));
                productPackageOption.setPriceUnit(ProductPackageOptionPriceUnit.of(command.priceUnit()));

                String nameRu = command.nameTranslations().ru(),
                        nameEn = command.nameTranslations().en(),
                        nameZh = command.nameTranslations().zh();

                if (command.nameTranslations() == null) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return UpdateProduct.Result.emptyTranslations("Package Option");
                }

                if (
                        (command.nameTranslations().en() == null || command.nameTranslations().en().isEmpty()) &&
                                (command.nameTranslations().ru() == null || command.nameTranslations().ru().isEmpty()) &&
                                (command.nameTranslations().zh() == null || command.nameTranslations().zh().isEmpty())
                ) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return UpdateProduct.Result.emptyTranslations("Package Option");
                }

                if (command.nameTranslations().ru() == null || command.nameTranslations().ru().isEmpty()) {
                    if (command.nameTranslations().en() != null && !command.nameTranslations().en().isEmpty()) {
                        TranslationResponse translationResponse = translationRepository.translateToRu(command.nameTranslations().en());
                        nameRu = translationResponse.getTranslations()[0].getText();
                    } else {
                        TranslationResponse translationResponse = translationRepository.translateToRu(command.nameTranslations().zh());
                        nameRu = translationResponse.getTranslations()[0].getText();
                    }
                }

                if (command.nameTranslations().en() == null || command.nameTranslations().en().isEmpty()) {
                    if (command.nameTranslations().ru() != null && !command.nameTranslations().ru().isEmpty()) {
                        TranslationResponse translationResponse = translationRepository.translateToEn(command.nameTranslations().ru());
                        nameEn = translationResponse.getTranslations()[0].getText();
                    } else {
                        TranslationResponse translationResponse = translationRepository.translateToEn(command.nameTranslations().zh());
                        nameEn = translationResponse.getTranslations()[0].getText();
                    }
                }

                if (command.nameTranslations().zh() == null || command.nameTranslations().zh().isEmpty()) {
                    if (command.nameTranslations().ru() != null && !command.nameTranslations().ru().isEmpty()) {
                        TranslationResponse translationResponse = translationRepository.translateToZh(command.nameTranslations().ru());
                        nameZh = translationResponse.getTranslations()[0].getText();
                    } else {
                        TranslationResponse translationResponse = translationRepository.translateToZh(command.nameTranslations().en());
                        nameZh = translationResponse.getTranslations()[0].getText();
                    }
                }

                productPackageOption.getName().setTranslations(new HstoreTranslationDto(nameEn, nameRu, nameZh));

                productPackageOptionSet.add(productPackageOption);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.translationError(e);
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

        /* ========== Product Vendor Details ========== */

        product.getProductVendorDetails().setDescription(ProductVendorDetailsDescription.of(
                operation.getUpdateProductVendorDetailsCommand().mainDescription(),
                operation.getUpdateProductVendorDetailsCommand().furtherDescription()
        ));

        // Main Description
        TranslationDto productVdMd = operation.getUpdateProductVendorDetailsCommand().mainDescriptionTranslations();

        if (productVdMd == null) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.emptyTranslations("Vendor Details Main Description");
        }

        if (
                (productVdMd.en() == null || productVdMd.en().isEmpty()) &&
                        (productVdMd.ru() == null || productVdMd.ru().isEmpty()) &&
                        (productVdMd.zh() == null || productVdMd.zh().isEmpty())
        ) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.emptyTranslations("Vendor Details Main Description");
        }

        String productVdMdRu = productVdMd.ru();
        String productVdMdEn = productVdMd.en();
        String productVdMdZh = productVdMd.zh();

        try {
            if (productVdMd.ru() == null) {
                if (productVdMd.en() != null) {
                    TranslationResponse translationResponse = translationRepository.translateToRu(productVdMd.en());
                    productVdMdRu = translationResponse.getTranslations()[0].getText();
                } else {
                    TranslationResponse translationResponse = translationRepository.translateToRu(productVdMd.zh());
                    productVdMdRu = translationResponse.getTranslations()[0].getText();
                }
            }

            if (productVdMd.en() == null) {
                if (productVdMd.ru() != null) {
                    TranslationResponse translationResponse = translationRepository.translateToEn(productVdMd.ru());
                    productVdMdEn = translationResponse.getTranslations()[0].getText();
                } else {
                    TranslationResponse translationResponse = translationRepository.translateToEn(productVdMd.zh());
                    productVdMdEn = translationResponse.getTranslations()[0].getText();
                }
            }

            if (productVdMd.zh() == null) {
                if (productVdMd.ru() != null) {
                    TranslationResponse translationResponse = translationRepository.translateToZh(productVdMd.ru());
                    productVdMdZh = translationResponse.getTranslations()[0].getText();
                } else {
                    TranslationResponse translationResponse = translationRepository.translateToZh(productVdMd.en());
                    productVdMdZh = translationResponse.getTranslations()[0].getText();
                }
            }

            product.getProductVendorDetails().getDescription().setMainDescriptionTranslations(
                    new HstoreTranslationDto(productVdMdEn, productVdMdRu, productVdMdZh));
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.translationError(e);
        }

        // Further Description
        TranslationDto productVdFd = operation.getUpdateProductVendorDetailsCommand().furtherDescriptionTranslations();

        if (productVdFd == null) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.emptyTranslations("Vendor Details Further Description");
        }

        if (
                (productVdFd.en() == null || productVdFd.en().isEmpty()) &&
                        (productVdFd.ru() == null || productVdFd.ru().isEmpty()) &&
                        (productVdFd.zh() == null || productVdFd.zh().isEmpty())
        ) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.emptyTranslations("Vendor Details Further Description");
        }

        String productVdFdRu = productVdFd.ru();
        String productVdFdEn = productVdFd.en();
        String productVdFdZh = productVdFd.zh();

        try {
            if (productVdFd.ru() == null) {
                if (productVdFd.en() != null) {
                    TranslationResponse translationResponse = translationRepository.translateToRu(productVdFd.en());
                    productVdFdRu = translationResponse.getTranslations()[0].getText();
                } else {
                    TranslationResponse translationResponse = translationRepository.translateToRu(productVdFd.zh());
                    productVdFdRu = translationResponse.getTranslations()[0].getText();
                }
            }

            if (productVdFd.en() == null) {
                if (productVdFd.ru() != null) {
                    TranslationResponse translationResponse = translationRepository.translateToEn(productVdFd.ru());
                    productVdFdEn = translationResponse.getTranslations()[0].getText();
                } else {
                    TranslationResponse translationResponse = translationRepository.translateToEn(productVdFd.zh());
                    productVdFdEn = translationResponse.getTranslations()[0].getText();
                }
            }

            if (productVdFd.zh() == null) {
                if (productVdFd.ru() != null) {
                    TranslationResponse translationResponse = translationRepository.translateToZh(productVdFd.ru());
                    productVdFdZh = translationResponse.getTranslations()[0].getText();
                } else {
                    TranslationResponse translationResponse = translationRepository.translateToZh(productVdFd.en());
                    productVdFdZh = translationResponse.getTranslations()[0].getText();
                }
            }

            product.getProductVendorDetails().getDescription().setFurtherDescriptionTranslations(
                    new HstoreTranslationDto(productVdFdEn, productVdFdRu, productVdFdZh));
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProduct.Result.translationError(e);
        }

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
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
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

    private record PreloadContentInfo<T>(
            T entity,
            MultipartFile file,
            String folderName
    ) {
    }
}