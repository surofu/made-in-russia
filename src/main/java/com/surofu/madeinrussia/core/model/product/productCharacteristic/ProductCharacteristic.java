package com.surofu.madeinrussia.core.model.product.productCharacteristic;

import com.surofu.madeinrussia.core.model.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_characteristics")
public final class ProductCharacteristic implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Embedded
    private ProductCharacteristicName name;

    @Embedded
    private ProductCharacteristicValue value;

    @Embedded
    private ProductCharacteristicCreationDate creationDate;

    @Embedded
    private ProductCharacteristicLastModificationDate lastModificationDate;
}
