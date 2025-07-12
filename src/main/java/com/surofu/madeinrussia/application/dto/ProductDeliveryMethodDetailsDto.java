package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.product.productDeliveryMethodDetails.ProductDeliveryMethodDetails;
import com.surofu.madeinrussia.infrastructure.persistence.product.productDeliveryMethodDetails.ProductDeliveryMethodDetailsView;
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
public final class ProductDeliveryMethodDetailsDto implements Serializable {

    private Long id;

    private String name;

    private String value;

    private ZonedDateTime creationDate;

    private ZonedDateTime lastModificationDate;

    @Schema(hidden = true)
    public static ProductDeliveryMethodDetailsDto of(ProductDeliveryMethodDetails productDeliveryMethodDetails) {
        return ProductDeliveryMethodDetailsDto.builder()
                .id(productDeliveryMethodDetails.getId())
                .name(productDeliveryMethodDetails.getName().toString())
                .value(productDeliveryMethodDetails.getValue().toString())
                .creationDate(productDeliveryMethodDetails.getCreationDate().getValue())
                .lastModificationDate(productDeliveryMethodDetails.getLastModificationDate().getValue())
                .build();
    }

    @Schema(hidden = true)
    public static ProductDeliveryMethodDetailsDto of(ProductDeliveryMethodDetailsView view) {
        return ProductDeliveryMethodDetailsDto.builder()
                .id(view.getId())
                .name(view.getName().toString())
                .value(view.getValue().toString())
                .creationDate(view.getCreationDate().getValue())
                .lastModificationDate(view.getLastModificationDate().getValue())
                .build();
    }
}
