package com.surofu.madeinrussia.core.model.product.review;

import com.surofu.madeinrussia.core.model.moderation.ApproveStatus;
import com.surofu.madeinrussia.core.model.product.Product;
import com.surofu.madeinrussia.core.model.product.review.media.ProductReviewMedia;
import com.surofu.madeinrussia.core.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "product_reviews",
        indexes = {
                @Index(
                        name = "idx_product_reviews_product_id",
                        columnList = "product_id"
                ),
                @Index(
                        name = "idx_product_reviews_user_id",
                        columnList = "user_id"
                )
        }
)
public final class ProductReview implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_reviews_product_id")
    )
    private Product product;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_reviews_user_id")
    )
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
        if (this == o) return true;
        if (!(o instanceof ProductReview)) return false;
        return id != null && id.equals(((ProductReview) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
