package com.surofu.madeinrussia.application.command.product.create;

import com.surofu.madeinrussia.application.dto.translation.TranslationDto;

public record CreateProductVendorDetailsMediaAltTextCommand(
        String altText,
        TranslationDto translations
){
}
