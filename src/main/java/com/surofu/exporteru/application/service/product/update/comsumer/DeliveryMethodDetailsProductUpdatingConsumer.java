package com.surofu.exporteru.application.service.product.update.comsumer;

import com.surofu.exporteru.application.command.product.update.UpdateProductDeliveryMethodDetailsCommand;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetails;
import com.surofu.exporteru.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetailsName;
import com.surofu.exporteru.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetailsValue;
import com.surofu.exporteru.core.repository.ProductDeliveryMethodDetailsRepository;
import com.surofu.exporteru.core.repository.TranslationRepository;
import com.surofu.exporteru.core.service.product.operation.UpdateProduct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryMethodDetailsProductUpdatingConsumer
    implements ProductUpdatingConsumer {
  private final ProductDeliveryMethodDetailsRepository deliveryMethodDetailsRepository;
  private final TranslationRepository translationRepository;

  @Override
  @Transactional
  public void accept(Product product, UpdateProduct operation) {
    try {
      List<ProductDeliveryMethodDetails> newDeliveryMethods = new ArrayList<>();
      List<ProductDeliveryMethodDetails> oldDetails = new ArrayList<>();

      for (UpdateProductDeliveryMethodDetailsCommand command : operation.getUpdateProductDeliveryMethodDetailsCommands()) {
        ProductDeliveryMethodDetails details = new ProductDeliveryMethodDetails();
        details.setProduct(product);
        details.setName(new ProductDeliveryMethodDetailsName(command.name(), new HashMap<>()));
        details.setValue(new ProductDeliveryMethodDetailsValue(command.value(), new HashMap<>()));

        if (!product.getDeliveryMethodDetails().contains(details)) {
          newDeliveryMethods.add(details);
        } else {
          oldDetails.add(details);
        }
      }

      List<String> namesToTranslate = newDeliveryMethods.stream()
          .map(ProductDeliveryMethodDetails::getName)
          .map(ProductDeliveryMethodDetailsName::getValue)
          .toList();
      List<String> valuesToTranslate = newDeliveryMethods.stream()
          .map(ProductDeliveryMethodDetails::getValue)
          .map(ProductDeliveryMethodDetailsValue::getValue)
          .toList();
      List<String> textsToTranslate =
          new ArrayList<>(namesToTranslate.size() + valuesToTranslate.size());
      textsToTranslate.addAll(namesToTranslate);
      textsToTranslate.addAll(valuesToTranslate);

      List<Map<String, String>> translatedTexts = translationRepository.expand(textsToTranslate);
      for (int i = 0; i < newDeliveryMethods.size(); i++) {
        ProductDeliveryMethodDetails details = newDeliveryMethods.get(i);
        details.setName(new ProductDeliveryMethodDetailsName(details.getName().getValue(),
            translatedTexts.get(i)));
        details.setValue(new ProductDeliveryMethodDetailsValue(details.getValue().getValue(),
            translatedTexts.get(i + newDeliveryMethods.size())));
      }

      List<ProductDeliveryMethodDetails> deliveryMethodsToDelete =
          product.getDeliveryMethodDetails().stream()
              .filter(d -> !oldDetails.contains(d)).toList();

      deliveryMethodsToDelete.forEach(product.getDeliveryMethodDetails()::remove);
      newDeliveryMethods.forEach(product.getDeliveryMethodDetails()::add);
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      log.error(e.getMessage(), e);
    }
  }
}
