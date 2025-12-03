package com.surofu.exporteru.application.service.product;

import com.surofu.exporteru.application.service.product.create.ProductCreatingValidator;
import com.surofu.exporteru.application.service.product.create.consumer.ProductCreatingConsumer;
import com.surofu.exporteru.core.model.category.Category;
import com.surofu.exporteru.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.exporteru.core.model.deliveryTerm.DeliveryTerm;
import com.surofu.exporteru.core.model.moderation.ApproveStatus;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.ProductDescription;
import com.surofu.exporteru.core.model.product.ProductPreviewImageUrl;
import com.surofu.exporteru.core.model.product.ProductTitle;
import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.repository.CategoryRepository;
import com.surofu.exporteru.core.repository.DeliveryMethodRepository;
import com.surofu.exporteru.core.repository.DeliveryTermRepository;
import com.surofu.exporteru.core.repository.ProductRepository;
import com.surofu.exporteru.core.repository.TranslationRepository;
import com.surofu.exporteru.core.repository.UserRepository;
import com.surofu.exporteru.core.service.product.operation.CreateProduct;
import java.util.ArrayList;
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
public class ProductCreatingService {
  private final ProductCreatingValidator validationService;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;
  private final DeliveryMethodRepository deliveryMethodRepository;
  private final DeliveryTermRepository deliveryTermRepository;
  private final ProductRepository productRepository;
  private final List<ProductCreatingConsumer> consumers;
  private final TranslationRepository translationRepository;

  @Transactional
  public CreateProduct.Result create(CreateProduct operation) {
    // Validate
    CreateProduct.Result validationResult = validationService.validate(operation);
    if (!(validationResult instanceof CreateProduct.Result.Success)) {
      return validationResult;
    }

    try {
      // Load
      User user =
          userRepository.getById(operation.getSecurityUser().getUser().getId()).orElseThrow();
      Category category = categoryRepository.getById(operation.getCategoryId()).orElseThrow();
      List<DeliveryMethod> deliveryMethods =
          deliveryMethodRepository.getAllDeliveryMethodsByIds(operation.getDeliveryMethodIds());
      List<DeliveryTerm> deliveryTerms =
          deliveryTermRepository.getAllByIds(operation.getDeliveryTermIds());
      List<Product> similarProducts =
          productRepository.findAllByIds(operation.getSimilarProductIds());

      // Translate
      List<String> translationTexts = new ArrayList<>();
      translationTexts.add(operation.getTitle().getValue());
      translationTexts.add(operation.getDescription().getMainDescription());
      if (StringUtils.trimToNull(operation.getDescription().getFurtherDescription()) != null) {
        translationTexts.add(operation.getDescription().getFurtherDescription());
      }
      List<Map<String, String>> translationResults = translationRepository.expand(translationTexts);

      // Setting
      Product product = new Product();
      product.setApproveStatus(ApproveStatus.PENDING);
      product.setTitle(
          new ProductTitle(operation.getTitle().getValue(), translationResults.get(0)));
      product.setDescription(new ProductDescription(
          operation.getDescription().getMainDescription(),
          operation.getDescription().getFurtherDescription(),
          translationResults.get(1),
          StringUtils.trimToNull(operation.getDescription().getFurtherDescription()) != null
              ? translationResults.get(2) : new HashMap<>()
      ));
      product.setMinimumOrderQuantity(operation.getMinimumOrderQuantity());
      product.setDiscountExpirationDate(operation.getDiscountExpirationDate());
      product.setUser(user);
      product.setCategory(category);
      product.setDeliveryMethods(new HashSet<>(deliveryMethods));
      product.setDeliveryTerms(new HashSet<>(deliveryTerms));
      product.setSimilarProducts(new HashSet<>(similarProducts));
      product.setPreviewImageUrl(ProductPreviewImageUrl.of("TEMP"));

      // Save
      Product savedProduct = productRepository.save(product);

      // Produce
      for (ProductCreatingConsumer consumer : consumers) {
        consumer.accept(savedProduct, operation);
      }
      return CreateProduct.Result.success();
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return CreateProduct.Result.errorSavingProduct(e);
    }
  }
}
