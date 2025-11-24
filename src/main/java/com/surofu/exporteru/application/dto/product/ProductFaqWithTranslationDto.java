package com.surofu.exporteru.application.dto.product;

import com.surofu.exporteru.infrastructure.persistence.product.faq.ProductFaqWithTranslationsView;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "ProductFaq with translations",
        description = "Represents a frequently asked question and answer for a product with localization fields"
)
public final class ProductFaqWithTranslationDto implements Serializable {

    @Schema(
            description = "Unique identifier of the FAQ entry",
            example = "42",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "The question being asked about the product",
            example = "Is this product waterproof?",
            minLength = 10,
            maxLength = 500,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String question;

    private Map<String, String> questionTranslations;

    @Schema(
            description = "The answer to the question",
            example = "Yes, this product has an IP68 waterproof rating.",
            minLength = 10,
            maxLength = 2000,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String answer;

    private Map<String, String> answerTranslations;

    @Schema(
            description = "Timestamp when the FAQ was created",
            example = "2025-05-15T10:30:00Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime creationDate;

    @Schema(
            description = "Timestamp when the FAQ was last modified",
            example = "2025-06-20T14:15:00Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime lastModificationDate;

    @Schema(hidden = true)
    public static ProductFaqWithTranslationDto of(ProductFaqWithTranslationsView view) {
        return ProductFaqWithTranslationDto.builder()
                .id(view.getId())
                .question(view.getQuestion())
                .questionTranslations(view.getQuestionTranslationsMap())
                .answer(view.getAnswer())
                .answerTranslations(view.getAnswerTranslationsMap())
                .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
                .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
                .build();
    }
}