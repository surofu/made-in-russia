package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.product.productCharacteristic.ProductCharacteristic;
import com.surofu.madeinrussia.infrastructure.persistence.product.productCharacteristic.ProductCharacteristicView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        description = "Data Transfer Object for product characteristics/specifications",
        name = "ProductCharacteristic"
)
public final class ProductCharacteristicDto implements Serializable {

    @Schema(
            description = "Unique identifier of the characteristic",
            example = "105",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long id;

    @Schema(
            description = "Name of the characteristic (e.g., 'Weight', 'Color')",
            example = "Battery Capacity",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @Schema(
            description = "Value of the characteristic with appropriate units",
            example = "5000 mAh",
            maxLength = 255,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String value;

    @Schema(
            description = "Timestamp when the characteristic was created",
            example = "2025-05-04T09:17:20.767615Z",
            type = "string",
            format = "date-time"
    )
    private ZonedDateTime creationDate;

    @Schema(
            description = "Timestamp when the characteristic was last modified",
            example = "2025-05-04T09:17:20.767615Z",
            type = "string",
            format = "date-time",
            nullable = true
    )
    private ZonedDateTime lastModificationDate;

    @Schema(hidden = true)
    public static ProductCharacteristicDto of(ProductCharacteristic productCharacteristic) {
        return ProductCharacteristicDto.builder()
                .id(productCharacteristic.getId())
                .name(productCharacteristic.getName().toString())
                .value(productCharacteristic.getValue().toString())
                .creationDate(productCharacteristic.getCreationDate().getValue())
                .lastModificationDate(productCharacteristic.getLastModificationDate().getValue())
                .build();
    }

    @Schema(hidden = true)
    public static ProductCharacteristicDto of(ProductCharacteristicView view) {
        return ProductCharacteristicDto.builder()
                .id(view.getId())
                .name(view.getName().toString())
                .value(view.getValue().toString())
                .creationDate(view.getCreationDate().getValue())
                .lastModificationDate(view.getLastModificationDate().getValue())
                .build();
    }
}