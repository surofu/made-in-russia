package com.surofu.exporteru.application.dto.error;

import java.util.Map;

public record ValidationExceptionDto(
        Integer status,
        String error,
        Map<String, String> errors,
        String message
) {
}
