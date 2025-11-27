package com.surofu.exporteru.application.service.product.create.consumer;

import com.surofu.exporteru.application.command.product.create.CreateProductCharacteristicCommand;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.characteristic.ProductCharacteristic;
import com.surofu.exporteru.core.model.product.characteristic.ProductCharacteristicName;
import com.surofu.exporteru.core.model.product.characteristic.ProductCharacteristicValue;
import com.surofu.exporteru.core.repository.ProductCharacteristicRepository;
import com.surofu.exporteru.core.repository.TranslationRepository;
import com.surofu.exporteru.core.service.product.operation.CreateProduct;
import com.surofu.exporteru.infrastructure.persistence.product.JpaProductRepository;
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
public class CharacteristicsProductCreatingConsumer implements ProductCreatingConsumer {
  private final ProductCharacteristicRepository characteristicRepository;
  private final TranslationRepository translationRepository;
  private final JpaProductRepository productRepository;

  @Async
  @Override
  @Transactional
  public void accept(Long productId, CreateProduct operation) {
    try {
      Product product = productRepository.getById(productId).orElseThrow();
      List<ProductCharacteristic> productCharacteristics = new ArrayList<>();
      List<Map<String, String>> translatedTexts = translateTexts(operation);

      List<CreateProductCharacteristicCommand> createProductCharacteristicCommands =
          operation.getCreateProductCharacteristicCommands();
      for (int i = 0; i < createProductCharacteristicCommands.size(); i++) {
        CreateProductCharacteristicCommand command = createProductCharacteristicCommands.get(i);
        ProductCharacteristic characteristic = new ProductCharacteristic();
        characteristic.setProduct(product);
        characteristic.setName(
            new ProductCharacteristicName(command.name(), translatedTexts.get(i)));
        characteristic.setValue(new ProductCharacteristicValue(command.value(),
            translatedTexts.get(i + createProductCharacteristicCommands.size())));
        productCharacteristics.add(characteristic);
      }

      characteristicRepository.saveAll(productCharacteristics);
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      log.error(e.getMessage(), e);
    }
  }

  private List<Map<String, String>> translateTexts(CreateProduct operation) {
    List<String> namesToTranslate =
        operation.getCreateProductCharacteristicCommands().stream()
            .map(CreateProductCharacteristicCommand::name).toList();
    List<String> valuesToTranslate =
        operation.getCreateProductCharacteristicCommands().stream()
            .map(CreateProductCharacteristicCommand::value).toList();
    List<String> textToTranslate = new ArrayList<>();
    textToTranslate.addAll(namesToTranslate);
    textToTranslate.addAll(valuesToTranslate);
    return translationRepository.expand(textToTranslate);
  }
}
