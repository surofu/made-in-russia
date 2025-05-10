package com.surofu.madeinrussia.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "SimpleMessageResponse",
        description = "Basic success response containing a single message"
)
public final class SimpleResponseMessageDto implements Serializable {

    @Schema(
            description = "Human-readable success message",
            example = "Operation completed successfully",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String message;

    @Schema(hidden = true)
    public static SimpleResponseMessageDto of(String message) {
        return SimpleResponseMessageDto.builder()
                .message(message)
                .build();
    }
}
