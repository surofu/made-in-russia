package com.surofu.exporteru.application.service.product;

import com.surofu.exporteru.application.command.product.update.UpdateOldMediaDto;
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
import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetails;
import com.surofu.exporteru.core.model.vendorDetails.media.VendorMedia;
import com.surofu.exporteru.core.model.vendorDetails.media.VendorMediaMimeType;
import com.surofu.exporteru.core.model.vendorDetails.media.VendorMediaPosition;
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
import com.surofu.exporteru.infrastructure.persistence.vendor.media.JpaVendorMediaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
  private final JpaVendorMediaRepository vendorMediaRepository;

  @PersistenceContext
  private EntityManager entityManager;

  @Transactional(timeout = 30)
  public UpdateProduct.Result updateProduct(UpdateProduct operation) {
    try {
      entityManager.setFlushMode(FlushModeType.COMMIT);

      Optional<Product> productOptional =
          productRepository.getProductById(operation.getProductId());

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

      return productSavingService.saveUpdate(product);
    } catch (EmptyTranslationException e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return UpdateProduct.Result.emptyTranslations(e.getMessage());
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return UpdateProduct.Result.translationError(e);
    }
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
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return UpdateProduct.Result.emptyTranslations(e.getMessage());
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
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
      price.getUnit().setTranslations(translationRepository.expand(command.unit()));
      newPrices.add(price);
    }

    // Сохраняем новые цены через репозиторий
    productPriceRepository.saveAll(newPrices);
    return UpdateProduct.Result.success();
  }

  private UpdateProduct.Result updateCharacteristicsWithRepository(Product product,
                                                                   UpdateProduct operation)
      throws EmptyTranslationException {
    List<ProductCharacteristic> oldCharacteristics =
        productCharacteristicRepository.getAllByProductId(product.getId());
    if (!oldCharacteristics.isEmpty()) {
      productCharacteristicRepository.deleteAll(oldCharacteristics);
    }

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
      faq.setQuestion(new ProductFaqQuestion(command.question(),
          translationRepository.expand(command.questionTranslations())));
      faq.setAnswer(new ProductFaqAnswer(command.answer(),
          translationRepository.expand(command.answerTranslations())));
      newFaq.add(faq);
    }

    // Сохраняем новые FAQ через репозиторий
    productFaqRepository.saveAll(newFaq);
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
    return UpdateProduct.Result.success();
  }

  private UpdateProduct.Result updateMediaWithRepositories(Product product,
                                                           UpdateProduct operation) {
    try {
      // Получаем текущие медиа
      Set<ProductMedia> existingMedia = new HashSet<>(product.getMedia());

      // Создаем Map для быстрого доступа к старым медиа по ID
      Map<Long, ProductMedia> oldMediaMap = existingMedia.stream()
          .collect(Collectors.toMap(ProductMedia::getId, m -> m));

      // Определяем медиа для сохранения и удаления
      Set<ProductMedia> mediaToKeep = new HashSet<>();
      Set<ProductMedia> mediaToDelete = new HashSet<>(existingMedia);

      // Обрабатываем старые медиа с новыми позициями
      for (UpdateOldMediaDto oldMediaDto : operation.getOldProductMedia()) {
        ProductMedia oldMedia = oldMediaMap.get(oldMediaDto.id());
        if (oldMedia != null) {
          // Обновляем позицию для старого медиа
          oldMedia.setPosition(ProductMediaPosition.of(oldMediaDto.position()));
          mediaToKeep.add(oldMedia);
          mediaToDelete.remove(oldMedia);
        }
      }

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

      // Определяем максимальную позицию среди оставшихся медиа для новых файлов
      int maxPosition = mediaToKeep.stream()
          .mapToInt(media -> media.getPosition().getValue())
          .max()
          .orElse(-1);

      // Создаем новые медиа с переводами, начиная со следующей позиции
      List<ProductMedia> newMedia = new ArrayList<>(mediaToKeep);

      if (!operation.getProductMedia().isEmpty()) {
        List<String> urls = fileStorageRepository.uploadManyImagesToFolder(
            FileStorageFolders.PRODUCT_IMAGES.getValue(),
            operation.getProductMedia().toArray(new MultipartFile[] {}));

        for (int i = 0; i < operation.getProductMedia().size(); i++) {
          MultipartFile file = operation.getProductMedia().get(i);
          int position = maxPosition + 1 + i;
          ProductMedia media =
              createProductMediaWithTranslations(file, product, position, operation);
          media.setUrl(ProductMediaUrl.of(urls.get(i)));
          newMedia.add(media);
        }
      }

      productMediaRepository.saveAll(newMedia);

      // Обновляем preview image (берем медиа с минимальной позицией)
      Optional<ProductMedia> firstMedia = newMedia.stream()
          .min(Comparator.comparing(media -> media.getPosition().getValue()));

      if (firstMedia.isPresent()) {
        product.setPreviewImageUrl(
            ProductPreviewImageUrl.of(firstMedia.get().getUrl().toString()));
      } else {
        return UpdateProduct.Result.errorSavingProduct(new RuntimeException("Empty result files"));
      }

      return UpdateProduct.Result.success();
    } catch (Exception e) {
      log.error("Error updating media for product {}", product.getId(), e);
      return UpdateProduct.Result.errorSavingFiles(e);
    }
  }

  private UpdateProduct.Result updateVendorDetailsMedia(VendorDetails vendorDetails,
                                                        UpdateProduct operation) {
    try {
      if (operation.getProductVendorDetailsMedia().isEmpty()) {
        return UpdateProduct.Result.success();
      }

      Set<VendorMedia> existingMedia = new HashSet<>(vendorDetails.getMedia());

      // Создаем Map для быстрого доступа к старым медиа по ID
      Map<Long, VendorMedia> oldMediaMap = existingMedia.stream()
          .collect(Collectors.toMap(VendorMedia::getId, m -> m));

      // Определяем медиа для сохранения и удаления
      Set<VendorMedia> mediaToKeep = new HashSet<>();
      Set<VendorMedia> mediaToDelete = new HashSet<>(existingMedia);

      // Обрабатываем старые медиа с новыми позициями
      for (UpdateOldMediaDto oldMediaDto : operation.getOldVendorDetailsMedia()) {
        VendorMedia oldMedia = oldMediaMap.get(oldMediaDto.id());
        if (oldMedia != null) {
          // Обновляем позицию для старого медиа
          oldMedia.setPosition(VendorMediaPosition.of(oldMediaDto.position()));
          mediaToKeep.add(oldMedia);
          mediaToDelete.remove(oldMedia);
        }
      }

      // Удаляем медиа из vendor details
      vendorMediaRepository.deleteAll(mediaToDelete);

      // Определяем максимальную позицию среди оставшихся медиа для новых файлов
      int maxPosition = mediaToKeep.stream()
          .mapToInt(media -> media.getPosition().getValue())
          .max()
          .orElse(-1);

      // Добавляем новые медиа, начиная со следующей позиции
      List<String> urls = fileStorageRepository.uploadManyImagesToFolder(
          FileStorageFolders.PRODUCT_IMAGES.getValue(),
          operation.getProductVendorDetailsMedia().toArray(new MultipartFile[] {}));

      for (int i = 0; i < operation.getProductVendorDetailsMedia().size(); i++) {
        MultipartFile file = operation.getProductVendorDetailsMedia().get(i);
        int position = maxPosition + 1 + i;
        VendorMedia media = new VendorMedia();
        media.setVendorDetails(vendorDetails);
        media.setMediaType(getMediaType(file));
        media.setMimeType(VendorMediaMimeType.of(file.getContentType()));
        media.setPosition(VendorMediaPosition.of(position));
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

  // Обновленный метод создания медиа с учетом позиции
  private ProductMedia createProductMediaWithTranslations(MultipartFile file, Product product,
                                                          int position,
                                                          UpdateProduct operation)
      throws EmptyTranslationException {
    ProductMedia media = new ProductMedia();
    media.setProduct(product);
    media.setMediaType(getMediaType(file));
    media.setMimeType(ProductMediaMimeType.of(file.getContentType()));
    media.setPosition(ProductMediaPosition.of(position));

    // Устанавливаем alt text с переводами сразу
    // Используем position для определения соответствующего alt text command
    if (position < operation.getUpdateProductMediaAltTextCommands().size()) {
      UpdateProductMediaAltTextCommand command =
          operation.getUpdateProductMediaAltTextCommands().get(position);
      media.setAltText(new ProductMediaAltText(
          command.altText(),
          translationRepository.expand(command.translations())
      ));
    } else {
      String filename = file.getOriginalFilename();
      media.setAltText(new ProductMediaAltText(
          filename,
          translationRepository.expand(filename)
      ));
    }

    return media;
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