package com.surofu.exporteru.application.service.product.create.consumer;

import com.surofu.exporteru.application.command.product.create.CreateProductPriceCommand;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.price.ProductPrice;
import com.surofu.exporteru.core.model.product.price.ProductPriceCurrency;
import com.surofu.exporteru.core.model.product.price.ProductPriceDiscount;
import com.surofu.exporteru.core.model.product.price.ProductPriceOriginalPrice;
import com.surofu.exporteru.core.model.product.price.ProductPriceQuantityRange;
import com.surofu.exporteru.core.model.product.price.ProductPriceUnit;
import com.surofu.exporteru.core.repository.ProductPriceRepository;
import com.surofu.exporteru.core.repository.ProductRepository;
import com.surofu.exporteru.core.repository.TranslationRepository;
import com.surofu.exporteru.core.service.product.operation.CreateProduct;
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
public class PricesProductCreatingConsumer implements ProductCreatingConsumer {
  private final ProductPriceRepository priceRepository;
  private final TranslationRepository translationRepository;
  private final ProductRepository productRepository;

  @Async
  @Override
  @Transactional
  public void accept(Long productId, CreateProduct operation) {
    try {
      Product product = productRepository.getById(productId).orElseThrow();
      List<ProductPrice> prices = new ArrayList<>();
      List<Map<String, String>> translatedUnits = translateTexts(operation);

      List<CreateProductPriceCommand> createProductPriceCommands =
          operation.getCreateProductPriceCommands();
      for (int i = 0, unitIndex = 0; i < createProductPriceCommands.size(); i++) {
        CreateProductPriceCommand command = createProductPriceCommands.get(i);
        ProductPrice price = new ProductPrice();
        price.setProduct(product);
        price.setOriginalPrice(ProductPriceOriginalPrice.of(command.price()));
        price.setDiscount(ProductPriceDiscount.of(command.discount()));
        price.setCurrency(ProductPriceCurrency.of(command.currency()));

        if (command.unit().isBlank()) {
          price.setUnit(new ProductPriceUnit(command.unit(), new HashMap<>()));
        } else {
          price.setUnit(new ProductPriceUnit(command.unit(), translatedUnits.get(unitIndex)));
          unitIndex++;
        }

        price.setQuantityRange(
            ProductPriceQuantityRange.of(command.quantityFrom(), command.quantityTo()));
        prices.add(price);
      }

      priceRepository.saveAll(prices);
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      log.error(e.getMessage(), e);
    }
  }

  private List<Map<String, String>> translateTexts(CreateProduct operation) {
    List<String> unitsToTranslate =
        operation.getCreateProductPriceCommands().stream()
            .map(CreateProductPriceCommand::unit)
            .filter(t -> !t.isBlank())
            .toList();
    return translationRepository.expand(unitsToTranslate);
  }
}
