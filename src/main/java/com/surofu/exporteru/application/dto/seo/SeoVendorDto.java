package com.surofu.exporteru.application.dto.seo;

import com.surofu.exporteru.infrastructure.persistence.seo.SeoVendorView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class SeoVendorDto implements Serializable {
    private Long id;
    private String registeredAt;
    private String updatedAt;

    public static SeoVendorDto of(SeoVendorView view) {
        if (view == null) {
            return null;
        }

        return SeoVendorDto.builder()
                .id(view.getId())
                .registeredAt(view.getRegisteredAt().toString())
                .updatedAt(view.getUpdatedAt().toString())
                .build();
    }
}
