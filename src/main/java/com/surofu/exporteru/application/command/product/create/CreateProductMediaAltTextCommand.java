package com.surofu.exporteru.application.command.product.create;

import java.util.Map;

public record CreateProductMediaAltTextCommand(
    String altText,
    Map<String, String> translations
) {
}
