package com.surofu.exporteru.application.service.product.update.comsumer;

import com.surofu.exporteru.application.command.product.update.UpdateProductPackageOptionCommand;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.packageOption.ProductPackageOption;
import com.surofu.exporteru.core.model.product.packageOption.ProductPackageOptionName;
import com.surofu.exporteru.core.model.product.packageOption.ProductPackageOptionPrice;
import com.surofu.exporteru.core.model.product.packageOption.ProductPackageOptionPriceUnit;
import com.surofu.exporteru.core.repository.ProductPackageOptionsRepository;
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
public class PackageOptonsProductUpdatingConsumer implements ProductUpdatingConsumer {
  private final ProductPackageOptionsRepository packageOptionsRepository;
  private final TranslationRepository translationRepository;

  @Override
  @Transactional
  public void accept(Product product, UpdateProduct operation) {
    try {
      List<ProductPackageOption> newPackageOptions = new ArrayList<>();
      List<ProductPackageOption> oldPackageOptions = new ArrayList<>();

      for (UpdateProductPackageOptionCommand command : operation.getUpdateProductPackageOptionCommands()) {
        ProductPackageOption packageOption = new ProductPackageOption();
        packageOption.setProduct(product);
        packageOption.setName(new ProductPackageOptionName(command.name(), new HashMap<>()));
        packageOption.setPrice(ProductPackageOptionPrice.of(command.price()));
        packageOption.setPriceUnit(ProductPackageOptionPriceUnit.of(command.priceUnit()));

        if (!product.getPackageOptions().contains(packageOption)) {
          newPackageOptions.add(packageOption);
        } else {
          oldPackageOptions.add(packageOption);
        }
      }

      List<String> namesToTranslate = newPackageOptions.stream()
          .map(ProductPackageOption::getName)
          .map(ProductPackageOptionName::getValue)
          .toList();

      List<Map<String, String>> translatedTexts = translationRepository.expand(namesToTranslate);
      for (int i = 0; i < newPackageOptions.size(); i++) {
        ProductPackageOption packageOption = newPackageOptions.get(i);
        packageOption.setName(new ProductPackageOptionName(packageOption.getName().getValue(),
            translatedTexts.get(i)));
      }

      List<ProductPackageOption> packageOptionsToDelete = product.getPackageOptions().stream()
          .filter(c -> !oldPackageOptions.contains(c)).toList();

      packageOptionsRepository.deleteAll(packageOptionsToDelete);
      packageOptionsRepository.saveAll(newPackageOptions);
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      log.error(e.getMessage(), e);
    }
  }
}
