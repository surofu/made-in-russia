package com.surofu.exporteru.core.model.product.review.media;

import com.surofu.exporteru.core.model.media.MediaType;
import com.surofu.exporteru.core.model.product.review.ProductReview;
import jakarta.persistence.*;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "product_review_media",
        indexes = {
                @Index(
                        name = "idx_product_review_media_product_review_id",
                        columnList = "product_review_id"
                )
        }
)
public final class ProductReviewMedia implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_review_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_review_media_product_review_id")
    )
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
        if (this == o) return true;
        if (!(o instanceof ProductReviewMedia productReviewMedia)) return false;
        return Objects.equals(id, productReviewMedia.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
