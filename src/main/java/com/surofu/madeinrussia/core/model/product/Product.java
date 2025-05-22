package com.surofu.madeinrussia.core.model.product;

import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.model.product.productCharacteristic.ProductCharacteristic;
import com.surofu.madeinrussia.core.model.product.productMedia.ProductMedia;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
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

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    private Set<ProductMedia> media = new HashSet<>();

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    private Set<ProductCharacteristic> characteristics = new HashSet<>();

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    private Set<ProductReview> reviews = new HashSet<>();

    @Embedded
    private ProductArticleCode articleCode;

    @Embedded
    private ProductTitle title;

    @Embedded
    private ProductDescription description;

    @Embedded
    private ProductPrice price;

    @Embedded
    private ProductPreviewImageUrl previewImageUrl;

    @Embedded
    private ProductCreationDate creationDate;

    @Embedded
    private ProductLastModificationDate lastModificationDate;
}
