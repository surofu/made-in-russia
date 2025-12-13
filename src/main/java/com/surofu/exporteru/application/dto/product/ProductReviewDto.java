package com.surofu.exporteru.application.dto.product;

import com.surofu.exporteru.application.dto.user.UserDto;
import com.surofu.exporteru.core.model.product.review.ProductReview;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ProductReview")
public final class ProductReviewDto implements Serializable {
  private Long id;
  private ProductForReviewDto product;
  private String approveStatus;
  private UserDto author;
  private String text;
  private List<ProductReviewMediaDto> media;
  private Integer rating;
  private ZonedDateTime creationDate;
  private ZonedDateTime lastModificationDate;

  public static ProductReviewDto of(ProductReview productReview) {
    System.out.println("Locale: " + LocaleContextHolder.getLocale().getLanguage());
    System.out.println("login: " + UserDto.of(productReview.getUser()).getLogin());

    return ProductReviewDto.builder()
        .id(productReview.getId())
        .product(ProductForReviewDto.of(productReview.getProduct()))
        .approveStatus(productReview.getApproveStatus().toString())
        .author(UserDto.of(productReview.getUser()))
        .text(productReview.getContent().getLocalizedValue())
        .media(productReview.getMedia().stream().map(ProductReviewMediaDto::of).toList())
        .rating(productReview.getRating().getValue())
        .creationDate(productReview.getCreationDate().getValue())
        .lastModificationDate(productReview.getLastModificationDate().getValue())
        .build();
  }
}