package com.surofu.exporteru.application.dto.seo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class SeoDto implements Serializable {
    private List<SeoProductDto> products;
    private List<SeoVendorDto> vendors;
}
