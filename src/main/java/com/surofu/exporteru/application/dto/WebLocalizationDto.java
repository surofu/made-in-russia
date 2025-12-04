package com.surofu.exporteru.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Map;

@Schema(name = "WebLocalization")
public record WebLocalizationDto(
    Map<String, Object> content
) implements Serializable {
}
