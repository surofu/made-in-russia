package com.surofu.exporteru.application.command.product.update;

import com.surofu.exporteru.application.dto.translation.TranslationDto;

public record UpdateProductMediaAltTextCommand(
        String altText,
        TranslationDto translations
) {
}
