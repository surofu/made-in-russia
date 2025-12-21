package com.surofu.exporteru.application.command.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serializable;
import org.springframework.validation.annotation.Validated;

public record CreateOrderCommand(
    @Validated
    @NotNull
    String firstName,
    @Validated
    @NotNull
    @Positive
    Integer quantity,
    String comment
) implements Serializable {
}
