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
@Schema(name = "ProductFAQ with translations")
public final class ProductFaqWithTranslationDto implements Serializable {
    private Long id;
    private String question;
    private Map<String, String> questionTranslations;
    private String answer;
    private Map<String, String> answerTranslations;
    private ZonedDateTime creationDate;
    private ZonedDateTime lastModificationDate;

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