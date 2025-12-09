package com.surofu.exporteru.application.service.product;

import com.surofu.exporteru.application.service.product.update.ProductUpdatingValidator;
import com.surofu.exporteru.application.service.product.update.comsumer.ProductUpdatingConsumer;
import com.surofu.exporteru.core.model.category.Category;
import com.surofu.exporteru.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.exporteru.core.model.deliveryTerm.DeliveryTerm;
import com.surofu.exporteru.core.model.moderation.ApproveStatus;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.ProductDescription;
import com.surofu.exporteru.core.model.product.ProductPreviewImageUrl;
import com.surofu.exporteru.core.model.product.ProductTitle;
import com.surofu.exporteru.core.repository.CategoryRepository;
import com.surofu.exporteru.core.repository.DeliveryMethodRepository;
import com.surofu.exporteru.core.repository.DeliveryTermRepository;
import com.surofu.exporteru.core.repository.ProductRepository;
import com.surofu.exporteru.core.repository.TranslationRepository;
import com.surofu.exporteru.core.service.product.operation.UpdateProduct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductUpdatingService {
  private final ProductUpdatingValidator validator;
  private final List<ProductUpdatingConsumer> consumers;
  private final ProductRepository productRepository;
  private final TranslationRepository translationRepository;
  private final CategoryRepository categoryRepository;
  private final DeliveryMethodRepository deliveryMethodRepository;
  private final DeliveryTermRepository deliveryTermRepository;

  @Transactional
  public UpdateProduct.Result update(UpdateProduct operation) {
    // Validate
    UpdateProduct.Result validationResult = validator.validate(operation);
    if (!(validationResult instanceof UpdateProduct.Result.Success)) {
      return validationResult;
    }

    try {
      // Load
      Product product =
          productRepository.getByIdWithDependencies(operation.getProductId()).orElseThrow();
      Category category = categoryRepository.getById(operation.getCategoryId()).orElseThrow();
      List<DeliveryMethod> deliveryMethods =
          deliveryMethodRepository.getAllDeliveryMethodsByIds(operation.getDeliveryMethodIds());
      List<DeliveryTerm> deliveryTerms =
          deliveryTermRepository.getAllByIds(operation.getDeliveryTermIds());
      List<Product> similarProducts =
          productRepository.findAllByIds(operation.getSimilarProductIds());

      // Translate
      Map<String, String> translateMap = new HashMap<>();
      if (!product.getTitle().equals(operation.getTitle())) {
        translateMap.put("title", operation.getTitle().getValue());
      }
      if (!product.getDescription().getMainDescription()
          .equals(operation.getDescription().getMainDescription())) {
        translateMap.put("mainDescription", operation.getDescription().getMainDescription());
      }
      if (StringUtils.trimToNull(operation.getDescription().getFurtherDescription()) != null &&
          !product.getDescription().getFurtherDescription()
              .equals(operation.getDescription().getFurtherDescription())) {
        translateMap.put("furtherDescription", operation.getDescription().getFurtherDescription());
      }
      Map<String, Map<String, String>> translationResults =
          translationRepository.expandMap(translateMap);

      // Setting
      product.setApproveStatus(ApproveStatus.PENDING);
      product.setMinimumOrderQuantity(operation.getMinimumOrderQuantity());
      product.setDiscountExpirationDate(operation.getDiscountExpirationDate());
      product.setCategory(category);
      product.setDeliveryMethods(new HashSet<>(deliveryMethods));
      product.setDeliveryTerms(new HashSet<>(deliveryTerms));
      product.setSimilarProducts(new HashSet<>(similarProducts));
      product.setPreviewImageUrl(ProductPreviewImageUrl.of("TEMP"));

      if (translationResults.containsKey("title")) {
        product.setTitle(new ProductTitle(
            operation.getTitle().getValue(),
            translationResults.get("title")
        ));
      }
      if (translationResults.containsKey("mainDescription")) {
        product.setDescription(new ProductDescription(
            operation.getDescription().getMainDescription(),
            product.getDescription().getFurtherDescription(),
            translationResults.get("mainDescription"),
            product.getDescription().getFurtherDescriptionTranslations()
        ));
      }
      if (translationResults.containsKey("furtherDescription")) {
        product.setDescription(new ProductDescription(
            product.getDescription().getMainDescription(),
            operation.getDescription().getFurtherDescription(),
            product.getDescription().getMainDescriptionTranslations(),
            translationResults.get("furtherDescription")
        ));
      }

      // Save
      Product savedProduct = productRepository.save(product);
      productRepository.flush();

      // Produce
      for (ProductUpdatingConsumer consumer : consumers) {
        consumer.accept(savedProduct, operation);
      }
      return UpdateProduct.Result.success();
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return UpdateProduct.Result.errorSavingProduct(e);
    }
  }
}
