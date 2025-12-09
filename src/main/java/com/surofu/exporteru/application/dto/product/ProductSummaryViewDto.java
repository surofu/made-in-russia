package com.surofu.exporteru.application.dto.product;

import com.surofu.exporteru.application.dto.AbstractAccountDto;
import com.surofu.exporteru.application.dto.DeliveryMethodDto;
import com.surofu.exporteru.application.dto.category.CategoryDto;
import com.surofu.exporteru.application.dto.user.UserDto;
import com.surofu.exporteru.core.model.currency.CurrencyCode;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.view.ProductSummaryView;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ProductSummary")
public final class ProductSummaryViewDto implements Serializable {
  private Long id;
  private String approveStatus;
  private AbstractAccountDto user;
  private CategoryDto category;
  private List<DeliveryMethodDto> deliveryMethods;
  private String title;
  private BigDecimal originalPrice;
  private BigDecimal discount;
  private BigDecimal discountedPrice;
  private String priceCurrency;
  private Double rating;
  private String previewImageUrl;
  private ZonedDateTime creationDate;
  private ZonedDateTime lastModificationDate;

  public static ProductSummaryViewDto of(ProductSummaryView view, String lang) {
    return ProductSummaryViewDto.builder()
        .id(view.getId())
        .approveStatus(view.getApproveStatus().toString())
        .user(view.getUser())
        .category(view.getCategory())
        .deliveryMethods(view.getDeliveryMethods())
        .title(view.getTitleByLang(lang))
        .originalPrice(view.getOriginPrice() == null ? null :
            view.getOriginPrice().setScale(2, RoundingMode.DOWN))
        .discount(view.getDiscount())
        .discountedPrice(view.getDiscountedPrice() == null ? null :
            (view.getDiscountedPrice().compareTo(BigDecimal.ZERO) < 1 ? BigDecimal.ONE :
                view.getDiscountedPrice()))
        .priceCurrency(
            view.getPriceCurrencyCode() == null ? null : view.getPriceCurrencyCode().toString())
        .rating(view.getRating())
        .previewImageUrl(view.getPreviewImageUrl())
        .creationDate(view.getCreationDate())
        .lastModificationDate(view.getLastModificationDate())
        .build();
  }

  public static ProductSummaryViewDto of(Product product) {
    return ProductSummaryViewDto.builder()
        .id(product.getId())
        .approveStatus(product.getApproveStatus().toString())
        .user(UserDto.of(product.getUser()))
        .category(CategoryDto.of(product.getCategory()))
        .deliveryMethods(product.getDeliveryMethods().stream().map(DeliveryMethodDto::of).toList())
        .title(product.getTitle().getLocalizedValue())
        .originalPrice(product.getPrices().isEmpty() ?
            BigDecimal.valueOf(-1) :
            product.getPrices().iterator().next().getOriginalPrice().getValue()
                .setScale(2, RoundingMode.DOWN))
        .discount(product.getPrices().isEmpty() ?
            BigDecimal.valueOf(-1) : product.getPrices().iterator().next().getDiscount().getValue()
            .setScale(0, RoundingMode.DOWN))
        .discountedPrice(product.getPrices().isEmpty() ?
            BigDecimal.valueOf(-1) :
            product.getPrices().iterator().next().getDiscountedPrice().getValue()
                .compareTo(BigDecimal.ZERO) < 1 ? BigDecimal.ONE :
                product.getPrices().iterator().next().getDiscountedPrice().getValue())
        .priceCurrency(product.getPrices().isEmpty() ?
            CurrencyCode.USD.name() :
            product.getPrices().iterator().next().getCurrency().getValue().name())
        .rating(product.getRating())
        .previewImageUrl(product.getPreviewImageUrl().getValue())
        .creationDate(product.getCreationDate().getValue())
        .lastModificationDate(product.getLastModificationDate().getValue())
        .build();
  }
}