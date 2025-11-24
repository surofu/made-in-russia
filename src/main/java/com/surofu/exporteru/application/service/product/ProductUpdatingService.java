package com.surofu.exporteru.application.service.product;

import com.surofu.exporteru.application.command.product.update.UpdateProductCharacteristicCommand;
import com.surofu.exporteru.application.command.product.update.UpdateProductDeliveryMethodDetailsCommand;
import com.surofu.exporteru.application.command.product.update.UpdateProductFaqCommand;
import com.surofu.exporteru.application.command.product.update.UpdateProductMediaAltTextCommand;
import com.surofu.exporteru.application.command.product.update.UpdateProductPackageOptionCommand;
import com.surofu.exporteru.application.command.product.update.UpdateProductPriceCommand;
import com.surofu.exporteru.application.enums.FileStorageFolders;
import com.surofu.exporteru.application.exception.EmptyTranslationException;
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
import com.surofu.exporteru.core.model.product.media.ProductMediaUrl;
import com.surofu.exporteru.core.model.product.packageOption.ProductPackageOption;
import com.surofu.exporteru.core.model.product.packageOption.ProductPackageOptionName;
import com.surofu.exporteru.core.model.product.packageOption.ProductPackageOptionPrice;
import com.surofu.exporteru.core.model.product.packageOption.ProductPackageOptionPriceUnit;
import com.surofu.exporteru.core.model.product.price.ProductPrice;
import com.surofu.exporteru.core.model.product.price.ProductPriceCurrency;
import com.surofu.exporteru.core.model.product.price.ProductPriceDiscount;
import com.surofu.exporteru.core.model.product.price.ProductPriceDiscountedPrice;
import com.surofu.exporteru.core.model.product.price.ProductPriceOriginalPrice;
import com.surofu.exporteru.core.model.product.price.ProductPriceQuantityRange;
import com.surofu.exporteru.core.model.product.price.ProductPriceUnit;
import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetails;
import com.surofu.exporteru.core.model.vendorDetails.media.VendorMedia;
import com.surofu.exporteru.core.model.vendorDetails.media.VendorMediaMimeType;
import com.surofu.exporteru.core.model.vendorDetails.media.VendorMediaUrl;
import com.surofu.exporteru.core.repository.FileStorageRepository;
import com.surofu.exporteru.core.repository.ProductCharacteristicRepository;
import com.surofu.exporteru.core.repository.ProductDeliveryMethodDetailsRepository;
import com.surofu.exporteru.core.repository.ProductFaqRepository;
import com.surofu.exporteru.core.repository.ProductMediaRepository;
import com.surofu.exporteru.core.repository.ProductPackageOptionsRepository;
import com.surofu.exporteru.core.repository.ProductPriceRepository;
import com.surofu.exporteru.core.repository.ProductRepository;
import com.surofu.exporteru.core.repository.TranslationRepository;
import com.surofu.exporteru.core.service.product.operation.UpdateProduct;
import com.surofu.exporteru.infrastructure.persistence.category.JpaCategoryRepository;
import com.surofu.exporteru.infrastructure.persistence.deliveryMethod.JpaDeliveryMethodRepository;
import com.surofu.exporteru.infrastructure.persistence.user.JpaUserRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductUpdatingService {
  private final ProductRepository productRepository;
  private final JpaCategoryRepository categoryRepository;
  private final JpaDeliveryMethodRepository deliveryMethodRepository;
  private final TranslationRepository translationRepository;
  private final FileStorageRepository fileStorageRepository;
  private final ProductSavingService productSavingService;
  private final JpaUserRepository userRepository;
  private final ProductCharacteristicRepository productCharacteristicRepository;
  private final ProductFaqRepository productFaqRepository;
  private final ProductPriceRepository productPriceRepository;
  private final ProductDeliveryMethodDetailsRepository productDeliveryMethodDetailsRepository;
  private final ProductPackageOptionsRepository productPackageOptionsRepository;
  private final ProductMediaRepository productMediaRepository;

  @Transactional(timeout = 30)
  public UpdateProduct.Result updateProduct(UpdateProduct operation) {
    Optional<Product> productOptional = productRepository.getProductById(operation.getProductId());

    if (productOptional.isEmpty()) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return UpdateProduct.Result.productNotFound(operation.getProductId());
    }

    Product product = productOptional.get();

    UpdateProduct.Result validationResult = validateUpdateProductOperation(operation);
    if (!(validationResult instanceof UpdateProduct.Result.Success)) {
      return validationResult;
    }

    User user = userRepository.getUserById(operation.getSecurityUser().getUser().getId())
        .orElseThrow(() -> new RuntimeException("User not found"));
    VendorDetails vendorDetails = user.getVendorDetails();

    Optional<Category> categoryOptional = categoryRepository.getById(operation.getCategoryId());
    if (categoryOptional.isEmpty()) {
      return UpdateProduct.Result.categoryNotFound(operation.getCategoryId());
    }

    Category category = categoryOptional.get();

    Optional<Long> firstNotExistsDeliveryMethodIdOptional =
        deliveryMethodRepository.firstNotExists(operation.getDeliveryMethodIds());
    if (firstNotExistsDeliveryMethodIdOptional.isPresent()) {
      return UpdateProduct.Result.deliveryMethodNotFound(
          firstNotExistsDeliveryMethodIdOptional.get());
    }

    List<DeliveryMethod> deliveryMethods =
        deliveryMethodRepository.getAllDeliveryMethodsByIds(operation.getDeliveryMethodIds());

    Optional<Long> firstNotExistsSimilarProductIdOptional =
        productRepository.firstNotExists(operation.getSimilarProductIds());
    if (firstNotExistsSimilarProductIdOptional.isPresent()) {
      return UpdateProduct.Result.similarProductNotFound(
          firstNotExistsSimilarProductIdOptional.get());
    }

    List<Long> limitedSimilarProductIds = operation.getSimilarProductIds().stream()
        .limit(50)
        .collect(Collectors.toList());
    List<Product> similarProducts = productRepository.findAllByIds(limitedSimilarProductIds);

    // Обновляем основные поля продукта
    updateProductBasicFields(product, operation, category, deliveryMethods, similarProducts);

    try {
      // Обновляем связанные сущности с переводами через репозитории
      UpdateProduct.Result relatedEntitiesResult =
          updateRelatedEntitiesWithRepositories(product, operation);
      if (relatedEntitiesResult != UpdateProduct.Result.Success.INSTANCE) {
        return relatedEntitiesResult;
      }

      // Обновляем медиа через репозитории
      UpdateProduct.Result mediaResult = updateMediaWithRepositories(product, operation);
      if (mediaResult != UpdateProduct.Result.Success.INSTANCE) {
        return mediaResult;
      }

      // Обновляем медиа vendor details
      UpdateProduct.Result vendorMediaResult = updateVendorDetailsMedia(vendorDetails, operation);
      if (vendorMediaResult != UpdateProduct.Result.Success.INSTANCE) {
        return vendorMediaResult;
      }

    } catch (EmptyTranslationException e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return UpdateProduct.Result.emptyTranslations(e.getMessage());
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return UpdateProduct.Result.translationError(e);
    }

    return productSavingService.saveUpdate(product);
  }

  private void updateProductBasicFields(Product product, UpdateProduct operation,
                                        Category category, List<DeliveryMethod> deliveryMethods,
                                        List<Product> similarProducts) {
    product.setApproveStatus(ApproveStatus.PENDING);
    product.setCategory(category);
    product.setDeliveryMethods(new HashSet<>(deliveryMethods));
    product.setSimilarProducts(new HashSet<>(similarProducts));
    product.setMinimumOrderQuantity(operation.getMinimumOrderQuantity());
    product.setDiscountExpirationDate(operation.getDiscountExpirationDate());

    // Устанавливаем заголовок с переводами сразу
    product.setTitle(new ProductTitle(
        operation.getProductTitle().getValue(),
        translationRepository.expand(operation.getProductTitle().getTranslations())
    ));

    // Устанавливаем описание с переводами сразу
    product.setDescription(new ProductDescription(
        operation.getProductDescription().getMainDescription(),
        operation.getProductDescription().getFurtherDescription(),
        translationRepository.expand(
            operation.getProductDescription().getMainDescriptionTranslations()),
        translationRepository.expand(
            operation.getProductDescription().getFurtherDescriptionTranslations())
    ));
  }

  private UpdateProduct.Result updateRelatedEntitiesWithRepositories(Product product,
                                                                     UpdateProduct operation) {
    try {
      UpdateProduct.Result pricesResult = updatePricesWithRepository(product, operation);
      if (pricesResult != UpdateProduct.Result.Success.INSTANCE) {
        return pricesResult;
      }

      UpdateProduct.Result characteristicsResult =
          updateCharacteristicsWithRepository(product, operation);
      if (characteristicsResult != UpdateProduct.Result.Success.INSTANCE) {
        return characteristicsResult;
      }

      UpdateProduct.Result faqResult = updateFaqWithRepository(product, operation);
      if (faqResult != UpdateProduct.Result.Success.INSTANCE) {
        return faqResult;
      }

      UpdateProduct.Result deliveryDetailsResult =
          updateDeliveryMethodDetailsWithRepository(product, operation);
      if (deliveryDetailsResult != UpdateProduct.Result.Success.INSTANCE) {
        return deliveryDetailsResult;
      }

      UpdateProduct.Result packagingOptionsResult =
          updatePackagingOptionsWithRepository(product, operation);
      if (packagingOptionsResult != UpdateProduct.Result.Success.INSTANCE) {
        return packagingOptionsResult;
      }

      return UpdateProduct.Result.success();
    } catch (EmptyTranslationException e) {
      return UpdateProduct.Result.emptyTranslations(e.getMessage());
    } catch (Exception e) {
      return UpdateProduct.Result.errorSavingProduct(e);
    }
  }

  private UpdateProduct.Result updatePricesWithRepository(Product product, UpdateProduct operation)
      throws EmptyTranslationException {
    // Получаем старые цены и удаляем их через репозиторий
    List<ProductPrice> oldPrices = productPriceRepository.getAllByProductId(product.getId());
    if (!oldPrices.isEmpty()) {
      productPriceRepository.deleteAll(oldPrices);
    }

    // Создаем новые цены с переводами
    List<ProductPrice> newPrices = new ArrayList<>();
    for (UpdateProductPriceCommand command : operation.getUpdateProductPriceCommands()) {
      ProductPrice price = new ProductPrice();
      price.setProduct(product);
      price.setQuantityRange(
          ProductPriceQuantityRange.of(command.quantityFrom(), command.quantityTo()));
      price.setCurrency(ProductPriceCurrency.of(command.currency()));
      price.setUnit(ProductPriceUnit.of(command.unit()));
      price.setOriginalPrice(ProductPriceOriginalPrice.of(command.price()));
      price.setDiscount(ProductPriceDiscount.of(command.discount()));
      price.setDiscountedPrice(
          ProductPriceDiscountedPrice.of(
              command.price().subtract(
                  command.price().multiply(command.discount())
                      .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)
              )
          )
      );
      // Устанавливаем переводы для unit сразу
      price.getUnit().setTranslations(translationRepository.expand(command.unit()));
      newPrices.add(price);
    }

    // Сохраняем новые цены через репозиторий
    productPriceRepository.saveAll(newPrices);

    // Обновляем коллекцию в продукте
    product.getPrices().clear();
    product.getPrices().addAll(newPrices);

    return UpdateProduct.Result.success();
  }

  private UpdateProduct.Result updateCharacteristicsWithRepository(Product product,
                                                                   UpdateProduct operation)
      throws EmptyTranslationException {
    // Получаем старые характеристики и удаляем их через репозиторий
    List<ProductCharacteristic> oldCharacteristics =
        productCharacteristicRepository.getAllByProductId(product.getId());
    if (!oldCharacteristics.isEmpty()) {
      productCharacteristicRepository.deleteAll(oldCharacteristics);
    }

    // Создаем новые характеристики с переводами
    List<ProductCharacteristic> newCharacteristics = new ArrayList<>();
    for (UpdateProductCharacteristicCommand command : operation.getUpdateProductCharacteristicCommands()) {
      ProductCharacteristic characteristic = new ProductCharacteristic();
      characteristic.setProduct(product);
      characteristic.setName(new ProductCharacteristicName(command.name(),
          translationRepository.expand(command.nameTranslations())));
      characteristic.setValue(new ProductCharacteristicValue(command.value(),
          translationRepository.expand(command.valueTranslations())));
      newCharacteristics.add(characteristic);
    }

    // Сохраняем новые характеристики через репозиторий
    productCharacteristicRepository.saveAll(newCharacteristics);

    // Обновляем коллекцию в продукте
    product.getCharacteristics().clear();
    product.getCharacteristics().addAll(newCharacteristics);

    return UpdateProduct.Result.success();
  }

  private UpdateProduct.Result updateFaqWithRepository(Product product, UpdateProduct operation)
      throws EmptyTranslationException {
    // Получаем старые FAQ и удаляем их через репозиторий
    List<ProductFaq> oldFaq = productFaqRepository.getAllByProductId(product.getId());
    if (!oldFaq.isEmpty()) {
      productFaqRepository.deleteAll(oldFaq);
    }

    // Создаем новые FAQ с переводами
    List<ProductFaq> newFaq = new ArrayList<>();
    for (UpdateProductFaqCommand command : operation.getUpdateProductFaqCommands()) {
      ProductFaq faq = new ProductFaq();
      faq.setProduct(product);
      faq.setQuestion(ProductFaqQuestion.of(command.question()));
      faq.setAnswer(ProductFaqAnswer.of(command.answer()));

      // Устанавливаем переводы сразу
      faq.getQuestion()
          .setTranslations(translationRepository.expand(command.questionTranslations()));
      faq.getAnswer().setTranslations(translationRepository.expand(command.answerTranslations()));

      newFaq.add(faq);
    }

    // Сохраняем новые FAQ через репозиторий
    productFaqRepository.saveAll(newFaq);

    // Обновляем коллекцию в продукте
    product.getFaq().clear();
    product.getFaq().addAll(newFaq);

    return UpdateProduct.Result.success();
  }

  private UpdateProduct.Result updateDeliveryMethodDetailsWithRepository(Product product,
                                                                         UpdateProduct operation)
      throws EmptyTranslationException {
    // Получаем старые детали доставки и удаляем их через репозиторий
    List<ProductDeliveryMethodDetails> oldDetails =
        productDeliveryMethodDetailsRepository.getAllByProductId(product.getId());
    if (!oldDetails.isEmpty()) {
      productDeliveryMethodDetailsRepository.deleteAll(oldDetails);
    }

    // Создаем новые детали доставки с переводами
    List<ProductDeliveryMethodDetails> newDetails = new ArrayList<>();
    for (UpdateProductDeliveryMethodDetailsCommand command : operation.getUpdateProductDeliveryMethodDetailsCommands()) {
      ProductDeliveryMethodDetails detail = new ProductDeliveryMethodDetails();
      detail.setProduct(product);
      detail.setName(ProductDeliveryMethodDetailsName.of(command.name()));
      detail.setValue(ProductDeliveryMethodDetailsValue.of(command.value()));

      // Устанавливаем переводы сразу
      detail.getName().setTranslations(translationRepository.expand(command.nameTranslations()));
      detail.getValue().setTranslations(translationRepository.expand(command.valueTranslations()));

      newDetails.add(detail);
    }

    // Сохраняем новые детали доставки через репозиторий
    productDeliveryMethodDetailsRepository.saveAll(newDetails);

    // Обновляем коллекцию в продукте
    product.getDeliveryMethodDetails().clear();
    product.getDeliveryMethodDetails().addAll(newDetails);

    return UpdateProduct.Result.success();
  }

  private UpdateProduct.Result updatePackagingOptionsWithRepository(Product product,
                                                                    UpdateProduct operation)
      throws EmptyTranslationException {
    // Получаем старые опции упаковки и удаляем их через репозиторий
    List<ProductPackageOption> oldOptions =
        productPackageOptionsRepository.getAllByProductId(product.getId());
    if (!oldOptions.isEmpty()) {
      productPackageOptionsRepository.deleteAll(oldOptions);
    }

    // Создаем новые опции упаковки с переводами
    List<ProductPackageOption> newOptions = new ArrayList<>();
    for (UpdateProductPackageOptionCommand command : operation.getUpdateProductPackageOptionCommands()) {
      ProductPackageOption option = new ProductPackageOption();
      option.setProduct(product);
      option.setName(ProductPackageOptionName.of(command.name()));
      option.setPrice(ProductPackageOptionPrice.of(command.price()));
      option.setPriceUnit(ProductPackageOptionPriceUnit.of(command.priceUnit()));

      // Устанавливаем переводы сразу
      option.getName().setTranslations(translationRepository.expand(command.nameTranslations()));

      newOptions.add(option);
    }

    // Сохраняем новые опции упаковки через репозиторий
    productPackageOptionsRepository.saveAll(newOptions);

    // Обновляем коллекцию в продукте
    product.getPackageOptions().clear();
    product.getPackageOptions().addAll(newOptions);

    return UpdateProduct.Result.success();
  }

  private UpdateProduct.Result updateMediaWithRepositories(Product product,
                                                           UpdateProduct operation) {
    try {
      // Получаем текущие медиа
      Set<ProductMedia> existingMedia = new HashSet<>(product.getMedia());

      // Определяем медиа для сохранения и удаления
      Set<ProductMedia> mediaToKeep = existingMedia.stream()
          .filter(m -> operation.getOldProductMedia().stream()
              .anyMatch(dto -> Objects.equals(dto.id(), m.getId())))
          .collect(Collectors.toSet());

      Set<ProductMedia> mediaToDelete = existingMedia.stream()
          .filter(m -> !mediaToKeep.contains(m))
          .collect(Collectors.toSet());

      // Удаляем старые медиа через репозиторий
      if (!mediaToDelete.isEmpty()) {
        productMediaRepository.deleteAll(mediaToDelete);

        // Удаляем файлы старых медиа
        List<String> mediaUrlsToDelete = mediaToDelete.stream()
            .map(ProductMedia::getUrl)
            .map(ProductMediaUrl::toString)
            .toList();
        fileStorageRepository.deleteMediaByLink(mediaUrlsToDelete.toArray(new String[0]));
      }

      // Создаем новые медиа с переводами
      List<ProductMedia> newMedia = new ArrayList<>(mediaToKeep);

      if (!operation.getProductMedia().isEmpty()) {
        List<String> urls = fileStorageRepository.uploadManyImagesToFolder(
            FileStorageFolders.PRODUCT_IMAGES.getValue(),
            operation.getProductMedia().toArray(new MultipartFile[] {}));

        for (int i = 0; i < operation.getProductMedia().size(); i++) {
          MultipartFile file = operation.getProductMedia().get(i);
          ProductMedia media = createProductMediaWithTranslations(file, product, i, operation);
          media.setUrl(ProductMediaUrl.of(urls.get(i)));
          newMedia.add(media);
        }
      }

      // Сохраняем все медиа через репозиторий
      productMediaRepository.saveAll(newMedia);

      // Обновляем коллекцию в продукте
      product.getMedia().clear();
      product.getMedia().addAll(newMedia);

      // Обновляем preview image
      if (!product.getMedia().isEmpty()) {
        product.setPreviewImageUrl(
            ProductPreviewImageUrl.of(product.getMedia().iterator().next().getUrl().toString()));
      } else {
        return UpdateProduct.Result.errorSavingProduct(new RuntimeException("Empty result files"));
      }

      return UpdateProduct.Result.success();
    } catch (Exception e) {
      log.error("Error updating media for product {}", product.getId(), e);
      return UpdateProduct.Result.errorSavingFiles(e);
    }
  }

  private ProductMedia createProductMediaWithTranslations(MultipartFile file, Product product,
                                                          int index,
                                                          UpdateProduct operation)
      throws EmptyTranslationException {
    ProductMedia media = new ProductMedia();
    media.setProduct(product);
    media.setMediaType(getMediaType(file));
    media.setMimeType(ProductMediaMimeType.of(file.getContentType()));

    // Устанавливаем alt text с переводами сразу
    if (index < operation.getUpdateProductMediaAltTextCommands().size()) {
      UpdateProductMediaAltTextCommand command =
          operation.getUpdateProductMediaAltTextCommands().get(index);
      media.setAltText(ProductMediaAltText.of(command.altText()));
      // Устанавливаем переводы для alt text сразу
      media.getAltText().setTranslations(translationRepository.expand(command.translations()));
    } else {
      String filename = file.getOriginalFilename();
      media.setAltText(ProductMediaAltText.of(filename));
      // Создаем базовые переводы для filename
      media.getAltText().setTranslations(translationRepository.expand(filename));
    }

    return media;
  }

  private UpdateProduct.Result updateVendorDetailsMedia(VendorDetails vendorDetails,
                                                        UpdateProduct operation) {
    try {
      if (operation.getProductVendorDetailsMedia().isEmpty()) {
        return UpdateProduct.Result.success();
      }

      Set<VendorMedia> existingMedia = new HashSet<>(vendorDetails.getMedia());

      // Определяем медиа для сохранения и удаления
      Set<VendorMedia> mediaToKeep = existingMedia.stream()
          .filter(m -> operation.getOldVendorDetailsMedia().stream()
              .anyMatch(dto -> Objects.equals(dto.id(), m.getId())))
          .collect(Collectors.toSet());

      Set<VendorMedia> mediaToDelete = existingMedia.stream()
          .filter(m -> !mediaToKeep.contains(m))
          .collect(Collectors.toSet());

      // Удаляем медиа из vendor details
      for (VendorMedia media : mediaToDelete) {
        vendorDetails.getMedia().remove(media);
      }

      // Добавляем новые медиа
      List<String> urls = fileStorageRepository.uploadManyImagesToFolder(
          FileStorageFolders.PRODUCT_IMAGES.getValue(),
          operation.getProductVendorDetailsMedia().toArray(new MultipartFile[] {}));

      for (int i = 0; i < operation.getProductVendorDetailsMedia().size(); i++) {
        MultipartFile file = operation.getProductVendorDetailsMedia().get(i);
        VendorMedia media = new VendorMedia();
        media.setVendorDetails(vendorDetails);
        media.setMediaType(getMediaType(file));
        media.setMimeType(VendorMediaMimeType.of(file.getContentType()));
        media.setUrl(VendorMediaUrl.of(urls.get(i)));
        vendorDetails.getMedia().add(media);
      }

      // Удаляем файлы старых медиа
      if (!mediaToDelete.isEmpty()) {
        List<String> mediaUrlsToDelete = mediaToDelete.stream()
            .map(VendorMedia::getUrl)
            .map(VendorMediaUrl::toString)
            .toList();
        fileStorageRepository.deleteMediaByLink(mediaUrlsToDelete.toArray(new String[0]));
      }

      return UpdateProduct.Result.success();
    } catch (Exception e) {
      log.error("Error updating vendor details media for vendor {}", vendorDetails.getId(), e);
      return UpdateProduct.Result.errorSavingFiles(e);
    }
  }

  private UpdateProduct.Result validateUpdateProductOperation(UpdateProduct operation) {
    UpdateProduct.Result filesResult = validateFiles(operation);
    if (!(filesResult instanceof UpdateProduct.Result.Success)) {
      return filesResult;
    }
    return UpdateProduct.Result.success();
  }

  private UpdateProduct.Result validateFiles(UpdateProduct operation) {
    for (MultipartFile file : operation.getProductMedia()) {
      if (file.isEmpty()) {
        return UpdateProduct.Result.emptyFile();
      }
      if (!validateFileMediaType(file)) {
        return UpdateProduct.Result.invalidMediaType(file.getContentType());
      }
    }

    for (MultipartFile file : operation.getProductVendorDetailsMedia()) {
      if (file.isEmpty()) {
        return UpdateProduct.Result.emptyFile();
      }
      if (!validateFileMediaType(file)) {
        return UpdateProduct.Result.invalidMediaType(file.getContentType());
      }
    }

    return UpdateProduct.Result.success();
  }

  private boolean validateFileMediaType(MultipartFile file) {
    if (file.getContentType() == null) {
      return false;
    }
    return file.getContentType().startsWith("image/") || file.getContentType().startsWith("video/");
  }

  private MediaType getMediaType(MultipartFile file) {
    String contentType = file.getContentType();
    if (contentType == null) {
      throw new IllegalArgumentException("File content type is null");
    }
    if (contentType.startsWith("image/")) {
      return MediaType.IMAGE;
    }
    if (contentType.startsWith("video/")) {
      return MediaType.VIDEO;
    }
    throw new IllegalArgumentException("Unsupported content type: " + contentType);
  }
}