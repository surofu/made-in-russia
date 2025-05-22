package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "DeliveryMethod",
        description = "Represents a delivery method DTO"
)
public final class DeliveryMethodDto implements Serializable {

    @Schema(
            description = "Unique identifier of the delivery method",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "Name of the delivery method",
            example = "Express Shipping",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @Schema(
            description = "Timestamp when the delivery method was created",
            example = "2025-05-04T09:17:20.767615Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime creationDate;

    @Schema(
            description = "Timestamp when the delivery method was last modified",
            example = "2025-05-04T09:17:20.767615Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime lastModificationDate;

    @Schema(hidden = true)
    public static DeliveryMethodDto of(DeliveryMethod deliveryMethod) {
        return DeliveryMethodDto.builder()
                .id(deliveryMethod.getId())
                .name(deliveryMethod.getName().getName())
                .creationDate(deliveryMethod.getCreationDate().getCreationDate())
                .lastModificationDate(deliveryMethod.getLastModificationDate().getLastModificationDate())
                .build();
    }
}
