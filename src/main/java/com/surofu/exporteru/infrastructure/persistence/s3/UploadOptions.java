package com.surofu.exporteru.infrastructure.persistence.s3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadOptions implements Serializable {
    private Float quality;
    private Integer width;
    private Integer height;
}
