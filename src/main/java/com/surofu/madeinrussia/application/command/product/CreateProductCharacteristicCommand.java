package com.surofu.madeinrussia.application.command.product;

public record CreateProductCharacteristicCommand(
        String name,
        String value
) {
}
