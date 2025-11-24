package com.surofu.exporteru.application.service.product;

import com.surofu.exporteru.application.command.product.create.CreateProductCharacteristicCommand;
import com.surofu.exporteru.application.command.product.create.CreateProductDeliveryMethodDetailsCommand;
import com.surofu.exporteru.application.command.product.create.CreateProductFaqCommand;
import com.surofu.exporteru.application.command.product.create.CreateProductMediaAltTextCommand;
import com.surofu.exporteru.application.command.product.create.CreateProductPackageOptionCommand;
import com.surofu.exporteru.application.command.product.create.CreateProductPriceCommand;
import com.surofu.exporteru.application.enums.FileStorageFolders;
import com.surofu.exporteru.core.model.category.Category;
import com.surofu.exporteru.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.exporteru.core.model.media.MediaType;
import com.surofu.exporteru.core.model.moderation.ApproveStatus;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.ProductDescription;
import com.surofu.exporteru.core.model.product.ProductPreviewImageUrl;
import com.surofu.exporteru.core.model.product.ProductTitle;
import com.surofu.exporteru.core.model.product.characteristic.ProductCharacteristic;
import com.surofu.exporteru.core.model.product.characteristic.ProductCharacteristicName;
import com.surofu.exporteru.core.model.product.characteristic.ProductCharacteristicValue;
import com.surofu.exporteru.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetails;
import com.surofu.exporteru.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetailsName;
import com.surofu.exporteru.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetailsValue;
import com.surofu.exporteru.core.model.product.faq.ProductFaq;
import com.surofu.exporteru.core.model.product.faq.ProductFaqAnswer;
import com.surofu.exporteru.core.model.product.faq.ProductFaqQuestion;
import com.surofu.exporteru.core.model.product.media.ProductMedia;
import com.surofu.exporteru.core.model.product.media.ProductMediaAltText;
import com.surofu.exporteru.core.model.product.media.ProductMediaMimeType;
import com.surofu.exporteru.core.model.product.media.ProductMediaPosition;
import com.surofu.exporteru.core.model.product.media.ProductMediaUrl;
import com.surofu.exporteru.core.model.product.packageOption.ProductPackageOption;
import com.surofu.exporteru.core.model.product.packageOption.ProductPackageOptionName;
import com.surofu.exporteru.core.model.product.packageOption.ProductPackageOptionPrice;
import com.surofu.exporteru.core.model.product.packageOption.ProductPackageOptionPriceUnit;
import com.surofu.exporteru.core.model.product.price.ProductPrice;
import com.surofu.exporteru.core.model.product.price.ProductPriceCurrency;
import com.surofu.exporteru.core.model.product.price.ProductPriceDiscount;
import com.surofu.exporteru.core.model.product.price.ProductPriceOriginalPrice;
import com.surofu.exporteru.core.model.product.price.ProductPriceQuantityRange;
import com.surofu.exporteru.core.model.product.price.ProductPriceUnit;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetails;
import com.surofu.exporteru.core.model.vendorDetails.media.VendorMedia;
import com.surofu.exporteru.core.model.vendorDetails.media.VendorMediaMimeType;
import com.surofu.exporteru.core.model.vendorDetails.media.VendorMediaPosition;
import com.surofu.exporteru.core.model.vendorDetails.media.VendorMediaUrl;
import com.surofu.exporteru.core.repository.FileStorageRepository;
import com.surofu.exporteru.core.repository.ProductRepository;
import com.surofu.exporteru.core.repository.TranslationRepository;
import com.surofu.exporteru.core.service.product.operation.CreateProduct;
import com.surofu.exporteru.infrastructure.persistence.category.JpaCategoryRepository;
import com.surofu.exporteru.infrastructure.persistence.deliveryMethod.JpaDeliveryMethodRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductCreatingService {
  private final ProductRepository productRepository;
  private final JpaCategoryRepository categoryRepository;
  private final JpaDeliveryMethodRepository deliveryMethodRepository;
  private final TranslationRepository translationRepository;
  private final FileStorageRepository fileStorageRepository;
  private final ProductSavingService productSavingService;

  @PersistenceContext
  private EntityManager entityManager;

  public CreateProduct.Result createProduct(CreateProduct operation) {
    entityManager.setFlushMode(FlushModeType.COMMIT);

    // ---------- Validation ---------- //
    CreateProduct.Result validationResult = validateCreateProductOperation(operation);

    if (!(validationResult instanceof CreateProduct.Result.Success)) {
      return validationResult;
    }

    // ---------- Loading dependencies ---------- //
    // Category
    Optional<Category> categoryOptional = categoryRepository.getById(operation.getCategoryId());

    if (categoryOptional.isEmpty()) {
      return CreateProduct.Result.categoryNotFound(operation.getCategoryId());
    }

    Category category = categoryOptional.get();

    // Delivery Method
    Optional<Long> firstNotExistsDeliveryMethodIdOptional =
        deliveryMethodRepository.firstNotExists(operation.getDeliveryMethodIds());

    if (firstNotExistsDeliveryMethodIdOptional.isPresent()) {
      return CreateProduct.Result.deliveryMethodNotFound(
          firstNotExistsDeliveryMethodIdOptional.get());
    }

    List<DeliveryMethod> deliveryMethods =
        deliveryMethodRepository.getAllDeliveryMethodsByIds(operation.getDeliveryMethodIds());

    // Similar product
    Optional<Long> firstNotExistsSimilarProductIdOptional =
        productRepository.firstNotExists(operation.getSimilarProductIds());

    if (firstNotExistsSimilarProductIdOptional.isPresent()) {
      return CreateProduct.Result.similarProductNotFound(
          firstNotExistsSimilarProductIdOptional.get());
    }

    List<Product> similarProducts =
        productRepository.findAllByIds(operation.getSimilarProductIds());

    // ---------- Setting new product ---------- //
    Product product = new Product();
    product.setUser(operation.getSecurityUser().getUser());
    product.setApproveStatus(ApproveStatus.PENDING);
    product.setCategory(category);
    product.setDeliveryMethods(new HashSet<>(deliveryMethods));
    product.setSimilarProducts(new HashSet<>(similarProducts));
    product.setMinimumOrderQuantity(operation.getMinimumOrderQuantity());
    product.setDiscountExpirationDate(operation.getDiscountExpirationDate());
    product.setTitle(new ProductTitle(
        operation.getProductTitle().getValue(),
        translationRepository.expand(operation.getProductTitle().getTranslations())
    ));
    product.setDescription(new ProductDescription(
        operation.getProductDescription().getMainDescription(),
        operation.getProductDescription().getFurtherDescription(),
        translationRepository.expand(
            operation.getProductDescription().getMainDescriptionTranslations()),
        translationRepository.expand(
            operation.getProductDescription().getFurtherDescriptionTranslations())
    ));
    settingPrices(product, operation);
    settingCharacteristics(product, operation);
    settingFaq(product, operation);
    settingDeliveryMethodDetails(product, operation);
    settingPackagingOption(product, operation);
    settingMedia(product, operation);
    settingVendorDetailsMedia(operation.getSecurityUser().getUser().getVendorDetails(),
        operation);

    return productSavingService.saveCreate(product);
  }

  // Setting
  private void settingPrices(Product product, CreateProduct operation) {
    Set<ProductPrice> prices = new HashSet<>();

    for (CreateProductPriceCommand command : operation.getCreateProductPriceCommands()) {
      ProductPrice price = new ProductPrice();
      price.setProduct(product);
      price.setQuantityRange(
          ProductPriceQuantityRange.of(command.quantityFrom(), command.quantityTo()));
      price.setCurrency(ProductPriceCurrency.of(command.currency()));
      price.setUnit(ProductPriceUnit.of(command.unit()));
      price.setOriginalPrice(ProductPriceOriginalPrice.of(command.price()));
      price.setDiscount(ProductPriceDiscount.of(command.discount()));
      price.getUnit().setTranslations(translationRepository.expand(command.unit()));
      prices.add(price);
    }

    product.setPrices(prices);
  }

  private void settingCharacteristics(Product product, CreateProduct operation) {
    Set<ProductCharacteristic> characteristics = new HashSet<>();

    for (CreateProductCharacteristicCommand command : operation.getCreateProductCharacteristicCommands()) {
      ProductCharacteristic characteristic = new ProductCharacteristic();
      characteristic.setProduct(product);
      characteristic.setName(new ProductCharacteristicName(command.name(),
          translationRepository.expand(command.nameTranslations())));
      characteristic.setValue(new ProductCharacteristicValue(command.value(),
          translationRepository.expand(command.valueTranslations())));
      characteristics.add(characteristic);
    }

    product.setCharacteristics(characteristics);
  }

  private void settingFaq(Product product, CreateProduct operation) {
    Set<ProductFaq> faqs = new HashSet<>();

    for (CreateProductFaqCommand command : operation.getCreateProductFaqCommands()) {
      ProductFaq faq = new ProductFaq();
      faq.setProduct(product);
      faq.setQuestion(new ProductFaqQuestion(command.question(),
          translationRepository.expand(command.questionTranslations())));
      faq.setAnswer(new ProductFaqAnswer(command.answer(),
          translationRepository.expand(command.answerTranslations())));
      faqs.add(faq);
    }

    product.setFaq(faqs);
  }

  private void settingDeliveryMethodDetails(Product product, CreateProduct operation) {
    Set<ProductDeliveryMethodDetails> details = new HashSet<>();

    for (CreateProductDeliveryMethodDetailsCommand command : operation.getCreateProductDeliveryMethodDetailsCommands()) {
      ProductDeliveryMethodDetails detail = new ProductDeliveryMethodDetails();
      detail.setProduct(product);
      detail.setName(ProductDeliveryMethodDetailsName.of(command.name()));
      detail.setValue(ProductDeliveryMethodDetailsValue.of(command.value()));
      detail.getName().setTranslations(translationRepository.expand(command.nameTranslations()));
      detail.getValue().setTranslations(translationRepository.expand(command.valueTranslations()));
      details.add(detail);
    }

    product.setDeliveryMethodDetails(details);
  }

  private void settingPackagingOption(Product product, CreateProduct operation) {
    Set<ProductPackageOption> options = new HashSet<>();

    for (CreateProductPackageOptionCommand command : operation.getCreateProductPackageOptionCommands()) {
      ProductPackageOption option = new ProductPackageOption();
      option.setProduct(product);
      option.setName(ProductPackageOptionName.of(command.name()));
      option.setPrice(ProductPackageOptionPrice.of(command.price()));
      option.setPriceUnit(ProductPackageOptionPriceUnit.of(command.priceUnit()));
      option.getName().setTranslations(translationRepository.expand(command.nameTranslations()));
      options.add(option);
    }

    product.setPackageOptions(options);
  }

  private void settingMedia(Product product, CreateProduct operation) {
    List<ProductMedia> mediaList = new ArrayList<>();

    // ИСПРАВЛЕНО: используем правильный список файлов для загрузки
    List<String> urls;
    try {
      urls = fileStorageRepository.uploadManyImagesToFolder(
          FileStorageFolders.PRODUCT_IMAGES.getValue(),
          operation.getProductMedia().toArray(new MultipartFile[] {})
      );
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    List<MultipartFile> productMedia = operation.getProductMedia();
    for (int i = 0; i < productMedia.size(); i++) {
      MultipartFile file = productMedia.get(i);
      ProductMedia media = new ProductMedia();
      media.setProduct(product);
      media.setPosition(ProductMediaPosition.of(i));
      media.setMediaType(getMediaType(file));
      media.setMimeType(ProductMediaMimeType.of(file.getContentType()));
      media.setUrl(ProductMediaUrl.of(urls.get(i)));
      mediaList.add(media);

      if (i < operation.getCreateProductMediaAltTextCommands().size()) {
        CreateProductMediaAltTextCommand command =
            operation.getCreateProductMediaAltTextCommands().get(i);
        media.setAltText(new ProductMediaAltText(command.altText(), translationRepository.expand(command.translations())));
      } else {
        media.setAltText(new ProductMediaAltText(file.getOriginalFilename(), new HashMap<>()));
      }
    }

    if (mediaList.isEmpty()) {
      throw new RuntimeException("No media files uploaded");
    }

    product.setPreviewImageUrl(
        ProductPreviewImageUrl.of(mediaList.get(0).getUrl().toString()));
    product.setMedia(new HashSet<>(mediaList));
  }

  private void settingVendorDetailsMedia(VendorDetails vendorDetails, CreateProduct operation) {
    if (operation.getProductVendorDetailsMedia().isEmpty()) {
      return;
    }

    List<VendorMedia> mediaList = new ArrayList<>();
    List<String> urls;
    try {
      urls = fileStorageRepository.uploadManyImagesToFolder(
          FileStorageFolders.PRODUCT_IMAGES.getValue(),
          operation.getProductVendorDetailsMedia().toArray(new MultipartFile[] {})
      );
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    List<MultipartFile> vendorDetailsMedia = operation.getProductVendorDetailsMedia();
    for (int i = 0; i < vendorDetailsMedia.size(); i++) {
      MultipartFile file = vendorDetailsMedia.get(i);
      VendorMedia media = new VendorMedia();
      media.setVendorDetails(vendorDetails);
      media.setPosition(VendorMediaPosition.of(i));
      media.setMediaType(getMediaType(file));
      media.setMimeType(VendorMediaMimeType.of(file.getContentType()));
      media.setUrl(VendorMediaUrl.of(urls.get(i)));
      mediaList.add(media);
    }

    // ИСПРАВЛЕНО: добавляем медиа к существующим, а не заменяем
    if (vendorDetails.getMedia() == null) {
      vendorDetails.setMedia(new HashSet<>());
    }
    vendorDetails.getMedia().addAll(mediaList);
  }

  // Validation
  private CreateProduct.Result validateCreateProductOperation(CreateProduct operation) {
    CreateProduct.Result filesResult = validateFiles(operation);

    if (!(filesResult instanceof CreateProduct.Result.Success)) {
      return filesResult;
    }

    return CreateProduct.Result.success();
  }

  private CreateProduct.Result validateFiles(CreateProduct operation) {
    if (operation.getProductMedia().isEmpty()) {
      return CreateProduct.Result.emptyFile();
    }

    for (MultipartFile file : operation.getProductMedia()) {
      if (file.isEmpty()) {
        return CreateProduct.Result.emptyFile();
      }

      if (!validateFileMediaType(file)) {
        return CreateProduct.Result.invalidMediaType(file.getContentType());
      }
    }

    for (MultipartFile file : operation.getProductVendorDetailsMedia()) {
      if (file.isEmpty()) {
        return CreateProduct.Result.emptyFile();
      }

      if (!validateFileMediaType(file)) {
        return CreateProduct.Result.invalidMediaType(file.getContentType());
      }
    }

    return CreateProduct.Result.success();
  }

  private boolean validateFileMediaType(MultipartFile file) {
    if (file.getContentType() == null) {
      return false;
    }
    return file.getContentType().startsWith("image/") || file.getContentType().startsWith("video/");
  }

  private MediaType getMediaType(MultipartFile file) {
    if (Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
      return MediaType.IMAGE;
    }

    if (Objects.requireNonNull(file.getContentType()).startsWith("video/")) {
      return MediaType.VIDEO;
    }

    throw new IllegalArgumentException("Unsupported content type: " + file.getContentType());
  }
}