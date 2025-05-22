package com.surofu.madeinrussia.core.model.product.productReview.productReviewMedia;

import com.surofu.madeinrussia.core.model.media.MediaType;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_review_media")
public final class ProductReviewMedia implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
}
