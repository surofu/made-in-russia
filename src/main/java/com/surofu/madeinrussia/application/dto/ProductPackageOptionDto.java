package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.product.productPackageOption.ProductPackageOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class ProductPackageOptionDto implements Serializable {

    private Long id;

    private String name;

    private BigDecimal price;

    private ZonedDateTime creationDate;

    private ZonedDateTime lastModificationDate;

    public static ProductPackageOptionDto of(ProductPackageOption productPackageOption) {
        return ProductPackageOptionDto.builder()
                .id(productPackageOption.getId())
                .name(productPackageOption.getName().toString())
                .price(productPackageOption.getPrice().getValue())
                .creationDate(productPackageOption.getCreationDate().getValue())
                .lastModificationDate(productPackageOption.getLastModificationDate().getValue())
                .build();
    }
}
