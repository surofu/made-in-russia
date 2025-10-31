package com.surofu.exporteru.application.dto;

import java.io.Serializable;
import java.util.Map;

public record WebLocalizationDto(
        Map<String, Object> content
) implements Serializable {
}
