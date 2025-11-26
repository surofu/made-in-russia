package com.surofu.exporteru.application.service.product.create.consumer;

import com.surofu.exporteru.application.command.product.create.CreateProductDeliveryMethodDetailsCommand;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetails;
import com.surofu.exporteru.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetailsName;
import com.surofu.exporteru.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetailsValue;
import com.surofu.exporteru.core.repository.ProductDeliveryMethodDetailsRepository;
import com.surofu.exporteru.core.repository.ProductRepository;
import com.surofu.exporteru.core.repository.TranslationRepository;
import com.surofu.exporteru.core.service.product.operation.CreateProduct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryMethodDetailsProductCreationCreationConsumer
    implements ProductCreationConsumer {
  private final ProductDeliveryMethodDetailsRepository deliveryMethodDetailsRepository;
  private final TranslationRepository translationRepository;
  private final ProductRepository productRepository;

  @Async
  @Override
  @Transactional
  public void accept(Long productId, CreateProduct operation) {
    try {
      Product product = productRepository.getById(productId).orElseThrow();
      List<ProductDeliveryMethodDetails> deliveryMethodDetailsList = new ArrayList<>();
      List<Map<String, String>> translatedTexts = translateTexts(operation);

      List<CreateProductDeliveryMethodDetailsCommand> createProductDeliveryMethodDetailsCommands =
          operation.getCreateProductDeliveryMethodDetailsCommands();
      for (int i = 0; i < createProductDeliveryMethodDetailsCommands.size(); i++) {
        CreateProductDeliveryMethodDetailsCommand command =
            createProductDeliveryMethodDetailsCommands.get(i);
        ProductDeliveryMethodDetails deliveryMethodDetails = new ProductDeliveryMethodDetails();
        deliveryMethodDetails.setProduct(product);
        deliveryMethodDetails.setName(
            new ProductDeliveryMethodDetailsName(command.name(), translatedTexts.get(i)));
        deliveryMethodDetails.setValue(
            new ProductDeliveryMethodDetailsValue(command.value(),
                translatedTexts.get(i + createProductDeliveryMethodDetailsCommands.size())));
        deliveryMethodDetailsList.add(deliveryMethodDetails);
      }

      deliveryMethodDetailsRepository.saveAll(deliveryMethodDetailsList);
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      log.error(e.getMessage(), e);
    }
  }

  private List<Map<String, String>> translateTexts(CreateProduct operation) {
    List<String> namesToTranslate =
        operation.getCreateProductDeliveryMethodDetailsCommands().stream()
            .map(CreateProductDeliveryMethodDetailsCommand::name).toList();
    List<String> valuesToTranslate =
        operation.getCreateProductDeliveryMethodDetailsCommands().stream()
            .map(CreateProductDeliveryMethodDetailsCommand::value).toList();
    List<String> textToTranslate = new ArrayList<>();
    textToTranslate.addAll(namesToTranslate);
    textToTranslate.addAll(valuesToTranslate);
    return translationRepository.expand(textToTranslate);
  }
}
