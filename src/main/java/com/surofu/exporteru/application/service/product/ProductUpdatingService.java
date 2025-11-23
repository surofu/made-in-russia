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
import com.surofu.exporteru.core.model.product.ProductPreviewImageUrl;
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
import com.surofu.exporteru.core.model.vendorDetails.VendorDetails;
import com.surofu.exporteru.core.model.vendorDetails.media.VendorMedia;
import com.surofu.exporteru.core.model.vendorDetails.media.VendorMediaMimeType;
import com.surofu.exporteru.core.model.vendorDetails.media.VendorMediaUrl;
import com.surofu.exporteru.core.repository.FileStorageRepository;
import com.surofu.exporteru.core.repository.ProductRepository;
import com.surofu.exporteru.core.repository.TranslationRepository;
import com.surofu.exporteru.core.service.product.operation.CreateProduct;
import com.surofu.exporteru.core.service.product.operation.UpdateProduct;
import com.surofu.exporteru.infrastructure.persistence.category.JpaCategoryRepository;
import com.surofu.exporteru.infrastructure.persistence.deliveryMethod.JpaDeliveryMethodRepository;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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
public class ProductUpdatingService {
  private final ProductRepository productRepository;
  private final JpaCategoryRepository categoryRepository;
  private final JpaDeliveryMethodRepository deliveryMethodRepository;
  private final TranslationRepository translationRepository;
  private final FileStorageRepository fileStorageRepository;
  private final ProductSavingService productSavingService;

  public UpdateProduct.Result updateProduct(UpdateProduct operation) {
    // ---------- Validation ---------- //
    Optional<Product> productOptional = productRepository.getProductById(operation.getProductId());

    if (productOptional.isEmpty()) {
      return UpdateProduct.Result.productNotFound(operation.getProductId());
    }

    Product product = productOptional.get();

    UpdateProduct.Result validationResult = validateUpdateProductOperation(operation);

    if (!(validationResult instanceof UpdateProduct.Result.Success)) {
      return validationResult;
    }

    // ---------- Loading dependencies ---------- //
    // Category
    Optional<Category> categoryOptional = categoryRepository.getById(operation.getCategoryId());

    if (categoryOptional.isEmpty()) {
      return UpdateProduct.Result.categoryNotFound(operation.getCategoryId());
    }

    Category category = categoryOptional.get();

    // Delivery Method
    Optional<Long> firstNotExistsDeliveryMethodIdOptional =
        deliveryMethodRepository.firstNotExists(operation.getDeliveryMethodIds());

    if (firstNotExistsDeliveryMethodIdOptional.isPresent()) {
      return UpdateProduct.Result.deliveryMethodNotFound(
          firstNotExistsDeliveryMethodIdOptional.get());
    }

    List<DeliveryMethod> deliveryMethods =
        deliveryMethodRepository.getAllDeliveryMethodsByIds(operation.getDeliveryMethodIds());

    // Similar product
    Optional<Long> firstNotExistsSimilarProductIdOptional =
        productRepository.firstNotExists(operation.getSimilarProductIds());

    if (firstNotExistsSimilarProductIdOptional.isPresent()) {
      return UpdateProduct.Result.similarProductNotFound(
          firstNotExistsSimilarProductIdOptional.get());
    }

    List<Product> similarProducts =
        productRepository.findAllByIds(operation.getSimilarProductIds());

    // ---------- Setting new product ---------- //
    product.setApproveStatus(ApproveStatus.PENDING);
    product.setCategory(category);
    product.setDeliveryMethods(new HashSet<>(deliveryMethods));
    product.setSimilarProducts(new HashSet<>(similarProducts));
    product.setTitle(operation.getProductTitle());
    product.setDescription(operation.getProductDescription());
    product.setMinimumOrderQuantity(operation.getMinimumOrderQuantity());
    product.setDiscountExpirationDate(operation.getDiscountExpirationDate());

    try {
      product.getTitle().setTranslations(
          translationRepository.expand(operation.getProductTitle().getTranslations()));
      product.getDescription().setMainDescriptionTranslations(translationRepository.expand(
          operation.getProductDescription().getMainDescriptionTranslations()));
      product.getDescription().setMainDescriptionTranslations(translationRepository.expand(
          operation.getProductDescription().getFurtherDescriptionTranslations()));
      settingPrices(product, operation);
      settingCharacteristics(product, operation);
      settingFaq(product, operation);
      settingDeliveryMethodDetails(product, operation);
      settingPackagingOption(product, operation);
      settingMedia(product, operation);
      settingVendorDetailsMedia(operation.getSecurityUser().getUser().getVendorDetails(),
          operation);
    } catch (EmptyTranslationException e) {
      return UpdateProduct.Result.emptyTranslations(e.getMessage());
    } catch (Exception e) {
      return UpdateProduct.Result.translationError(e);
    }

    return productSavingService.saveUpdate(product);
  }

  // Setting
  private void settingPrices(Product product, UpdateProduct operation) throws
      EmptyTranslationException, IOException {
    Set<ProductPrice> prices = new HashSet<>();

    List<UpdateProductPriceCommand> updateProductPriceCommands =
        operation.getUpdateProductPriceCommands();
    for (UpdateProductPriceCommand command : updateProductPriceCommands) {
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
              command.price().multiply(command.discount())
                  .divide(BigDecimal.valueOf(100), RoundingMode.DOWN)
          )
      );
      price.getUnit().setTranslations(translationRepository.expand(command.unit()));
      prices.add(price);
    }

    product.getPrices().clear();
    product.getPrices().addAll(prices);
  }

  private void settingCharacteristics(Product product, UpdateProduct operation) throws
      EmptyTranslationException, IOException {
    Set<ProductCharacteristic> characteristics = new HashSet<>();

    for (UpdateProductCharacteristicCommand command : operation.getUpdateProductCharacteristicCommands()) {
      ProductCharacteristic characteristic = new ProductCharacteristic();
      characteristic.setProduct(product);
      characteristic.setName(ProductCharacteristicName.of(command.name()));
      characteristic.setValue(ProductCharacteristicValue.of(command.value()));
      characteristic.getName()
          .setTranslations(translationRepository.expand(command.nameTranslations()));
      characteristic.getValue()
          .setTranslations(translationRepository.expand(command.valueTranslations()));
      characteristics.add(characteristic);
    }

    product.getCharacteristics().clear();
    product.getCharacteristics().addAll(characteristics);
  }

  private void settingFaq(Product product, UpdateProduct operation) throws
      EmptyTranslationException, IOException {
    Set<ProductFaq> faqs = new HashSet<>();

    for (UpdateProductFaqCommand command : operation.getUpdateProductFaqCommands()) {
      ProductFaq faq = new ProductFaq();
      faq.setProduct(product);
      faq.setQuestion(ProductFaqQuestion.of(command.question()));
      faq.setAnswer(ProductFaqAnswer.of(command.answer()));
      faq.getQuestion()
          .setTranslations(translationRepository.expand(command.questionTranslations()));
      faq.getAnswer().setTranslations(translationRepository.expand(command.answerTranslations()));
      faqs.add(faq);
    }

    product.getFaq().clear();
    product.getFaq().addAll(faqs);
  }

  private void settingDeliveryMethodDetails(Product product, UpdateProduct operation) throws
      EmptyTranslationException, IOException {
    Set<ProductDeliveryMethodDetails> details = new HashSet<>();

    for (UpdateProductDeliveryMethodDetailsCommand command : operation.getUpdateProductDeliveryMethodDetailsCommands()) {
      ProductDeliveryMethodDetails detail = new ProductDeliveryMethodDetails();
      detail.setProduct(product);
      detail.setName(ProductDeliveryMethodDetailsName.of(command.name()));
      detail.setValue(ProductDeliveryMethodDetailsValue.of(command.value()));
      detail.getName().setTranslations(translationRepository.expand(command.nameTranslations()));
      detail.getValue().setTranslations(translationRepository.expand(command.valueTranslations()));
      details.add(detail);
    }

    product.getDeliveryMethodDetails().clear();
    product.getDeliveryMethodDetails().addAll(details);
  }

  private void settingPackagingOption(Product product, UpdateProduct operation) throws
      EmptyTranslationException, IOException {
    Set<ProductPackageOption> options = new HashSet<>();

    for (UpdateProductPackageOptionCommand command : operation.getUpdateProductPackageOptionCommands()) {
      ProductPackageOption option = new ProductPackageOption();
      option.setProduct(product);
      option.setName(ProductPackageOptionName.of(command.name()));
      option.setPrice(ProductPackageOptionPrice.of(command.price()));
      option.setPriceUnit(ProductPackageOptionPriceUnit.of(command.priceUnit()));
      option.getName().setTranslations(translationRepository.expand(command.nameTranslations()));
      options.add(option);
    }

    product.getPackageOptions().clear();
    product.getPackageOptions().addAll(options);
  }

  private void settingMedia(Product product, UpdateProduct operation) throws Exception {
    List<String> urls =
        fileStorageRepository.uploadManyImagesToFolder(FileStorageFolders.PRODUCT_IMAGES.getValue(),
            operation.getProductMedia().toArray(new MultipartFile[] {}));

    int totalMediaCount =
        operation.getOldProductMedia().size() + operation.getProductMedia().size();
    List<ProductMedia> result = new ArrayList<>();

    int newMediaIndex = 0;
    for (int i = 0; i < totalMediaCount; i++) {
      if (i < operation.getOldProductMedia().size()) {
        UpdateOldMediaDto updateOldMediaDto = operation.getOldProductMedia().get(i);

        if (Objects.equals(i, updateOldMediaDto.position())) {
          Optional<ProductMedia> oldProductMediaOptional = product.getMedia().stream()
              .filter(m -> Objects.equals(m.getId(), updateOldMediaDto.id())).findFirst();
          oldProductMediaOptional.ifPresent(result::add);
        } else if (newMediaIndex < operation.getProductMedia().size()) {
          MultipartFile file = operation.getProductMedia().get(newMediaIndex);
          ProductMedia media = new ProductMedia();
          media.setProduct(product);
          media.setMediaType(getMediaType(file));
          media.setMimeType(ProductMediaMimeType.of(file.getContentType()));
          media.setUrl(ProductMediaUrl.of(urls.get(i)));

          if (newMediaIndex < operation.getUpdateProductMediaAltTextCommands().size()) {
            UpdateProductMediaAltTextCommand command =
                operation.getUpdateProductMediaAltTextCommands().get(i);
            media.setAltText(ProductMediaAltText.of(command.altText()));
            media.getAltText()
                .setTranslations(translationRepository.expand(command.translations()));
          }

          result.add(media);
        }
      }
    }

    List<String> mediaUrlsToDelete = product.getMedia().stream()
        .filter(m -> operation.getOldProductMedia().stream()
            .noneMatch(dto -> Objects.equals(dto.id(), m.getId())))
        .map(ProductMedia::getUrl)
        .map(ProductMediaUrl::toString)
        .toList();
    fileStorageRepository.deleteMediaByLink(mediaUrlsToDelete.toArray(new String[0]));

    if (result.isEmpty()) {
      throw new RuntimeException("Empty result files");
    }

    product.setPreviewImageUrl(ProductPreviewImageUrl.of(result.iterator().next().getUrl().toString()));
    product.getMedia().clear();
    product.getMedia().addAll(result);
  }

  private void settingVendorDetailsMedia(VendorDetails vendorDetails, UpdateProduct operation)
      throws Exception {
    List<String> urls =
        fileStorageRepository.uploadManyImagesToFolder(FileStorageFolders.PRODUCT_IMAGES.getValue(),
            operation.getProductVendorDetailsMedia().toArray(new MultipartFile[] {}));

    int totalMediaCount =
        operation.getOldVendorDetailsMedia().size() + operation.getProductVendorDetailsMedia().size();
    List<VendorMedia> result = new ArrayList<>();

    int newMediaIndex = 0;
    for (int i = 0; i < totalMediaCount; i++) {
      if (i < operation.getOldVendorDetailsMedia().size()) {
        UpdateOldMediaDto updateOldMediaDto = operation.getOldVendorDetailsMedia().get(i);

        if (Objects.equals(i, updateOldMediaDto.position())) {
          Optional<VendorMedia> oldProductMediaOptional = vendorDetails.getMedia().stream()
              .filter(m -> Objects.equals(m.getId(), updateOldMediaDto.id())).findFirst();
          oldProductMediaOptional.ifPresent(result::add);
        } else if (newMediaIndex < operation.getProductMedia().size()) {
          MultipartFile file = operation.getProductMedia().get(newMediaIndex);
          VendorMedia media = new VendorMedia();
          media.setVendorDetails(vendorDetails);
          media.setMediaType(getMediaType(file));
          media.setMimeType(VendorMediaMimeType.of(file.getContentType()));
          media.setUrl(VendorMediaUrl.of(urls.get(i)));
          result.add(media);
        }
      }
    }

    List<String> mediaUrlsToDelete = vendorDetails.getMedia().stream()
        .filter(m -> operation.getOldProductMedia().stream()
            .noneMatch(dto -> Objects.equals(dto.id(), m.getId())))
        .map(VendorMedia::getUrl)
        .map(VendorMediaUrl::toString)
        .toList();
    fileStorageRepository.deleteMediaByLink(mediaUrlsToDelete.toArray(new String[0]));

    vendorDetails.getMedia().clear();
    vendorDetails.getMedia().addAll(result);
  }

  // Validation
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
    if (Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
      return MediaType.IMAGE;
    }

    if (Objects.requireNonNull(file.getContentType()).startsWith("video/")) {
      return MediaType.VIDEO;
    }

    throw new IllegalArgumentException("Unsupported content type: " + file.getContentType());
  }
}
