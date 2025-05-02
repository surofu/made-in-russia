package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DeliveryMethodDto {
    private Long id;
    private String name;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    public static DeliveryMethodDto of(DeliveryMethod deliveryMethod) {
        return DeliveryMethodDto.builder()
                .id(deliveryMethod.getId())
                .name(deliveryMethod.getName().getValue())
                .creationDate(deliveryMethod.getCreationDate())
                .lastModificationDate(deliveryMethod.getLastModificationDate())
                .build();
    }
}
