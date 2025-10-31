package com.surofu.exporteru.application.command.order;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

public record CreateOrderCommand(
        @Validated
        @NotNull
        String email,
        String firstName,
        String phoneNumber,
        @Validated
        @NotNull
        Integer quantity
) implements Serializable {
}
