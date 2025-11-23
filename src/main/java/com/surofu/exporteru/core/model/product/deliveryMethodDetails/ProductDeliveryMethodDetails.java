package com.surofu.exporteru.core.model.product.deliveryMethodDetails;

import com.surofu.exporteru.core.model.product.Product;
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
@Table(name = "product_delivery_method_details")
public final class ProductDeliveryMethodDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Embedded
    private ProductDeliveryMethodDetailsName name;

    @Embedded
    private ProductDeliveryMethodDetailsValue value;

    @Embedded
    private ProductDeliveryMethodDetailsCreationDate creationDate;

    @Embedded
    private ProductDeliveryMethodDetailsLastModificationDate lastModificationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductDeliveryMethodDetails productDeliveryMethodDetails)) return false;
        return Objects.equals(name, productDeliveryMethodDetails.name)
            && Objects.equals(value, productDeliveryMethodDetails.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}
