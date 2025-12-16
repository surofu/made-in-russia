package com.surofu.exporteru.application.command.order;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

public record CreateOrderCommand(
        @Validated
        @NotNull
        String firstName,
        @Validated
        @NotNull
        String phoneNumber,
        String comment
) implements Serializable {
}
