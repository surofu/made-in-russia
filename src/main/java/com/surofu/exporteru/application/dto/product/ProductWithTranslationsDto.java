package com.surofu.exporteru.application.dto.product;

import com.surofu.exporteru.application.dto.DeliveryMethodDto;
import com.surofu.exporteru.application.dto.category.CategoryDto;
import com.surofu.exporteru.application.dto.deliveryTerm.DeliveryTermDto;
import com.surofu.exporteru.application.dto.user.UserDto;
import com.surofu.exporteru.application.dto.vendor.VendorDto;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.infrastructure.persistence.product.ProductWithTranslationsView;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Product with translations")
public class ProductWithTranslationsDto implements Serializable {
  private Long id;
  private VendorDto user;
  private CategoryDto category;
  private List<DeliveryMethodDto> deliveryMethods;
  private List<DeliveryTermDto> deliveryTerms;
  private List<ProductMediaWithTranslationsDto> media;
  private List<SimilarProductDto> similarProducts;
  private List<ProductCharacteristicWithTranslationsDto> characteristics;
  private List<ProductFaqWithTranslationDto> faq;
  private List<ProductPriceDto> prices;
  private String article;
  private String title;
  private String mainDescription;
  private String furtherDescription;
  private Double rating;
  private Integer reviewsCount;
  private String previewImageUrl;
  private List<ProductReviewMediaDto> reviewsMedia;
  private List<ProductDeliveryMethodDetailsWithTranslationsDto> deliveryMethodsDetails;
  private List<ProductPackageOptionWithTranslationsDto> packagingOptions;
  private String minimumOrderQuantity;
  private Long daysBeforeDiscountExpires;
  private ZonedDateTime creationDate;
  private ZonedDateTime lastModificationDate;
  private Map<String, String> titleTranslations;
  private Map<String, String> mainDescriptionTranslations;
  private Map<String, String> furtherDescriptionTranslations;

  public static ProductWithTranslationsDto of(ProductWithTranslationsView view) {
    return ProductWithTranslationsDto.builder()
        .id(view.getId())
        .article(view.getArticleCode())
        .title(view.getTitle())
        .titleTranslations(view.getTitleTranslationsMap())
        .mainDescription(view.getMainDescription())
        .mainDescriptionTranslations(view.getMainDescriptionTranslationsMap())
        .furtherDescription(view.getFurtherDescription())
        .furtherDescriptionTranslations(view.getFurtherDescriptionTranslationsMap())
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

  public static ProductWithTranslationsDto of(Product product) {
    return ProductWithTranslationsDto.builder()
        .id(product.getId())
        .article(product.getArticleCode().toString())
        .title(product.getTitle().getLocalizedValue())
        .titleTranslations(product.getTitle().getTranslations())
        .mainDescription(product.getDescription().getLocalizedMainDescription())
        .mainDescriptionTranslations(product.getDescription().getMainDescriptionTranslations())
        .furtherDescription(product.getDescription().getLocalizedFurtherDescription())
        .furtherDescriptionTranslations(product.getDescription().getFurtherDescriptionTranslations())
        .rating(product.getRating())
        .reviewsCount(product.getReviewsCount())
        .previewImageUrl(product.getPreviewImageUrl().toString())
        .minimumOrderQuantity(product.getMinimumOrderQuantity() == null ? null :
            product.getMinimumOrderQuantity().toString())
        .daysBeforeDiscountExpires(product.getDiscountExpirationDate() == null ? null :
            getDaysBeforeDiscountExpires(product.getDiscountExpirationDate().getValue()))
        .creationDate(product.getCreationDate().getValue())
        .lastModificationDate(product.getLastModificationDate().getValue())
        .build();
  }

  private static Long getDaysBeforeDiscountExpires(ZonedDateTime discountExpirationDate) {
    if (discountExpirationDate == null) {
      return null;
    }
    return ZonedDateTime.now().until(discountExpirationDate, ChronoUnit.DAYS);
  }
}