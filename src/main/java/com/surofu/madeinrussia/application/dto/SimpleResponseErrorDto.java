package com.surofu.madeinrussia.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "SimpleErrorResponse",
        description = "Basic error response containing a single error message"
)
public final class SimpleResponseErrorDto implements Serializable {

    @Schema(
            description = "Error message describing what went wrong",
            example = "Product not found"
    )
    private String error;

    @Schema(hidden = true)
    public static SimpleResponseErrorDto of(String errorMessage) {
        return new SimpleResponseErrorDto(errorMessage);
    }
}
