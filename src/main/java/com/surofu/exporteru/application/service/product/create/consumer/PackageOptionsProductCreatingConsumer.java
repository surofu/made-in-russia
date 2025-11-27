package com.surofu.exporteru.application.service.product.create.consumer;

import com.surofu.exporteru.application.command.product.create.CreateProductPackageOptionCommand;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.packageOption.ProductPackageOption;
import com.surofu.exporteru.core.model.product.packageOption.ProductPackageOptionName;
import com.surofu.exporteru.core.model.product.packageOption.ProductPackageOptionPrice;
import com.surofu.exporteru.core.model.product.packageOption.ProductPackageOptionPriceUnit;
import com.surofu.exporteru.core.repository.ProductPackageOptionsRepository;
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
public class PackageOptionsProductCreatingConsumer implements ProductCreatingConsumer {
  private final ProductPackageOptionsRepository packageOptionsRepository;
  private final TranslationRepository translationRepository;
  private final ProductRepository productRepository;

  @Async
  @Override
  @Transactional
  public void accept(Long productId, CreateProduct operation) {
    try {
      Product product = productRepository.getById(productId).orElseThrow();
      List<ProductPackageOption> packageOptions = new ArrayList<>();
      List<Map<String, String>> translatedNames = translateTexts(operation);

      List<CreateProductPackageOptionCommand> createProductPackageOptionCommands =
          operation.getCreateProductPackageOptionCommands();
      for (int i = 0; i < createProductPackageOptionCommands.size(); i++) {
        CreateProductPackageOptionCommand command = createProductPackageOptionCommands.get(i);
        ProductPackageOption productPackageOption = new ProductPackageOption();
        productPackageOption.setProduct(product);
        productPackageOption.setName(
            new ProductPackageOptionName(command.name(), translatedNames.get(i)));
        productPackageOption.setPrice(ProductPackageOptionPrice.of(command.price()));
        productPackageOption.setPriceUnit(ProductPackageOptionPriceUnit.of(command.priceUnit()));
        packageOptions.add(productPackageOption);
      }

      packageOptionsRepository.saveAll(packageOptions);
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      log.error(e.getMessage(), e);
    }
  }

  private List<Map<String, String>> translateTexts(CreateProduct operation) {
    List<String> namesToTranslate =
        operation.getCreateProductPackageOptionCommands().stream()
            .map(CreateProductPackageOptionCommand::name).toList();
    return translationRepository.expand(namesToTranslate);
  }
}
