package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.product.productFaq.ProductFaq;
import com.surofu.madeinrussia.infrastructure.persistence.product.faq.ProductFaqView;
import io.swagger.v3.oas.annotations.media.Schema;
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
        name = "ProductFAQ",
        description = "Represents a frequently asked question and answer for a product",
        example = """
                {
                  "id": 42,
                  "question": "What is the warranty period for this product?",
                  "answer": "This product comes with a 2-year manufacturer warranty.",
                  "creationDate": "2025-05-15T10:30:00Z",
                  "lastModificationDate": "2025-06-20T14:15:00Z"
                }
                """
)
public final class ProductFaqDto implements Serializable {

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

    @Schema(
            description = "The answer to the question",
            example = "Yes, this product has an IP68 waterproof rating.",
            minLength = 10,
            maxLength = 2000,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String answer;

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
    public static ProductFaqDto of(ProductFaq productFaq) {
        return ProductFaqDto.builder()
                .id(productFaq.getId())
                .question(productFaq.getQuestion().toString())
                .answer(productFaq.getAnswer().toString())
                .creationDate(productFaq.getCreationDate().getValue())
                .lastModificationDate(productFaq.getLastModificationDate().getValue())
                .build();
    }

    @Schema(hidden = true)
    public static ProductFaqDto of(ProductFaqView view) {
        return ProductFaqDto.builder()
                .id(view.getId())
                .question(view.getQuestion())
                .answer(view.getAnswer())
                .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
                .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
                .build();
    }
}