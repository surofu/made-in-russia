package com.surofu.exporteru.application.command.product.update;

import java.util.Map;

public record UpdateProductMediaAltTextCommand(
    String altText,
    Map<String, String> translations
) {
}
