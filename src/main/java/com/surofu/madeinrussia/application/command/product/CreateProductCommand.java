package com.surofu.madeinrussia.application.command.product;

import java.util.List;

public record CreateProductCommand(
        String title,
        String mainDescription,
        String furtherDescription,
        String primaryDescription,
        Long categoryId,
        List<Long> deliveryMethodIds,
        List<CreateProductPriceCommand> prices,
        List<CreateProductCharacteristicCommand> characteristics,
        List<CreateProductFaqCommand> faq
) {
}
