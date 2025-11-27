package com.surofu.exporteru.application.service.product.update.comsumer;

import com.surofu.exporteru.application.command.product.update.UpdateProductCharacteristicCommand;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.characteristic.ProductCharacteristic;
import com.surofu.exporteru.core.model.product.characteristic.ProductCharacteristicName;
import com.surofu.exporteru.core.model.product.characteristic.ProductCharacteristicValue;
import com.surofu.exporteru.core.repository.ProductCharacteristicRepository;
import com.surofu.exporteru.core.repository.ProductRepository;
import com.surofu.exporteru.core.repository.TranslationRepository;
import com.surofu.exporteru.core.service.product.operation.UpdateProduct;
import java.util.ArrayList;
import java.util.HashMap;
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
public class CharacteristicsProductUpdatingConsumer implements ProductUpdatingConsumer {
  private final ProductCharacteristicRepository characteristicRepository;
  private final ProductRepository productRepository;
  private final TranslationRepository translationRepository;

  @Async
  @Override
  @Transactional
  public void accept(Long productId, UpdateProduct operation) {
    try {
      Product product = productRepository.getById(productId).orElseThrow();
      List<ProductCharacteristic> newCharacteristics = new ArrayList<>();
      List<ProductCharacteristic> oldCharacteristics = new ArrayList<>();

      for (UpdateProductCharacteristicCommand command : operation.getUpdateProductCharacteristicCommands()) {
        ProductCharacteristic characteristic = new ProductCharacteristic();
        characteristic.setProduct(product);
        characteristic.setName(new ProductCharacteristicName(command.name(), new HashMap<>()));
        characteristic.setValue(new ProductCharacteristicValue(command.value(), new HashMap<>()));

        if (!product.getCharacteristics().contains(characteristic)) {
          newCharacteristics.add(characteristic);
        } else {
          oldCharacteristics.add(characteristic);
        }
      }

      List<String> namesToTranslate = newCharacteristics.stream()
          .map(ProductCharacteristic::getName)
          .map(ProductCharacteristicName::getValue)
          .toList();
      List<String> valuesToTranslate = newCharacteristics.stream()
          .map(ProductCharacteristic::getValue)
          .map(ProductCharacteristicValue::getValue)
          .toList();
      List<String> textsToTranslate = new ArrayList<>(namesToTranslate.size() + valuesToTranslate.size());
      textsToTranslate.addAll(namesToTranslate);
      textsToTranslate.addAll(valuesToTranslate);

      List<Map<String, String>> translatedTexts = translationRepository.expand(textsToTranslate);
      for (int i = 0; i < newCharacteristics.size(); i++) {
        ProductCharacteristic characteristic = newCharacteristics.get(i);
        characteristic.setName(new ProductCharacteristicName(characteristic.getName().getValue(), translatedTexts.get(i)));
        characteristic.setValue(new ProductCharacteristicValue(characteristic.getValue().getValue(), translatedTexts.get(i + newCharacteristics.size())));
      }

      List<ProductCharacteristic> characteristicsToDelete = product.getCharacteristics().stream()
          .filter(c -> !oldCharacteristics.contains(c)).toList();

      characteristicRepository.deleteAll(characteristicsToDelete);
      characteristicRepository.saveAll(newCharacteristics);
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      log.error(e.getMessage(), e);
    }
  }
}
