package com.surofu.madeinrussia.core.model.product;

import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.model.product.characteristic.ProductCharacteristic;
import com.surofu.madeinrussia.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetails;
import com.surofu.madeinrussia.core.model.product.faq.ProductFaq;
import com.surofu.madeinrussia.core.model.product.media.ProductMedia;
import com.surofu.madeinrussia.core.model.product.packageOption.ProductPackageOption;
import com.surofu.madeinrussia.core.model.product.price.ProductPrice;
import com.surofu.madeinrussia.core.model.product.review.ProductReview;
import com.surofu.madeinrussia.core.model.product.review.media.ProductReviewMedia;
import com.surofu.madeinrussia.core.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "products",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_products_article_code",
                        columnNames = "article_code"
                )
        },
        indexes = {
                @Index(
                        name = "idx_products_category_id",
                        columnList = "category_id"
                ),
                @Index(
                        name = "idx_products_article_code",
                        columnList = "article_code"
                ),
                @Index(
                        name = "idx_products_user_id",
                        columnList = "user_id"
                )
        }
)
public final class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_products_user_id")
    )
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "category_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_products_category_id")
    )
    private Category category;

    @Fetch(FetchMode.SUBSELECT)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "products_delivery_methods",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "delivery_method_id")
    )
    private Set<DeliveryMethod> deliveryMethods = new HashSet<>();

    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("position")
    private Set<ProductMedia> media = new HashSet<>();

    @Fetch(FetchMode.SUBSELECT)
    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    @JoinTable(
            name = "similar_products",
            joinColumns = @JoinColumn(name = "parent_product_id"),
            inverseJoinColumns = @JoinColumn(name = "similar_product_id")
    )
    private Set<Product> similarProducts = new HashSet<>();

    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("creationDate")
    private Set<ProductCharacteristic> characteristics = new HashSet<>();

    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("creationDate")
    private Set<ProductReview> reviews = new HashSet<>();

    @Transient
    private Set<ProductReviewMedia> reviewsMedia = new HashSet<>();

    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("creationDate")
    private Set<ProductFaq> faq = new HashSet<>();

    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("creationDate")
    private Set<ProductPrice> prices = new HashSet<>();

    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("creationDate")
    private Set<ProductDeliveryMethodDetails> deliveryMethodDetails = new HashSet<>();

    @Fetch(FetchMode.JOIN)
    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("creationDate")
    private Set<ProductPackageOption> packageOptions = new HashSet<>();

    @Embedded
    private ProductArticleCode articleCode;

    @Embedded
    private ProductTitle title;

    @Embedded
    private ProductDescription description;

    @Transient
    private Double rating;

    @Formula("(select count(*) from product_reviews r where r.product_id = id)")
    private Integer reviewsCount;

    @Embedded
    private ProductPreviewImageUrl previewImageUrl;

    @Embedded
    private ProductCreationDate creationDate;

    @Embedded
    private ProductMinimumOrderQuantity minimumOrderQuantity;

    @Embedded
    private ProductDiscountExpirationDate discountExpirationDate;

    @Embedded
    private ProductLastModificationDate lastModificationDate;

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }
}
