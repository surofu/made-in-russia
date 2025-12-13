package com.surofu.exporteru.application.dto.seo;

import com.surofu.exporteru.infrastructure.persistence.seo.SeoProductView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class SeoProductDto implements Serializable {
    private Long id;
    private String updatedAt;

    public static SeoProductDto of(SeoProductView view) {
        if (view == null) {
            return null;
        }

        return SeoProductDto.builder()
                .id(view.getId())
                .updatedAt(view.getUpdatedAt().toString())
                .build();
    }
}
