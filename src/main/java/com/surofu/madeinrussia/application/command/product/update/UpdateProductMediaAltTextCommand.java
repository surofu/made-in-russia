package com.surofu.madeinrussia.application.command.product.update;

import com.surofu.madeinrussia.application.dto.translation.TranslationDto;

public record UpdateProductMediaAltTextCommand(
        String altText,
        TranslationDto translations
) {
}
