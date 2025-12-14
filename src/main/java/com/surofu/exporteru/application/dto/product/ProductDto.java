package com.surofu.exporteru.application.dto.product;

import com.surofu.exporteru.application.dto.DeliveryMethodDto;
import com.surofu.exporteru.application.dto.category.CategoryDto;
import com.surofu.exporteru.application.dto.deliveryTerm.DeliveryTermDto;
import com.surofu.exporteru.application.dto.vendor.VendorDto;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.ProductDiscountExpirationDate;
import com.surofu.exporteru.core.model.product.price.ProductPrice;
import com.surofu.exporteru.core.model.product.price.ProductPriceDiscountedPrice;
import com.surofu.exporteru.infrastructure.persistence.product.ProductView;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Product")
public class ProductDto implements Serializable {
  private Long id;
  private VendorDto user;
  private String approveStatus;
  private CategoryDto category;
  private List<DeliveryMethodDto> deliveryMethods;
  private List<DeliveryTermDto> deliveryTerms;
  private List<ProductMediaDto> media;
  private List<SimilarProductDto> similarProducts;
  private List<ProductCharacteristicDto> characteristics;
  private List<ProductFaqDto> faq;
  private List<ProductPriceDto> prices;
  private String article;
  private String title;
  private String mainDescription;
  private String furtherDescription;
  private Double rating;
  private Integer reviewsCount;
  private String previewImageUrl;
  private List<ProductReviewMediaDto> reviewsMedia;
  private List<ProductDeliveryMethodDetailsDto> deliveryMethodsDetails;
  private List<ProductPackageOptionDto> packagingOptions;
  private String minimumOrderQuantity;
  private Long daysBeforeDiscountExpires;
  private ZonedDateTime creationDate;
  private ZonedDateTime lastModificationDate;

  @Schema(hidden = true)
  public static ProductDto of(Product product) {
    return ProductDto.builder()
        .id(product.getId())
        .user(VendorDto.of(product.getUser()))
        .approveStatus(product.getApproveStatus().toString())
        .category(CategoryDto.ofWithoutChildren(product.getCategory()))
        .deliveryMethods(product.getDeliveryMethods().stream()
            .map(DeliveryMethodDto::of)
            .collect(Collectors.toList())
        )
        .deliveryTerms(product.getDeliveryTerms().stream()
            .map(DeliveryTermDto::of)
            .collect(Collectors.toList())
        )
        .media(product.getMedia().stream().map(ProductMediaDto::of).toList())
        .similarProducts(product.getSimilarProducts().stream().map(SimilarProductDto::of).toList())
        .characteristics(
            product.getCharacteristics().stream().map(ProductCharacteristicDto::of).toList())
        .prices(product.getPrices().stream()
            .map(p -> ProductPriceDto.of(normalizePrice(p, product.getDiscountExpirationDate())))
            .toList())
        .article(product.getArticleCode().toString())
        .title(product.getTitle().getLocalizedValue())
        .mainDescription(product.getDescription().getLocalizedMainDescription())
        .furtherDescription(product.getDescription().getLocalizedFurtherDescription())
        .previewImageUrl(product.getPreviewImageUrl().getValue())
        .creationDate(product.getCreationDate().getValue())
        .lastModificationDate(product.getLastModificationDate().getValue())
        .rating(product.getRating())
        .reviewsCount(product.getReviewsCount())
        .reviewsMedia(product.getReviewsMedia().stream().map(ProductReviewMediaDto::of).toList())
        .faq(product.getFaq().stream().map(ProductFaqDto::of).toList())
        .deliveryMethodsDetails(
            product.getDeliveryMethodDetails().stream().map(ProductDeliveryMethodDetailsDto::of)
                .toList())
        .packagingOptions(
            product.getPackageOptions().stream().map(ProductPackageOptionDto::of).toList())
        .minimumOrderQuantity(product.getMinimumOrderQuantity() == null ? null :
            product.getMinimumOrderQuantity().toString())
        .daysBeforeDiscountExpires(
            getDaysBeforeDiscountExpires(product.getDiscountExpirationDate().getValue()))
        .build();
  }

  @Schema(hidden = true)
  public static ProductDto of(ProductView view) {
    return ProductDto.builder()
        .id(view.getId())
        .approveStatus(view.getApproveStatus())
        .article(view.getArticleCode())
        .title(view.getTitle())
        .mainDescription(view.getMainDescription())
        .furtherDescription(view.getFurtherDescription())
        .rating(view.getRating())
        .reviewsCount(view.getReviewsCount())
        .previewImageUrl(view.getPreviewImageUrl())
        .minimumOrderQuantity(view.getMinimumOrderQuantity() == null ? null :
            view.getMinimumOrderQuantity().toString())
        .daysBeforeDiscountExpires(view.getDiscountExpirationDate() == null ? null :
            getDaysBeforeDiscountExpires(
                view.getDiscountExpirationDate().atZone(ZoneId.systemDefault())))
        .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
        .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
        .build();
  }

  private static ProductPrice normalizePrice(ProductPrice price,
                                             ProductDiscountExpirationDate discountExpirationDate) {
    if (discountExpirationDate == null) {
      return price;
    }

    if (discountExpirationDate.getValue().isBefore(ZonedDateTime.now()) ||
        discountExpirationDate.getValue().isEqual(ZonedDateTime.now())) {
      price.setDiscountedPrice(new ProductPriceDiscountedPrice(price.getOriginalPrice().getValue()));
      return price;
    }

    return price;
  }

  private static Long getDaysBeforeDiscountExpires(ZonedDateTime discountExpirationDate) {
    if (discountExpirationDate == null) {
      return null;
    }

    ZonedDateTime now = ZonedDateTime.now();

    // Если дата истекла или равна текущей - возвращаем null
    if (discountExpirationDate.isBefore(now) || discountExpirationDate.isEqual(now)) {
      return null;
    }

    // Корректный расчет с учетом часовых поясов
    return now.until(discountExpirationDate, ChronoUnit.DAYS);
  }
}