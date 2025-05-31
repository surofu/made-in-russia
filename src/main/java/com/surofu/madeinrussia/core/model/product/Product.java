package com.surofu.madeinrussia.core.model.product;

import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.model.product.productCharacteristic.ProductCharacteristic;
import com.surofu.madeinrussia.core.model.product.productFaq.ProductFaq;
import com.surofu.madeinrussia.core.model.product.productMedia.ProductMedia;
import com.surofu.madeinrussia.core.model.product.productPrice.ProductPrice;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public final class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @Fetch(FetchMode.SUBSELECT)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "products_delivery_methods",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "delivery_method_id")
    )
    private Set<DeliveryMethod> deliveryMethods = new HashSet<>();

    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    private Set<ProductMedia> media = new HashSet<>();

    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    private Set<ProductCharacteristic> characteristics = new HashSet<>();

    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    private Set<ProductReview> reviews = new HashSet<>();

    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    private Set<ProductFaq> faq = new HashSet<>();

    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    private Set<ProductPrice> prices = new HashSet<>();

    @Embedded
    private ProductArticleCode articleCode;

    @Embedded
    private ProductTitle title;

    @Embedded
    private ProductDescription description;

    @Formula("""
                (SELECT
                         CASE
                             WHEN COUNT(r.rating) = 0 THEN NULL
                             ELSE CAST(ROUND(
                                 CASE
                                     WHEN AVG(r.rating) < 1.0 THEN 1.0
                                     WHEN AVG(r.rating) > 5.0 THEN 5.0
                                     ELSE AVG(r.rating)
                                 END, 1) AS DOUBLE PRECISION)
                         END
                     FROM product_reviews r
                     WHERE r.product_id = id)
            """)
    private Double rating;

    @Formula("(select count(*) from product_reviews r where r.product_id = id)")
    private Integer reviewsCount;

    @Embedded
    private ProductPreviewImageUrl previewImageUrl;

    @Embedded
    private ProductCreationDate creationDate;

    @Embedded
    private ProductLastModificationDate lastModificationDate;
}
