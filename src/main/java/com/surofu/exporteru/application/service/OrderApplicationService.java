package com.surofu.exporteru.application.service;

import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.price.ProductPrice;
import com.surofu.exporteru.core.repository.ProductRepository;
import com.surofu.exporteru.core.service.mail.MailService;
import com.surofu.exporteru.core.service.order.OrderService;
import com.surofu.exporteru.core.service.order.operation.CreateOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderApplicationService implements OrderService {

    @Value("${app.frontend.product}")
    private String frontendProductPath;

    private final ProductRepository productRepository;
    private final MailService mailService;

    @Override
    @Transactional(readOnly = true)
    public CreateOrder.Result createOrder(CreateOrder operation) {
        Optional<Product> productOptional = productRepository.getProductByIdApproved(operation.getProductId());

        if (productOptional.isEmpty()) {
            return CreateOrder.Result.notFound(operation.getProductId());
        }

        Product product = productOptional.get();

        BigDecimal originalPrice = null, discountedPrice = null;

        for (ProductPrice price : product.getPrices()) {
            if (price.getQuantityRange().getFrom() < operation.getQuantity() && price.getQuantityRange().getTo() > operation.getQuantity()) {
                originalPrice = price.getOriginalPrice().getValue().multiply(BigDecimal.valueOf(operation.getQuantity()));
                discountedPrice = price.getDiscountedPrice().getValue();
                break;
            }
        }

        if (originalPrice == null || discountedPrice == null) {
            if (!product.getPrices().isEmpty()) {
                originalPrice = product.getPrices().iterator().next().getOriginalPrice().getValue();
                discountedPrice = product.getPrices().iterator().next().getDiscountedPrice().getValue();
            }
        }

        final BigDecimal finalOriginalPrice = originalPrice;
        final BigDecimal finalDiscountedPrice = discountedPrice;

        String productUrl = String.format("%s/%s", frontendProductPath, product.getId());

        CompletableFuture.runAsync(() -> {
            try {
                mailService.sendProductOrder(
                        product.getUser().getEmail().getValue(),
                        productUrl,
                        product.getTitle().getLocalizedValue(),
                        finalOriginalPrice,
                        finalDiscountedPrice,
                        operation.getFirstName(),
                        operation.getEmail(),
                        operation.getPhoneNumber(),
                        operation.getQuantity()
                );
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });

        return CreateOrder.Result.success(operation.getProductId(), operation.getEmail());
    }
}
