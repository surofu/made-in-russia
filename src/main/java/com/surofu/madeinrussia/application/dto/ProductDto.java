package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.product.Product;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public final class ProductDto {
    private Long id;
    private DeliveryMethodDto deliveryMethod;
    private CategoryDto category;
    private String title;
    private BigDecimal price;
    private BigDecimal discount;
    private BigDecimal discountedPrice;
    private String imageUrl;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    public static ProductDto of(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .deliveryMethod(DeliveryMethodDto.of(product.getDeliveryMethod()))
                .category(CategoryDto.of(product.getCategory()))
                .title(product.getTitle().getTitle())
                .price(product.getPrice().getPrice())
                .discount(product.getPrice().getDiscount())
                .discountedPrice(product.getPrice().getDiscountedPrice())
                .imageUrl(product.getImageUrl().getImageUrl())
                .creationDate(product.getCreationDate())
                .lastModificationDate(product.getLastModificationDate())
                .build();
    }
}
