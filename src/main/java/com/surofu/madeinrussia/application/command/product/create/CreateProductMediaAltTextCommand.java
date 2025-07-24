package com.surofu.madeinrussia.application.command.product.create;

import com.surofu.madeinrussia.application.dto.translation.TranslationDto;

public record CreateProductMediaAltTextCommand(
        String altText,
        TranslationDto translations
){
}
