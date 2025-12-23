package com.surofu.exporteru.core.model.product.review.media;

import com.surofu.exporteru.core.model.media.MediaType;
import com.surofu.exporteru.core.model.product.review.ProductReview;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_review_media")
public final class ProductReviewMedia implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_review_id")
  private ProductReview productReview;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private MediaType mediaType;

  @Embedded
  private ProductReviewMediaMimeType mimeType;
  @Embedded
  private ProductReviewMediaMediaPosition position;
  @Embedded
  private ProductReviewMediaUrl url;
  @Embedded
  private ProductReviewMediaAltText altText;
  @Embedded
  private ProductReviewMediaCreationDate creationDate;
  @Embedded
  private ProductReviewMediaLastModificationDate lastModificationDate;

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ProductReviewMedia that)) {
      return false;
    }
    if (id == null || that.id == null) {
      return false;
    }
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
