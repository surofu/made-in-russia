package com.surofu.exporteru.application.service.product.create;

import com.surofu.exporteru.application.command.product.create.CreateProductCharacteristicCommand;
import com.surofu.exporteru.application.command.product.create.CreateProductDeliveryMethodDetailsCommand;
import com.surofu.exporteru.application.command.product.create.CreateProductFaqCommand;
import com.surofu.exporteru.application.command.product.create.CreateProductPackageOptionCommand;
import com.surofu.exporteru.application.command.product.create.CreateProductPriceCommand;
import com.surofu.exporteru.core.model.product.characteristic.ProductCharacteristic;
import com.surofu.exporteru.core.model.product.characteristic.ProductCharacteristicName;
import com.surofu.exporteru.core.model.product.characteristic.ProductCharacteristicValue;
import com.surofu.exporteru.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetails;
import com.surofu.exporteru.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetailsName;
import com.surofu.exporteru.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetailsValue;
import com.surofu.exporteru.core.model.product.faq.ProductFaq;
import com.surofu.exporteru.core.model.product.faq.ProductFaqAnswer;
import com.surofu.exporteru.core.model.product.faq.ProductFaqQuestion;
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
import com.surofu.exporteru.core.repository.CategoryRepository;
import com.surofu.exporteru.core.repository.DeliveryMethodRepository;
import com.surofu.exporteru.core.repository.ProductRepository;
import com.surofu.exporteru.core.service.product.operation.CreateProduct;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductCreatingValidator {
  private static final Set<String> SUPPORTED_IMAGE_FORMATS =
      Set.of("image/jpg", "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg");
  private static final Set<String> SUPPORTED_VIDEO_FORMATS =
      Set.of("video/mp4", "video/avi", "video/mov", "video/mkv", "video/webm");
  private final CategoryRepository categoryRepository;
  private final DeliveryMethodRepository deliveryMethodRepository;
  private final ProductRepository productRepository;

  @Transactional(readOnly = true)
  public CreateProduct.Result validate(CreateProduct operation) {
    if (!categoryRepository.existsById(operation.getCategoryId())) {
      return CreateProduct.Result.categoryNotFound(operation.getCategoryId());
    }

    Optional<Long> notExistDeliveryMethod =
        deliveryMethodRepository.firstNotExists(operation.getDeliveryMethodIds());
    if (notExistDeliveryMethod.isPresent()) {
      return CreateProduct.Result.deliveryMethodNotFound(notExistDeliveryMethod.get());
    }

    Optional<Long> notExistSimilarProduct =
        productRepository.firstNotExists(operation.getSimilarProductIds());
    if (notExistSimilarProduct.isPresent()) {
      return CreateProduct.Result.similarProductNotFound(notExistSimilarProduct.get());
    }

    for (MultipartFile file : operation.getProductMedia()) {
      if (file.getContentType() == null) {
        return CreateProduct.Result.invalidMediaType(file.getContentType());
      }

      if (!SUPPORTED_IMAGE_FORMATS.contains(file.getContentType()) &&
          !SUPPORTED_VIDEO_FORMATS.contains(file.getContentType())) {
        return CreateProduct.Result.invalidMediaType(file.getContentType());
      }

      if (file.isEmpty()) {
        return CreateProduct.Result.emptyFile();
      }
    }

    for (MultipartFile file : operation.getVendorMedia()) {
      if (file.getContentType() == null) {
        return CreateProduct.Result.invalidMediaType(file.getContentType());
      }

      if (!SUPPORTED_IMAGE_FORMATS.contains(file.getContentType()) &&
          !SUPPORTED_VIDEO_FORMATS.contains(file.getContentType())) {
        return CreateProduct.Result.invalidMediaType(file.getContentType());
      }

      if (file.isEmpty()) {
        return CreateProduct.Result.emptyFile();
      }
    }

    for (int i = 0; i < operation.getCreateProductCharacteristicCommands().size(); i++) {
      CreateProductCharacteristicCommand command =
          operation.getCreateProductCharacteristicCommands().get(i);
      ProductCharacteristic characteristic = new ProductCharacteristic();
      characteristic.setName(new ProductCharacteristicName(command.name(), new HashMap<>()));
      characteristic.setValue(new ProductCharacteristicValue(command.value(), new HashMap<>()));
    }

    for (int i = 0; i < operation.getCreateProductDeliveryMethodDetailsCommands().size(); i++) {
      CreateProductDeliveryMethodDetailsCommand command =
          operation.getCreateProductDeliveryMethodDetailsCommands().get(i);
      ProductDeliveryMethodDetails deliveryMethodDetails = new ProductDeliveryMethodDetails();
      deliveryMethodDetails.setName(
          new ProductDeliveryMethodDetailsName(command.name(), new HashMap<>()));
      deliveryMethodDetails.setValue(
          new ProductDeliveryMethodDetailsValue(command.value(), new HashMap<>()));
    }

    for (int i = 0; i < operation.getCreateProductFaqCommands().size(); i++) {
      CreateProductFaqCommand command = operation.getCreateProductFaqCommands().get(i);
      ProductFaq faq = new ProductFaq();
      faq.setQuestion(new ProductFaqQuestion(command.question(), new HashMap<>()));
      faq.setAnswer(new ProductFaqAnswer(command.answer(), new HashMap<>()));
    }

    for (int i = 0; i < operation.getCreateProductPackageOptionCommands().size(); i++) {
      CreateProductPackageOptionCommand command =
          operation.getCreateProductPackageOptionCommands().get(i);
      ProductPackageOption productPackageOption = new ProductPackageOption();
      productPackageOption.setName(new ProductPackageOptionName(command.name(), new HashMap<>()));
      productPackageOption.setPrice(ProductPackageOptionPrice.of(command.price()));
      productPackageOption.setPriceUnit(ProductPackageOptionPriceUnit.of(command.priceUnit()));
    }

    for (int i = 0; i < operation.getCreateProductPriceCommands().size(); i++) {
      CreateProductPriceCommand command = operation.getCreateProductPriceCommands().get(i);
      ProductPrice price = new ProductPrice();
      price.setOriginalPrice(ProductPriceOriginalPrice.of(command.price()));
      price.setDiscount(ProductPriceDiscount.of(command.discount()));
      price.setCurrency(ProductPriceCurrency.of(command.currency()));
      price.setUnit(new ProductPriceUnit(command.unit(), new HashMap<>()));
      price.setQuantityRange(
          ProductPriceQuantityRange.of(command.quantityFrom(), command.quantityTo()));
    }

    return CreateProduct.Result.success();
  }
}
