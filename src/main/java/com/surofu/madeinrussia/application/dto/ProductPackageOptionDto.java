package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.product.productPackageOption.ProductPackageOption;
import com.surofu.madeinrussia.infrastructure.persistence.product.packageOption.ProductPackageOptionView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class ProductPackageOptionDto implements Serializable {

    private Long id;

    private String name;

    private BigDecimal price;

    private String priceUnit;

    private ZonedDateTime creationDate;

    private ZonedDateTime lastModificationDate;

    @Schema(hidden = true)
    public static ProductPackageOptionDto of(ProductPackageOption productPackageOption) {
        return ProductPackageOptionDto.builder()
                .id(productPackageOption.getId())
                .name(productPackageOption.getName().toString())
                .price(productPackageOption.getPrice().getValue())
                .priceUnit(productPackageOption.getPriceUnit().toString())
                .creationDate(productPackageOption.getCreationDate().getValue())
                .lastModificationDate(productPackageOption.getLastModificationDate().getValue())
                .build();
    }

    public static ProductPackageOptionDto of(ProductPackageOptionView view) {
        return ProductPackageOptionDto.builder()
                .id(view.getId())
                .name(view.getName())
                .price(view.getPrice())
                .priceUnit(view.getPriceUnit())
                .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
                .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
                .build();
    }
}
