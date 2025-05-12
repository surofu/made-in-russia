package com.surofu.madeinrussia.application.dto;

import java.util.Map;

public record ValidationExceptionDto(
        int status,
        String error,
        Map<String, String> errors,
        String message
) {
}
