package com.surofu.exporteru.application.service;

import com.surofu.exporteru.application.cache.CategoryCacheManager;
import com.surofu.exporteru.application.cache.GeneralCacheService;
import com.surofu.exporteru.application.cache.ProductCacheManager;
import com.surofu.exporteru.application.cache.ProductSummaryCacheManager;
import com.surofu.exporteru.application.dto.DeliveryMethodDto;
import com.surofu.exporteru.application.dto.SearchHintDto;
import com.surofu.exporteru.application.dto.category.CategoryDto;
import com.surofu.exporteru.application.dto.category.CategoryHintDto;
import com.surofu.exporteru.application.dto.deliveryTerm.DeliveryTermDto;
import com.surofu.exporteru.application.dto.product.ProductCharacteristicDto;
import com.surofu.exporteru.application.dto.product.ProductCharacteristicWithTranslationsDto;
import com.surofu.exporteru.application.dto.product.ProductDeliveryMethodDetailsDto;
import com.surofu.exporteru.application.dto.product.ProductDeliveryMethodDetailsWithTranslationsDto;
import com.surofu.exporteru.application.dto.product.ProductDto;
import com.surofu.exporteru.application.dto.product.ProductFaqDto;
import com.surofu.exporteru.application.dto.product.ProductFaqWithTranslationDto;
import com.surofu.exporteru.application.dto.product.ProductHintDto;
import com.surofu.exporteru.application.dto.product.ProductMediaDto;
import com.surofu.exporteru.application.dto.product.ProductMediaWithTranslationsDto;
import com.surofu.exporteru.application.dto.product.ProductPackageOptionDto;
import com.surofu.exporteru.application.dto.product.ProductPackageOptionWithTranslationsDto;
import com.surofu.exporteru.application.dto.product.ProductPriceDto;
import com.surofu.exporteru.application.dto.product.ProductReviewMediaDto;
import com.surofu.exporteru.application.dto.product.ProductWithTranslationsDto;
import com.surofu.exporteru.application.dto.product.SimilarProductDto;
import com.surofu.exporteru.application.dto.vendor.VendorCountryDto;
import com.surofu.exporteru.application.dto.vendor.VendorDto;
import com.surofu.exporteru.application.dto.vendor.VendorFaqDto;
import com.surofu.exporteru.application.dto.vendor.VendorProductCategoryDto;
import com.surofu.exporteru.application.service.product.ProductCreatingService;
import com.surofu.exporteru.application.service.product.ProductUpdatingService;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.model.category.Category;
import com.surofu.exporteru.core.model.category.CategorySlug;
import com.surofu.exporteru.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.exporteru.core.model.deliveryTerm.DeliveryTerm;
import com.surofu.exporteru.core.model.moderation.ApproveStatus;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.characteristic.ProductCharacteristic;
import com.surofu.exporteru.core.model.product.faq.ProductFaq;
import com.surofu.exporteru.core.model.product.media.ProductMedia;
import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.model.user.UserRole;
import com.surofu.exporteru.core.repository.CategoryRepository;
import com.surofu.exporteru.core.repository.DeliveryMethodRepository;
import com.surofu.exporteru.core.repository.DeliveryTermRepository;
import com.surofu.exporteru.core.repository.ProductCharacteristicRepository;
import com.surofu.exporteru.core.repository.ProductDeliveryMethodDetailsRepository;
import com.surofu.exporteru.core.repository.ProductFaqRepository;
import com.surofu.exporteru.core.repository.ProductMediaRepository;
import com.surofu.exporteru.core.repository.ProductPackageOptionsRepository;
import com.surofu.exporteru.core.repository.ProductPriceRepository;
import com.surofu.exporteru.core.repository.ProductRepository;
import com.surofu.exporteru.core.repository.ProductReviewMediaRepository;
import com.surofu.exporteru.core.repository.UserRepository;
import com.surofu.exporteru.core.repository.VendorCountryRepository;
import com.surofu.exporteru.core.repository.VendorFaqRepository;
import com.surofu.exporteru.core.repository.VendorProductCategoryRepository;
import com.surofu.exporteru.core.service.product.ProductService;
import com.surofu.exporteru.core.service.product.operation.CreateProduct;
import com.surofu.exporteru.core.service.product.operation.DeleteProductById;
import com.surofu.exporteru.core.service.product.operation.GetProductByArticle;
import com.surofu.exporteru.core.service.product.operation.GetProductById;
import com.surofu.exporteru.core.service.product.operation.GetProductCategoryByProductId;
import com.surofu.exporteru.core.service.product.operation.GetProductCharacteristicsByProductId;
import com.surofu.exporteru.core.service.product.operation.GetProductDeliveryMethodsByProductId;
import com.surofu.exporteru.core.service.product.operation.GetProductFaqByProductId;
import com.surofu.exporteru.core.service.product.operation.GetProductMediaByProductId;
import com.surofu.exporteru.core.service.product.operation.GetProductWithTranslationsById;
import com.surofu.exporteru.core.service.product.operation.GetSearchHints;
import com.surofu.exporteru.core.service.product.operation.UpdateProduct;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

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
  private final ProductCacheManager productCacheManager;
  private final LocalizationManager localizationManager;
  private final ProductCreatingService productCreatingService;
  private final ProductUpdatingService productUpdatingService;
  private final ProductSummaryCacheManager productSummaryCacheManager;
  private final GeneralCacheService generalCacheService;
  private final CategoryCacheManager categoryCacheManager;
  private final DeliveryTermRepository deliveryTermRepository;

  @Override
  @Transactional(readOnly = true)
  public GetProductById.Result getProductById(GetProductById operation) {
    // Check cache
    ProductDto cachedProduct = productCacheManager.getProduct(operation.getProductId(),
        operation.getLocale().getLanguage());

    if (cachedProduct != null) {
      return GetProductById.Result.success(cachedProduct);
    }

    // Process
    List<ApproveStatus> approveStatuses = new ArrayList<>();
    approveStatuses.add(ApproveStatus.APPROVED);

    Optional<Product> productWithUserOptional =
        productRepository.getProductWithUserById(operation.getProductId());

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

    Optional<ProductView> productView =
        productRepository.getProductViewByIdAndLangAndApproveStatuses(
            operation.getProductId(), operation.getLocale().getLanguage(), approveStatuses
        );

    if (productView.isEmpty()) {
      return GetProductById.Result.notFound(operation.getProductId());
    }

    ProductDto productDto = ProductDto.of(productView.get());
    ProductDto fullProductDto =
        loadFullProduct(productDto, productView.get(), operation.getLocale());
    return GetProductById.Result.success(fullProductDto);
  }

  @Override
  @Transactional(readOnly = true)
  public GetProductWithTranslationsById.Result getProductWithTranslationsByProductId(
      GetProductWithTranslationsById operation) {
    Optional<Product> productWithUserOptional =
        productRepository.getProductWithUserById(operation.getId());

    if (productWithUserOptional.isEmpty()) {
      return GetProductWithTranslationsById.Result.notFound(operation.getId());
    }

    Product productWithUser = productWithUserOptional.get();

    if (operation.getSecurityUser() != null) {
      Long currentUserId = operation.getSecurityUser().getUser().getId();
      Long ownerId = productWithUser.getUser().getId();

      if (!currentUserId.equals(ownerId) &&
          !operation.getSecurityUser().getUser().getRole().equals(UserRole.ROLE_ADMIN)) {
        return GetProductWithTranslationsById.Result.notFound(operation.getId());
      }
    }

    Optional<ProductWithTranslationsView> view =
        productRepository.getProductWithTranslationsByProductIdAndLang(
            operation.getId(), operation.getLocale().getLanguage());

    if (view.isEmpty()) {
      return GetProductWithTranslationsById.Result.notFound(operation.getId());
    }

    ProductWithTranslationsDto dto = ProductWithTranslationsDto.of(productWithUser);
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
    ProductDto fullProductDto =
        loadFullProduct(productDto, productView.get(), operation.getLocale());
    return GetProductByArticle.Result.success(fullProductDto);
  }

  @Override
  @Transactional(readOnly = true)
  public GetProductCategoryByProductId.Result getProductCategoryByProductId(
      GetProductCategoryByProductId operation) {
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
  public GetProductDeliveryMethodsByProductId.Result getProductDeliveryMethodsByProductId(
      GetProductDeliveryMethodsByProductId operation) {
    Long productId = operation.getProductId();

    boolean productExists = productRepository.existsById(productId);

    if (!productExists) {
      return GetProductDeliveryMethodsByProductId.Result.notFound(productId);
    }

    List<DeliveryMethod> deliveryMethods =
        productRepository.getProductDeliveryMethodsByProductId(productId);
    List<DeliveryMethodDto> deliveryMethodDtos =
        deliveryMethods.stream().map(DeliveryMethodDto::of).toList();

    return GetProductDeliveryMethodsByProductId.Result.success(deliveryMethodDtos);
  }

  @Override
  @Transactional(readOnly = true)
  public GetProductMediaByProductId.Result getProductMediaByProductId(
      GetProductMediaByProductId operation) {
    Long productId = operation.getProductId();
    Optional<List<ProductMedia>> productMedia =
        productRepository.getProductMediaByProductId(productId);
    Optional<List<ProductMediaDto>> productMediaDtos =
        productMedia.map(list -> list.stream().map(ProductMediaDto::of).toList());

    if (productMediaDtos.isPresent()) {
      return GetProductMediaByProductId.Result.success(productMediaDtos.get());
    }

    return GetProductMediaByProductId.Result.notFound(productId);
  }

  @Override
  @Transactional(readOnly = true)
  public GetProductCharacteristicsByProductId.Result getProductCharacteristicsByProductId(
      GetProductCharacteristicsByProductId operation) {
    Long productId = operation.getProductId();

    if (!productRepository.existsById(productId)) {
      return GetProductCharacteristicsByProductId.Result.notFound(productId);
    }

    List<ProductCharacteristic> productCharacteristics =
        productCharacteristicRepository.getAllByProductId(productId);
    List<ProductCharacteristicDto> productCharacteristicDtos =
        productCharacteristics.stream().map(ProductCharacteristicDto::of).toList();
    return GetProductCharacteristicsByProductId.Result.success(productCharacteristicDtos);

  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public GetProductFaqByProductId.Result getProductFaqByProductId(
      GetProductFaqByProductId operation) {
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
    return productCreatingService.create(operation);
  }

  @Override
  @Transactional
  public UpdateProduct.Result updateProduct(UpdateProduct operation) {
    return productUpdatingService.update(operation);
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
            .products(
                entry.getValue().stream().filter(Objects::nonNull).collect(Collectors.toList()))
            .build()
        )
        .toList();

    return GetSearchHints.Result.success(groupedSearchHints);
  }

  private String getFullSlug(String slug) {
    StringBuilder fullSlug = new StringBuilder(getSlugWithoutLevel(slug));
    String parentSlug = slug;

    while (true) {
      Optional<Category> category =
          categoryRepository.getCategoryBySlugWithChildren(new CategorySlug(parentSlug));

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

  @Override
  @Transactional
  public DeleteProductById.Result deleteProductById(DeleteProductById operation) {
    Optional<Product> product =
        productRepository.getProductByIdWithAnyApproveStatus(operation.getProductId());

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

  @Transactional(readOnly = true)
  protected ProductDto loadFullProduct(ProductDto productDto, ProductView view, Locale locale) {
    // Vendor
    Optional<User> userOptional = userRepository.getById(view.getUserId());
    if (userOptional.isPresent() && userOptional.get().getVendorDetails() != null) {
      VendorDto vendorDto = VendorDto.of(userOptional.get(), locale);

      // Vendor Countries
      List<VendorCountryView> vendorCountryViewList =
          vendorCountryRepository.getAllViewsByVendorDetailsIdAndLang(
              userOptional.get().getVendorDetails().getId(),
              locale.getLanguage()
          );
      List<VendorCountryDto> vendorCountryDtoList = vendorCountryViewList.stream()
          .map(VendorCountryDto::of)
          .toList();

      // Vendor Product Categories
      List<VendorProductCategoryView> vendorProductCategoryViewList =
          vendorProductCategoryRepository.getAllViewsByVendorDetailsIdAndLang(
              userOptional.get().getVendorDetails().getId(),
              locale.getLanguage()
          );
      List<VendorProductCategoryDto> vendorProductCategoryDtoList =
          vendorProductCategoryViewList.stream()
              .map(VendorProductCategoryDto::of)
              .toList();

      // Vendor Faq
      List<VendorFaqView> vendorFaqViewList =
          vendorFaqRepository.getAllViewsByVendorDetailsIdAndLang(
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
    List<DeliveryMethodView> deliveryMethodViewList =
        deliveryMethodRepository.getAllDeliveryMethodViewsByProductIdLang(
            productDto.getId(),
            locale.getLanguage()
        );
    List<DeliveryMethodDto> deliveryMethodDtoList =
        deliveryMethodViewList.stream().map(DeliveryMethodDto::of).toList();
    productDto.setDeliveryMethods(deliveryMethodDtoList);

    // Delivery Terms
    List<DeliveryTerm> deliveryTerms = deliveryTermRepository.getAllByProductId(productDto.getId());
    List<DeliveryTermDto> deliveryTermDtos =
        deliveryTerms.stream().map(DeliveryTermDto::of).toList();
    productDto.setDeliveryTerms(deliveryTermDtos);

    // Category
    CategoryDto categoryDtoFromCache =
        categoryCacheManager.getCategory(view.getCategoryId(), locale);

    if (categoryDtoFromCache != null) {
      productDto.setCategory(categoryDtoFromCache);
    } else {
      Optional<Category> category = categoryRepository.getById(view.getCategoryId());
      if (category.isPresent()) {
        CategoryDto categoryDto = CategoryDto.ofWithoutChildren(category.get());
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
    List<SimilarProductView> similarProductViewList =
        productRepository.getAllSimilarProductViewsByProductIdAndLang(
            productDto.getId(),
            locale.getLanguage()
        );
    List<SimilarProductDto> similarProductDtoList = similarProductViewList.stream()
        .map(SimilarProductDto::of)
        .toList();
    productDto.setSimilarProducts(similarProductDtoList);

    // Characteristics
    List<ProductCharacteristicView> productCharacteristicList =
        productCharacteristicRepository.findAllViewsByProductIdAndLang(
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
    List<ProductReviewMediaView> productReviewMediaList =
        productReviewMediaRepository.getAllViewsByProductId(
            productDto.getId()
        );
    List<ProductReviewMediaDto> productReviewMediaDtoList = productReviewMediaList.stream()
        .map(ProductReviewMediaDto::of)
        .toList();
    productDto.setReviewsMedia(productReviewMediaDtoList);

    // Delivery Method Details
    List<ProductDeliveryMethodDetailsView> productDeliveryMethodDetailsViewList =
        productDeliveryMethodDetailsRepository.getAllViewsByProductIdAndLang(
            productDto.getId(),
            locale.getLanguage()
        );
    List<ProductDeliveryMethodDetailsDto> productDeliveryMethodDetailsDtoList =
        productDeliveryMethodDetailsViewList.stream()
            .map(ProductDeliveryMethodDetailsDto::of).toList();
    productDto.setDeliveryMethodsDetails(productDeliveryMethodDetailsDtoList);

    // Packaging Options
    List<ProductPackageOptionView> productPackageOptionViewList =
        productPackageOptionsRepository.getAllViewsByProductIdAndLang(
            productDto.getId(),
            locale.getLanguage()
        );
    List<ProductPackageOptionDto> productPackageOptionDtoList =
        productPackageOptionViewList.stream()
            .map(ProductPackageOptionDto::of).toList();
    productDto.setPackagingOptions(productPackageOptionDtoList);

    return localizationManager.localizePrice(productDto, locale);
  }

  @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
  protected ProductWithTranslationsDto loadFullProduct(ProductWithTranslationsDto productDto,
                                                       ProductWithTranslationsView view,
                                                       Locale locale) {
    // Vendor
    Optional<UserView> userView = userRepository.getViewById(view.getUserId());

    if (userView.isPresent()) {
      VendorDto vendorDto = VendorDto.of(userView.get(), locale);

      // Vendor Countries
      List<VendorCountryView> vendorCountryViewList =
          vendorCountryRepository.getAllViewsByVendorDetailsIdAndLang(
              userView.get().getVendorDetails().getId(),
              locale.getLanguage()
          );
      List<VendorCountryDto> vendorCountryDtoList = vendorCountryViewList.stream()
          .map(VendorCountryDto::of)
          .toList();

      // Vendor Product Categories
      List<VendorProductCategoryView> vendorProductCategoryViewList =
          vendorProductCategoryRepository.getAllViewsByVendorDetailsIdAndLang(
              userView.get().getVendorDetails().getId(),
              locale.getLanguage()
          );
      List<VendorProductCategoryDto> vendorProductCategoryDtoList =
          vendorProductCategoryViewList.stream()
              .map(VendorProductCategoryDto::of)
              .toList();

      // Vendor Faq
      List<VendorFaqView> vendorFaqViewList =
          vendorFaqRepository.getAllViewsByVendorDetailsIdAndLang(
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
    List<DeliveryMethodView> deliveryMethodViewList =
        deliveryMethodRepository.getAllDeliveryMethodViewsByProductIdLang(
            productDto.getId(),
            locale.getLanguage()
        );
    List<DeliveryMethodDto> deliveryMethodDtoList =
        deliveryMethodViewList.stream().map(DeliveryMethodDto::of).toList();
    productDto.setDeliveryMethods(deliveryMethodDtoList);

    // Delivery Terms
    List<DeliveryTerm> deliveryTerms = deliveryTermRepository.getAllByProductId(productDto.getId());
    List<DeliveryTermDto> deliveryTermDtos =
        deliveryTerms.stream().map(DeliveryTermDto::of).toList();
    productDto.setDeliveryTerms(deliveryTermDtos);

    // Category
    Optional<Category> category = categoryRepository.getById(view.getCategoryId());

    if (category.isPresent()) {
      CategoryDto categoryDto = CategoryDto.ofWithoutChildren(category.get());
      productDto.setCategory(categoryDto);
    }

    // Media
    List<ProductMediaWithTranslationsView> productMediaList =
        productMediaRepository.getAllViewsWithTranslationsByProductIdAndLang(
            productDto.getId(), locale.getLanguage());
    List<ProductMediaWithTranslationsDto> productMediaDtoList = productMediaList.stream()
        .map(ProductMediaWithTranslationsDto::of)
        .toList();
    productDto.setMedia(productMediaDtoList);

    // Similar Products
    List<SimilarProductView> similarProductViewList =
        productRepository.getAllSimilarProductViewsByProductIdAndLang(
            productDto.getId(),
            locale.getLanguage()
        );
    List<SimilarProductDto> similarProductDtoList = similarProductViewList.stream()
        .map(SimilarProductDto::of)
        .toList();
    productDto.setSimilarProducts(similarProductDtoList);

    // Characteristics
    List<ProductCharacteristicWithTranslationsView> productCharacteristicList =
        productCharacteristicRepository
            .findAllViewsWithTranslationsByProductIdAndLang(productDto.getId(),
                locale.getLanguage());
    List<ProductCharacteristicWithTranslationsDto> productCharacteristicDtoList =
        productCharacteristicList.stream()
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
    List<ProductReviewMediaView> productReviewMediaList =
        productReviewMediaRepository.getAllViewsByProductId(
            productDto.getId()
        );
    List<ProductReviewMediaDto> productReviewMediaDtoList = productReviewMediaList.stream()
        .map(ProductReviewMediaDto::of)
        .toList();
    productDto.setReviewsMedia(productReviewMediaDtoList);

    // Delivery Method Details
    List<ProductDeliveryMethodDetailsWithTranslationsView> productDeliveryMethodDetailsViewList =
        productDeliveryMethodDetailsRepository
            .getAllViewsWithTranslationsByProductIdAndLang(productDto.getId(),
                locale.getLanguage());
    List<ProductDeliveryMethodDetailsWithTranslationsDto> productDeliveryMethodDetailsDtoList =
        productDeliveryMethodDetailsViewList.stream()
            .map(ProductDeliveryMethodDetailsWithTranslationsDto::of).toList();
    productDto.setDeliveryMethodsDetails(productDeliveryMethodDetailsDtoList);

    // Packaging Options
    List<ProductPackageOptionWithTranslationsView> productPackageOptionViewList =
        productPackageOptionsRepository
            .getAllViewsWithTranslationsByProductIdAndLang(productDto.getId(),
                locale.getLanguage());
    List<ProductPackageOptionWithTranslationsDto> productPackageOptionDtoList =
        productPackageOptionViewList.stream()
            .map(ProductPackageOptionWithTranslationsDto::of).toList();
    productDto.setPackagingOptions(productPackageOptionDtoList);

    return productDto;
  }
}