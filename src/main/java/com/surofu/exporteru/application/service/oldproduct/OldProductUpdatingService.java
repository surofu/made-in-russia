package com.surofu.exporteru.application.service.oldproduct;

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
import com.surofu.exporteru.core.repository.ProductRepository;
import com.surofu.exporteru.core.repository.TranslationRepository;
import com.surofu.exporteru.core.service.product.operation.UpdateProduct;
import com.surofu.exporteru.infrastructure.persistence.category.JpaCategoryRepository;
import com.surofu.exporteru.infrastructure.persistence.deliveryMethod.JpaDeliveryMethodRepository;
import com.surofu.exporteru.infrastructure.persistence.product.media.JpaProductMediaRepository;
import com.surofu.exporteru.infrastructure.persistence.user.JpaUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.PersistenceContext;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class OldProductUpdatingService {
  private final ProductRepository productRepository;
  private final JpaCategoryRepository categoryRepository;
  private final JpaDeliveryMethodRepository deliveryMethodRepository;
  private final TranslationRepository translationRepository;
  private final FileStorageRepository fileStorageRepository;
  private final OldProductSavingService oldProductSavingService;
  private final JpaUserRepository userRepository;
  private final JpaProductMediaRepository jpaProductMediaRepository;

  @PersistenceContext
  private EntityManager entityManager;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public UpdateProduct.Result updateProduct(UpdateProduct operation) {
    entityManager.setFlushMode(FlushModeType.COMMIT);

    try {
      // Получаем managed продукт
      Product product = productRepository.getById(operation.getProductId())
          .orElseThrow(() -> new RuntimeException("Product not found"));

      UpdateProduct.Result validationResult = validateUpdateProductOperation(operation);
      if (!(validationResult instanceof UpdateProduct.Result.Success)) {
        return validationResult;
      }

      User user = userRepository.getById(operation.getSecurityUser().getUser().getId())
          .orElseThrow(() -> new RuntimeException("User not found"));
      VendorDetails vendorDetails = user.getVendorDetails();

      Category category = categoryRepository.getById(operation.getCategoryId())
          .orElseThrow(() -> new RuntimeException("Category not found"));

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

      // Обновляем основные поля
      updateProductBasicFields(product, operation, category, deliveryMethods, similarProducts);

      // Обновляем связанные сущности
      UpdateProduct.Result relatedEntitiesResult =
          updateRelatedEntitiesWithRepositories(product, operation);
      if (relatedEntitiesResult != UpdateProduct.Result.Success.INSTANCE) {
        return relatedEntitiesResult;
      }

      // Обновляем медиа
      UpdateProduct.Result mediaResult = updateMediaWithRepositories(product, operation);
      if (mediaResult != UpdateProduct.Result.Success.INSTANCE) {
        return mediaResult;
      }

      // Обновляем медиа vendor details
      UpdateProduct.Result vendorMediaResult = updateVendorDetailsMedia(vendorDetails, operation);
      if (vendorMediaResult != UpdateProduct.Result.Success.INSTANCE) {
        return vendorMediaResult;
      }

      // Сохраняем продукт
      return oldProductSavingService.saveUpdate(product);
    } catch (EmptyTranslationException e) {
      log.error("Empty translation error", e);
      return UpdateProduct.Result.emptyTranslations(e.getMessage());
    } catch (Exception e) {
      log.error("Error updating product", e);
      return UpdateProduct.Result.success();
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  protected void updateProductBasicFields(Product product, UpdateProduct operation,
                                          Category category, List<DeliveryMethod> deliveryMethods,
                                          List<Product> similarProducts) {
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
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  protected UpdateProduct.Result updateRelatedEntitiesWithRepositories(Product product,
                                                                       UpdateProduct operation) {
    try {
      updatePrices(product, operation);
      updateCharacteristics(product, operation);
      updateFaq(product, operation);
      updateDeliveryMethodDetails(product, operation);
      updatePackagingOptions(product, operation);

      return UpdateProduct.Result.success();
    } catch (EmptyTranslationException e) {
      log.error("Empty translation error in related entities", e);
      return UpdateProduct.Result.emptyTranslations(e.getMessage());
    } catch (Exception e) {
      log.error("Error updating related entities", e);
      return UpdateProduct.Result.success();
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  protected void updatePrices(Product product, UpdateProduct operation)
      throws EmptyTranslationException {

    // Перезагружаем продукт вместе с ценами в текущей транзакции
    Product managedProduct = productRepository.findByIdWithPrices(product.getId())
        .orElseThrow(() -> new IllegalArgumentException("Product not found"));

    // Полностью очищаем коллекцию (orphanRemoval удалит их из БД)
    managedProduct.getPrices().clear();

    // Создаем новые цены
    for (UpdateProductPriceCommand command : operation.getUpdateProductPriceCommands()) {
      ProductPrice price = new ProductPrice();
      price.setProduct(managedProduct);
      price.setQuantityRange(
          ProductPriceQuantityRange.of(command.quantityFrom(), command.quantityTo()));
      price.setCurrency(ProductPriceCurrency.of(command.currency()));
      price.setUnit(new ProductPriceUnit(command.unit(), translationRepository.expand(command.unit())));
      price.setOriginalPrice(ProductPriceOriginalPrice.of(command.price()));
      price.setDiscount(ProductPriceDiscount.of(command.discount()));

      managedProduct.getPrices().add(price);
    }

    try {
      productRepository.save(managedProduct);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  protected void updateCharacteristics(Product product, UpdateProduct operation)
      throws EmptyTranslationException {

    Set<ProductCharacteristic> newCharacteristics = new HashSet<>();

    for (UpdateProductCharacteristicCommand command : operation.getUpdateProductCharacteristicCommands()) {
      ProductCharacteristic characteristic = new ProductCharacteristic();
      characteristic.setProduct(product);
      characteristic.setName(new ProductCharacteristicName(command.name(),
          translationRepository.expand(command.nameTranslations())));
      characteristic.setValue(new ProductCharacteristicValue(command.value(),
          translationRepository.expand(command.valueTranslations())));
      newCharacteristics.add(characteristic);
      product.getCharacteristics().add(characteristic);
    }

    product.getCharacteristics().removeIf(c -> !newCharacteristics.contains(c));
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  protected void updateFaq(Product product, UpdateProduct operation)
      throws EmptyTranslationException {
    // Создаем новый Set с новыми FAQ
    Set<ProductFaq> newFaq = new HashSet<>();

    for (UpdateProductFaqCommand command : operation.getUpdateProductFaqCommands()) {
      ProductFaq faq = new ProductFaq();
      faq.setProduct(product);
      faq.setQuestion(new ProductFaqQuestion(command.question(),
          translationRepository.expand(command.questionTranslations())));
      faq.setAnswer(new ProductFaqAnswer(command.answer(),
          translationRepository.expand(command.answerTranslations())));
      newFaq.add(faq);
      product.getFaq().add(faq);
    }

    // Очищаем старые и устанавливаем новые
    product.getFaq().removeIf(f -> !newFaq.contains(f));
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  protected void updateDeliveryMethodDetails(Product product, UpdateProduct operation)
      throws EmptyTranslationException {
    // Создаем новый Set с новыми деталями доставки
    Set<ProductDeliveryMethodDetails> newDetails = new HashSet<>();

    for (UpdateProductDeliveryMethodDetailsCommand command : operation.getUpdateProductDeliveryMethodDetailsCommands()) {
      ProductDeliveryMethodDetails detail = new ProductDeliveryMethodDetails();
      detail.setProduct(product);
      detail.setName(new ProductDeliveryMethodDetailsName(command.name(), translationRepository.expand(command.nameTranslations())));
      detail.setValue(new ProductDeliveryMethodDetailsValue(command.value(), translationRepository.expand(command.valueTranslations())));
      newDetails.add(detail);
      product.getDeliveryMethodDetails().add(detail);
    }

    // Очищаем старые и устанавливаем новые
    product.getDeliveryMethodDetails().removeIf(d -> !newDetails.contains(d));
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  protected void updatePackagingOptions(Product product, UpdateProduct operation)
      throws EmptyTranslationException {
    // Создаем новый Set с новыми опциями упаковки
    Set<ProductPackageOption> newOptions = new HashSet<>();

    for (UpdateProductPackageOptionCommand command : operation.getUpdateProductPackageOptionCommands()) {
      ProductPackageOption option = new ProductPackageOption();
      option.setProduct(product);
      option.setName(new ProductPackageOptionName(command.name(), translationRepository.expand(command.nameTranslations())));
      option.setPrice(ProductPackageOptionPrice.of(command.price()));
      option.setPriceUnit(ProductPackageOptionPriceUnit.of(command.priceUnit()));
      newOptions.add(option);
      product.getPackageOptions().add(option);
    }

    // Очищаем старые и устанавливаем новые
    product.getPackageOptions().removeIf(o -> !newOptions.contains(o));
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  protected UpdateProduct.Result updateMediaWithRepositories(Product product,
                                                             UpdateProduct operation) {
    try {
      // Перезагружаем продукт в текущей транзакции
      Product managedProduct = productRepository.getById(product.getId())
          .orElseThrow(() -> new IllegalArgumentException("Product not found"));

      Set<Long> mediaIdsToKeep = operation.getOldProductMedia().stream()
          .map(UpdateOldMediaDto::id)
          .collect(Collectors.toSet());

      // Обновляем позиции существующих медиа и собираем ID для удаления
      List<Long> mediaIdsToRemove = new ArrayList<>();
      for (ProductMedia media : managedProduct.getMedia()) {
        if (mediaIdsToKeep.contains(media.getId())) {
          operation.getOldProductMedia().stream()
              .filter(dto -> Objects.equals(dto.id(), media.getId()))
              .findFirst()
              .ifPresent(dto -> media.setPosition(ProductMediaPosition.of(dto.position())));
        } else {
          mediaIdsToRemove.add(media.getId());
        }
      }

      // Удаляем медиа через репозиторий (batch delete)
      if (!mediaIdsToRemove.isEmpty()) {
        managedProduct.getMedia().removeIf(media -> mediaIdsToRemove.contains(media.getId()));
      }

      // Загружаем новые файлы
      if (!operation.getProductMedia().isEmpty()) {
        List<String> imagesUrls = fileStorageRepository.uploadManyImagesToFolder(
            FileStorageFolders.PRODUCT_IMAGES.getValue(),
            operation.getProductMedia()
                .stream()
                .filter(f -> Objects.requireNonNull(f.getContentType()).startsWith("image/"))
                .toList()
                .toArray(new MultipartFile[] {})
        );
        List<String> videosUrls = fileStorageRepository.uploadManyImagesToFolder(
            FileStorageFolders.PRODUCT_VIDEOS.getValue(),
            operation.getProductMedia()
                .stream()
                .filter(f -> Objects.requireNonNull(f.getContentType()).startsWith("video/"))
                .toList()
                .toArray(new MultipartFile[] {})
        );
        List<String> urls = new ArrayList<>(imagesUrls);
        urls.addAll(videosUrls);

        // Получаем текущие позиции для определения свободных мест
        Set<Integer> existingPositions = managedProduct.getMedia().stream()
            .map(media -> media.getPosition().getValue())
            .collect(Collectors.toSet());

        // Находим все возможные позиции
        int maxPosition = managedProduct.getMedia().stream()
            .mapToInt(media -> media.getPosition().getValue())
            .max()
            .orElse(-1);
        int totalPositions = Math.max(maxPosition + 1, managedProduct.getMedia().size()) +
            operation.getProductMedia().size();

        // Добавляем новые медиа в свободные позиции
        int newMediaIndex = 0;
        for (int position = 0;
             position < totalPositions && newMediaIndex < operation.getProductMedia().size();
             position++) {
          if (!existingPositions.contains(position)) {
            MultipartFile file = operation.getProductMedia().get(newMediaIndex);
            ProductMedia media = createProductMediaWithTranslations(
                file, managedProduct, position, newMediaIndex, operation);
            media.setUrl(ProductMediaUrl.of(urls.get(newMediaIndex)));
            managedProduct.getMedia().add(media);
            newMediaIndex++;
          }
        }
      }

      // Сохраняем изменения
      try {
        productRepository.save(managedProduct);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return UpdateProduct.Result.success();
    }

    return UpdateProduct.Result.success();
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  protected UpdateProduct.Result updateVendorDetailsMedia(VendorDetails vendorDetails,
                                                          UpdateProduct operation) {
    try {
      // Обновляем позиции существующих медиа
      Set<Long> mediaIdsToKeep = operation.getOldVendorDetailsMedia().stream()
          .map(UpdateOldMediaDto::id)
          .collect(Collectors.toSet());

      // Создаем копию для безопасного удаления
      List<VendorMedia> mediaToRemove = new ArrayList<>();
      for (VendorMedia media : vendorDetails.getMedia()) {
        if (mediaIdsToKeep.contains(media.getId())) {
          // Обновляем позицию существующего медиа
          operation.getOldVendorDetailsMedia().stream()
              .filter(dto -> Objects.equals(dto.id(), media.getId()))
              .findFirst()
              .ifPresent(dto -> media.setPosition(VendorMediaPosition.of(dto.position())));
        } else {
          mediaToRemove.add(media);
        }
      }
      // Удаляем медиа, которые больше не нужны
      mediaToRemove.forEach(vendorDetails.getMedia()::remove);

      // Загружаем новые файлы
      if (!operation.getProductVendorDetailsMedia().isEmpty()) {
        List<String> imagesUrls = fileStorageRepository.uploadManyImagesToFolder(
            FileStorageFolders.VENDOR_IMAGES.getValue(),
            operation.getProductVendorDetailsMedia()
                .stream()
                .filter(f -> Objects.requireNonNull(f.getContentType()).startsWith("image/"))
                .toList()
                .toArray(new MultipartFile[] {})
        );
        List<String> videosUrls = fileStorageRepository.uploadManyImagesToFolder(
            FileStorageFolders.VENDOR_VIDEOS.getValue(),
            operation.getProductVendorDetailsMedia()
                .stream()
                .filter(f -> Objects.requireNonNull(f.getContentType()).startsWith("video/"))
                .toList()
                .toArray(new MultipartFile[] {})
        );
        List<String> urls = new ArrayList<>(imagesUrls);
        urls.addAll(videosUrls);

        // Получаем текущие позиции для определения свободных мест
        Set<Integer> existingPositions = vendorDetails.getMedia().stream()
            .map(media -> media.getPosition().getValue())
            .collect(Collectors.toSet());

        // Находим все возможные позиции (от 0 до максимальной + количество новых)
        int maxPosition = vendorDetails.getMedia().stream()
            .mapToInt(media -> media.getPosition().getValue())
            .max()
            .orElse(-1);
        int totalPositions = Math.max(maxPosition + 1, vendorDetails.getMedia().size()) +
            operation.getProductVendorDetailsMedia().size();

        // Добавляем новые медиа в свободные позиции или в конец
        int newMediaIndex = 0;
        for (int position = 0; position < totalPositions &&
            newMediaIndex < operation.getProductVendorDetailsMedia().size(); position++) {
          if (!existingPositions.contains(position)) {
            // Нашли свободную позицию - добавляем новое медиа
            MultipartFile file = operation.getProductVendorDetailsMedia().get(newMediaIndex);
            VendorMedia media = new VendorMedia();
            media.setVendorDetails(vendorDetails);
            media.setMediaType(getMediaType(file));
            media.setMimeType(VendorMediaMimeType.of(file.getContentType()));
            media.setPosition(VendorMediaPosition.of(position));
            media.setUrl(VendorMediaUrl.of(urls.get(newMediaIndex)));
            vendorDetails.getMedia().add(media);
            newMediaIndex++;
          }
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return UpdateProduct.Result.success();
    }

    return UpdateProduct.Result.success();
  }

  private ProductMedia createProductMediaWithTranslations(MultipartFile file, Product product,
                                                          int position, int fileIndex,
                                                          UpdateProduct operation)
      throws EmptyTranslationException {
    ProductMedia media = new ProductMedia();
    media.setProduct(product);
    media.setMediaType(getMediaType(file));
    media.setMimeType(ProductMediaMimeType.of(file.getContentType()));
    media.setPosition(ProductMediaPosition.of(position));

    if (fileIndex < operation.getUpdateProductMediaAltTextCommands().size()) {
      UpdateProductMediaAltTextCommand command =
          operation.getUpdateProductMediaAltTextCommands().get(fileIndex);
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