package com.surofu.exporteru.application.dto.vendor;

import com.surofu.exporteru.core.model.vendorDetails.media.VendorMedia;
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
public final class VendorMediaDto implements Serializable {

    private Long id;

    private String mediaType;

    private String mimeType;

    private String url;

    private ZonedDateTime creationDate;

    private ZonedDateTime lastModificationDate;

    public static VendorMediaDto of(VendorMedia entity) {
        return VendorMediaDto.builder()
                .id(entity.getId())
                .mediaType(entity.getMediaType().getName())
                .mimeType(entity.getMimeType().toString())
                .url(entity.getUrl().toString())
                .creationDate(entity.getCreationDate().getValue())
                .lastModificationDate(entity.getLastModificationDate().getValue())
                .build();
    }
}
