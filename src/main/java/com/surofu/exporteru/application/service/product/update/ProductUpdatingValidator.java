package com.surofu.exporteru.application.service.product.update;

import com.surofu.exporteru.application.command.product.update.UpdateProductCharacteristicCommand;
import com.surofu.exporteru.application.command.product.update.UpdateProductDeliveryMethodDetailsCommand;
import com.surofu.exporteru.application.command.product.update.UpdateProductFaqCommand;
import com.surofu.exporteru.application.command.product.update.UpdateProductPackageOptionCommand;
import com.surofu.exporteru.application.command.product.update.UpdateProductPriceCommand;
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
import com.surofu.exporteru.core.repository.DeliveryTermRepository;
import com.surofu.exporteru.core.repository.ProductRepository;
import com.surofu.exporteru.core.service.product.operation.UpdateProduct;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductUpdatingValidator {
  private static final Set<String> SUPPORTED_IMAGE_FORMATS =
      Set.of("image/jpg", "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg");
  private static final Set<String> SUPPORTED_VIDEO_FORMATS =
      Set.of("video/mp4", "video/avi", "video/mov", "video/mkv", "video/webm");
  private final CategoryRepository categoryRepository;
  private final DeliveryMethodRepository deliveryMethodRepository;
  private final DeliveryTermRepository deliveryTermRepository;
  private final ProductRepository productRepository;

  public UpdateProduct.Result validate(UpdateProduct operation) {
    if (productRepository.existsById(operation.getProductId())) {
      return UpdateProduct.Result.productNotFound(operation.getProductId());
    }
    if (!productRepository.existsWithUserId(
        operation.getProductId(),
        operation.getSecurityUser().getUser().getId())
    ) {
      return UpdateProduct.Result.invalidOwner(operation.getProductId(),
          operation.getSecurityUser().getUser().getLogin());
    }
    if (!categoryRepository.existsById(operation.getCategoryId())) {
      return UpdateProduct.Result.categoryNotFound(operation.getCategoryId());
    }
    Optional<Long> notExistDeliveryMethod =
        deliveryMethodRepository.firstNotExists(operation.getDeliveryMethodIds());
    if (notExistDeliveryMethod.isPresent()) {
      return UpdateProduct.Result.deliveryMethodNotFound(notExistDeliveryMethod.get());
    }
    Optional<Long> notExistDeliveryTerm =
        deliveryTermRepository.firstNotExists(operation.getDeliveryTermIds());
    if (notExistDeliveryTerm.isPresent()) {
      return UpdateProduct.Result.deliveryTermNotFound(notExistDeliveryTerm.get());
    }
    Optional<Long> notExistSimilarProduct =
        productRepository.firstNotExists(operation.getSimilarProductIds());
    if (notExistSimilarProduct.isPresent()) {
      return UpdateProduct.Result.similarProductNotFound(notExistSimilarProduct.get());
    }

    for (MultipartFile file : operation.getProductMedia()) {
      if (file.getContentType() == null) {
        return UpdateProduct.Result.invalidMediaType(file.getContentType());
      }

      if (!SUPPORTED_IMAGE_FORMATS.contains(file.getContentType()) &&
          !SUPPORTED_VIDEO_FORMATS.contains(file.getContentType())) {
        return UpdateProduct.Result.invalidMediaType(file.getContentType());
      }

      if (file.isEmpty()) {
        return UpdateProduct.Result.emptyFile();
      }
    }

    for (MultipartFile file : operation.getVendorMedia()) {
      if (file.getContentType() == null) {
        return UpdateProduct.Result.invalidMediaType(file.getContentType());
      }

      if (!SUPPORTED_IMAGE_FORMATS.contains(file.getContentType()) &&
          !SUPPORTED_VIDEO_FORMATS.contains(file.getContentType())) {
        return UpdateProduct.Result.invalidMediaType(file.getContentType());
      }

      if (file.isEmpty()) {
        return UpdateProduct.Result.emptyFile();
      }
    }

    for (int i = 0; i < operation.getUpdateProductCharacteristicCommands().size(); i++) {
      UpdateProductCharacteristicCommand command =
          operation.getUpdateProductCharacteristicCommands().get(i);
      ProductCharacteristic characteristic = new ProductCharacteristic();
      characteristic.setName(new ProductCharacteristicName(command.name(), new HashMap<>()));
      characteristic.setValue(new ProductCharacteristicValue(command.value(), new HashMap<>()));
    }

    for (int i = 0; i < operation.getUpdateProductDeliveryMethodDetailsCommands().size(); i++) {
      UpdateProductDeliveryMethodDetailsCommand command =
          operation.getUpdateProductDeliveryMethodDetailsCommands().get(i);
      ProductDeliveryMethodDetails deliveryMethodDetails = new ProductDeliveryMethodDetails();
      deliveryMethodDetails.setName(
          new ProductDeliveryMethodDetailsName(command.name(), new HashMap<>()));
      deliveryMethodDetails.setValue(
          new ProductDeliveryMethodDetailsValue(command.value(), new HashMap<>()));
    }

    for (int i = 0; i < operation.getUpdateProductFaqCommands().size(); i++) {
      UpdateProductFaqCommand command = operation.getUpdateProductFaqCommands().get(i);
      ProductFaq faq = new ProductFaq();
      faq.setQuestion(new ProductFaqQuestion(command.question(), new HashMap<>()));
      faq.setAnswer(new ProductFaqAnswer(command.answer(), new HashMap<>()));
    }

    for (int i = 0; i < operation.getUpdateProductPackageOptionCommands().size(); i++) {
      UpdateProductPackageOptionCommand command =
          operation.getUpdateProductPackageOptionCommands().get(i);
      ProductPackageOption productPackageOption = new ProductPackageOption();
      productPackageOption.setName(new ProductPackageOptionName(command.name(), new HashMap<>()));
      productPackageOption.setPrice(ProductPackageOptionPrice.of(command.price()));
      productPackageOption.setPriceUnit(ProductPackageOptionPriceUnit.of(command.priceUnit()));
    }

    for (int i = 0; i < operation.getUpdateProductPriceCommands().size(); i++) {
      UpdateProductPriceCommand command = operation.getUpdateProductPriceCommands().get(i);
      ProductPrice price = new ProductPrice();
      price.setOriginalPrice(ProductPriceOriginalPrice.of(command.price()));
      price.setDiscount(ProductPriceDiscount.of(command.discount()));
      price.setCurrency(ProductPriceCurrency.of(command.currency()));
      price.setUnit(new ProductPriceUnit(command.unit(), new HashMap<>()));
      price.setQuantityRange(
          ProductPriceQuantityRange.of(command.quantityFrom(), command.quantityTo()));
    }

    return UpdateProduct.Result.success();
  }
}
