package com.surofu.exporteru.application.dto.product;

import com.surofu.exporteru.core.model.product.characteristic.ProductCharacteristic;
import com.surofu.exporteru.infrastructure.persistence.product.characteristic.ProductCharacteristicView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ProductCharacteristic")
public final class ProductCharacteristicDto implements Serializable {
    private Long id;
    private String name;
    private String value;
    private ZonedDateTime creationDate;
    private ZonedDateTime lastModificationDate;

    @Schema(hidden = true)
    public static ProductCharacteristicDto of(ProductCharacteristic productCharacteristic) {
        return ProductCharacteristicDto.builder()
                .id(productCharacteristic.getId())
                .name(productCharacteristic.getName().getLocalizedValue())
                .value(productCharacteristic.getValue().getLocalizedValue())
                .creationDate(productCharacteristic.getCreationDate().getValue())
                .lastModificationDate(productCharacteristic.getLastModificationDate().getValue())
                .build();
    }

    @Schema(hidden = true)
    public static ProductCharacteristicDto of(ProductCharacteristicView view) {
        return ProductCharacteristicDto.builder()
                .id(view.getId())
                .name(view.getName())
                .value(view.getValue())
                .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
                .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
                .build();
    }
}