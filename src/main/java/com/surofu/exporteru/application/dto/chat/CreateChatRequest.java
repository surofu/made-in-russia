package com.surofu.exporteru.application.dto.chat;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO для создания чата
 */
@Getter
@Setter
public class CreateChatRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;
}