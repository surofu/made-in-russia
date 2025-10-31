package com.surofu.exporteru.core.model.deliveryMethod;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "delivery_methods",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_delivery_methods_name",
                        columnNames = "name"
                )
        },
        indexes = {
                @Index(
                        name = "idx_delivery_methods_name",
                        columnList = "name"
                )
        }
)
@BatchSize(size = 40)
public final class DeliveryMethod implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private DeliveryMethodName name;

    @Embedded
    private DeliveryMethodCreationDate creationDate;

    @Embedded
    private DeliveryMethodLastModificationDate lastModificationDate;
}
