package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for Delivery Method information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "DeliveryMethod",
        description = "Represents a shipping/delivery method with its metadata"
)
public final class DeliveryMethodDto implements Serializable {

    @Schema(
            description = "Unique identifier of the delivery method",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Name of the delivery method",
            example = "Express Shipping",
            maxLength = 100
    )
    private String name;

    @Schema(
            description = "Timestamp when the delivery method was created",
            example = "2023-07-15T14:30:00",
            type = "string",
            format = "date-time"
    )
    private LocalDateTime creationDate;

    @Schema(
            description = "Timestamp when the delivery method was last modified",
            example = "2023-07-20T09:15:30",
            type = "string",
            format = "date-time"
    )
    private LocalDateTime lastModificationDate;

    /**
     * Converts a DeliveryMethod entity to DeliveryMethodDto
     * @param deliveryMethod the entity to convert
     * @return converted DTO
     */
    @Schema(hidden = true)
    public static DeliveryMethodDto of(DeliveryMethod deliveryMethod) {
        return DeliveryMethodDto.builder()
                .id(deliveryMethod.getId())
                .name(deliveryMethod.getName().getValue())
                .creationDate(deliveryMethod.getCreationDate())
                .lastModificationDate(deliveryMethod.getLastModificationDate())
                .build();
    }
}
