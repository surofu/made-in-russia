package com.surofu.exporteru.application.command.product.create;

import com.surofu.exporteru.application.dto.translation.TranslationDto;

public record CreateProductMediaAltTextCommand(
        String altText,
        TranslationDto translations
){
}
