package com.surofu.madeinrussia.core.model.product;

import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public final class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "products_delivery_types",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "delivery_method_id")
    )
    private List<DeliveryMethod> deliveryMethods;

    @ManyToOne
    private Category category;

    @Embedded
    private ProductTitle title;

    @Embedded
    private ProductPrice price;

    @Embedded
    private ProductImageUrl imageUrl;

    @Embedded
    private ProductCreationDate creationDate;

    @Embedded
    private ProductLastModificationDate lastModificationDate;
}
