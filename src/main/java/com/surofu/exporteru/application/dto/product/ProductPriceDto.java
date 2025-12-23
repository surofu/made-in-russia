package com.surofu.exporteru.application.dto.product;

import com.surofu.exporteru.application.components.PriceUnitSlugFactory;
import com.surofu.exporteru.core.model.product.price.ProductPrice;
import com.surofu.exporteru.infrastructure.persistence.product.price.ProductPriceView;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ProductPrice")
public final class ProductPriceDto implements Serializable {
  private Long id;
  private Integer from;
  private Integer to;
  private String currency;
  private String unit;
  private String unitSlug;
  private BigDecimal originalPrice;
  private BigDecimal discount;
  private BigDecimal discountedPrice;
  private ZonedDateTime creationDate;
  private ZonedDateTime lastModificationDate;

  public static ProductPriceDto of(ProductPrice productPrice) {
    return ProductPriceDto.builder()
        .id(productPrice.getId())
        .from(productPrice.getQuantityRange().getFrom())
        .to(productPrice.getQuantityRange().getTo())
        .currency(productPrice.getCurrency().toString())
        .unit(productPrice.getUnit().getLocalizedValue())
        .unitSlug(PriceUnitSlugFactory.getSlug(productPrice.getUnit().getValue()))
        .originalPrice(productPrice.getOriginalPrice().getValue().setScale(0, RoundingMode.DOWN))
        .discount(productPrice.getDiscount().getValue().setScale(0, RoundingMode.DOWN))
        .discountedPrice(productPrice.getDiscount().getValue().equals(BigDecimal.ZERO) ?
            productPrice.getOriginalPrice().getValue() :
            productPrice.getDiscountedPrice().getValue().setScale(0, RoundingMode.DOWN))
        .creationDate(productPrice.getCreationDate().getValue())
        .lastModificationDate(productPrice.getLastModificationDate().getValue())
        .build();
  }

  public static ProductPriceDto of(ProductPriceView view) {
    return ProductPriceDto.builder()
        .id(view.getId())
        .from(view.getQuantityFrom())
        .to(view.getQuantityTo())
        .currency(view.getCurrency().toString())
        .unit(view.getUnit())
        .unitSlug(PriceUnitSlugFactory.getSlug(view.getUnit()))
        .originalPrice(view.getOriginalPrice().setScale(0, RoundingMode.DOWN))
        .discount(view.getDiscount().setScale(0, RoundingMode.DOWN))
        .discountedPrice(view.getDiscount().equals(BigDecimal.ZERO) ? view.getOriginalPrice() :
            view.getDiscountedPrice().setScale(0, RoundingMode.DOWN))
        .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
        .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
        .build();
  }
}