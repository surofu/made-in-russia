package com.surofu.madeinrussia.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Simple error response DTO containing a single error message
 */
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

    /**
     * Creates an error response with the given message
     * @param errorMessage the error message to include
     * @return constructed error DTO
     */
    @Schema(hidden = true)
    public static SimpleResponseErrorDto of(String errorMessage) {
        return new SimpleResponseErrorDto(errorMessage);
    }
}
