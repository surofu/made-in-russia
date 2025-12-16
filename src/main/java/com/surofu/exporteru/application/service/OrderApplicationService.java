package com.surofu.exporteru.application.service;

import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.repository.ProductRepository;
import com.surofu.exporteru.core.service.mail.MailService;
import com.surofu.exporteru.core.service.order.OrderService;
import com.surofu.exporteru.core.service.order.operation.CreateOrder;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderApplicationService implements OrderService {
  private final ProductRepository productRepository;
  private final MailService mailService;
  @Value("${app.frontend.product}")
  private String frontendProductPath;

  @Override
  @Transactional(readOnly = true)
  public CreateOrder.Result createOrder(CreateOrder operation) {
    Optional<Product> productOptional =
        productRepository.getProductByIdApproved(operation.getProductId());

    if (productOptional.isEmpty()) {
      return CreateOrder.Result.notFound(operation.getProductId());
    }

    Product product = productOptional.get();
    String productUrl = String.format("%s/%s", frontendProductPath, product.getId());

    CompletableFuture.runAsync(() -> {
      try {
        mailService.sendProductOrder(
            product.getUser().getEmail().getValue(),
            productUrl,
            product.getTitle().getLocalizedValue(),
            product.getPrices().iterator().next().getOriginalPrice().getValue(),
            product.getPrices().iterator().next().getDiscountedPrice().getValue(),
            operation.getLogin().getLocalizedValue(),
            operation.getPhoneNumber().getValue(),
            operation.getComment()
        );
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    });

    return CreateOrder.Result.success(operation.getProductId(), operation.getLogin());
  }
}
