package com.surofu.madeinrussia.core.model.product;

import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private DeliveryMethod deliveryMethod;

    @Embedded
    private ProductTitle title;

    @Embedded
    private ProductPrice price;

    @Embedded
    private ProductDiscount discount;

    @Embedded
    private ProductImageUrl imageUrl;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime creationDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime lastModificationDate;

    public BigDecimal getDiscountedPrice() {
       return price.makeDiscount(discount.getValue());
    }
}
