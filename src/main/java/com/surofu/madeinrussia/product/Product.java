package com.surofu.madeinrussia.product;

import com.surofu.madeinrussia.deliveryMethod.DeliveryMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    private String title;

    private BigDecimal price;

    private BigDecimal discount;

    private String imageUrl;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime creationDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime lastModificationDate;

    public BigDecimal getDiscountedPrice() {
        if (price == null || discount == null) {
            return BigDecimal.ZERO;
        }

        // Формула: цена * (1 - скидка/100)
        return price.multiply(
                BigDecimal.ONE.subtract(
                        discount.divide(BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP
                        )
                )
        ).setScale(2, RoundingMode.HALF_UP);
    }
}
