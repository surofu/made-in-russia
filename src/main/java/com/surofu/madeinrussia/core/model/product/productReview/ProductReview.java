package com.surofu.madeinrussia.core.model.product.productReview;

import com.surofu.madeinrussia.core.model.product.Product;
import com.surofu.madeinrussia.core.model.product.ProductLastModificationDate;
import com.surofu.madeinrussia.core.model.product.productReview.productReviewMedia.ProductReviewMedia;
import com.surofu.madeinrussia.core.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_reviews")
public final class ProductReview implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToMany(
            mappedBy = "productReview",
            fetch = FetchType.LAZY,
            cascade = { CascadeType.PERSIST, CascadeType.MERGE }
    )
    private Set<ProductReviewMedia> media = new HashSet<>();

    @Embedded
    private ProductReviewText text;

    @Embedded
    private ProductReviewRating rating;

    @Embedded
    private ProductReviewCreationDate creationDate;

    @Embedded
    private ProductLastModificationDate lastModificationDate;
}
