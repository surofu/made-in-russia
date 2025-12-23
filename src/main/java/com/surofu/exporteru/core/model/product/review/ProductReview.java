package com.surofu.exporteru.core.model.product.review;

import com.surofu.exporteru.core.model.moderation.ApproveStatus;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.review.media.ProductReviewMedia;
import com.surofu.exporteru.core.model.user.User;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_reviews")
public final class ProductReview implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;

  @Column(name = "product_id", insertable = false, updatable = false)
  private Long productId;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "approve_status")
  @Enumerated(EnumType.STRING)
  private ApproveStatus approveStatus = ApproveStatus.PENDING;

  @OrderBy("position.value asc")
  @OneToMany(
      mappedBy = "productReview",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true
  )
  private Set<ProductReviewMedia> media = new HashSet<>();

  @Embedded
  private ProductReviewContent content;
  @Embedded
  private ProductReviewRating rating;
  @Embedded
  private ProductReviewCreationDate creationDate;
  @Embedded
  private ProductReviewLastModificationDate lastModificationDate;

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ProductReview that)) {
      return false;
    }
    if (id == null || that.getId() == null) {
      return false;
    }
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
