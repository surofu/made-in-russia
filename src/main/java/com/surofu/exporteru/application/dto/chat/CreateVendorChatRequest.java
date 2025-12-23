package com.surofu.exporteru.application.dto.chat;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO для создания чата с поставщиком
 */
@Getter
@Setter
public class CreateVendorChatRequest {
    @NotNull(message = "Vendor ID is required")
    private Long vendorId;
}