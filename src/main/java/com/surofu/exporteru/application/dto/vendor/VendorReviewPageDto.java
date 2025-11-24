package com.surofu.exporteru.application.dto.vendor;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.surofu.exporteru.application.dto.product.ProductReviewDto;
import com.surofu.exporteru.core.model.product.review.ProductReview;
import java.io.Serializable;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Getter
@Setter
public final class VendorReviewPageDto implements Serializable {

  @JsonUnwrapped
  private PageImpl<ProductReviewDto> page;

  private Double averageRating = 0.0;

  public static VendorReviewPageDto of(Page<ProductReview> page, Double averageRating,
                                       Locale locale) {
    VendorReviewPageDto dto = new VendorReviewPageDto();
    dto.setPage(new PageImpl<>(
        page.getContent().stream()
            .map(p -> translateProductReview(p, locale))
            .toList(),
        page.getPageable(),
        page.getTotalElements()
    ));
    dto.setAverageRating(averageRating);
    return dto;
  }

  private static ProductReviewDto translateProductReview(ProductReview productReview,
                                                         Locale locale) {
    ProductReviewDto dto = ProductReviewDto.of(productReview);

    String translatedProductTitle =
        productReview.getProduct().getTitle().getLocalizedValue();

    if (StringUtils.trimToNull(translatedProductTitle) != null) {
      dto.getProduct().setTitle(translatedProductTitle);
    }

    if (productReview.getUser().getLogin().getTransliteration() != null) {
      String translatedUserLogin =
          productReview.getUser().getLogin().getLocalizedValue(locale);
      dto.getAuthor().setLogin(translatedUserLogin);
    }

    String translatedText = productReview.getContent().getLocalizedValue(locale);

    if (StringUtils.trimToNull(translatedText) != null) {
      dto.setText(translatedText);
    }

    return dto;
  }
}