package com.surofu.madeinrussia.core.model.deliveryMethod;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "delivery_methods")
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
