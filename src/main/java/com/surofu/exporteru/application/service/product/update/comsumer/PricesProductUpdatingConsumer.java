package com.surofu.exporteru.application.service.product.update.comsumer;

import com.surofu.exporteru.application.command.product.update.UpdateProductPriceCommand;
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
public class PricesProductUpdatingConsumer implements ProductUpdatingConsumer {
  private final ProductPriceRepository priceRepository;
  private final ProductRepository productRepository;
  private final TranslationRepository translationRepository;

  @Async
  @Override
  @Transactional
  public void accept(Long productId, UpdateProduct operation) {
    try {
      Product product = productRepository.getById(productId).orElseThrow();
      List<ProductPrice> newPrices = new ArrayList<>();
      List<ProductPrice> oldPrices = new ArrayList<>();

      for (UpdateProductPriceCommand command : operation.getUpdateProductPriceCommands()) {
        ProductPrice price = new ProductPrice();
        price.setProduct(product);
        price.setOriginalPrice(ProductPriceOriginalPrice.of(command.price()));
        price.setDiscount(ProductPriceDiscount.of(command.discount()));
        price.setQuantityRange(ProductPriceQuantityRange.of(command.quantityFrom(), command.quantityTo()));
        price.setCurrency(ProductPriceCurrency.of(command.currency()));
        price.setUnit(new ProductPriceUnit(command.unit(), new HashMap<>()));

        if (!product.getPrices().contains(price)) {
          newPrices.add(price);
        } else {
          oldPrices.add(price);
        }
      }

      List<String> namesToTranslate = newPrices.stream()
          .map(ProductPrice::getUnit)
          .map(ProductPriceUnit::getValue)
          .toList();

      List<Map<String, String>> translatedTexts = translationRepository.expand(namesToTranslate);
      for (int i = 0; i < newPrices.size(); i++) {
        ProductPrice price = newPrices.get(i);
        price.setUnit(new ProductPriceUnit(price.getUnit().getValue(),
            translatedTexts.get(i)));
      }

      List<ProductPrice> packageOptionsToDelete = product.getPrices().stream()
          .filter(c -> !oldPrices.contains(c)).toList();

      priceRepository.deleteAll(packageOptionsToDelete);
      priceRepository.saveAll(newPrices);
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      log.error(e.getMessage(), e);
    }
  }
}
