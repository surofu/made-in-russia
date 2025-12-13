package com.surofu.exporteru.application.dto.product;

import com.surofu.exporteru.core.model.product.faq.ProductFaq;
import com.surofu.exporteru.infrastructure.persistence.product.faq.ProductFaqView;
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
@Schema(name = "ProductFAQ")
public final class ProductFaqDto implements Serializable {
    private Long id;
    private String question;
    private String answer;
    private ZonedDateTime creationDate;
    private ZonedDateTime lastModificationDate;

    public static ProductFaqDto of(ProductFaq productFaq) {
        return ProductFaqDto.builder()
                .id(productFaq.getId())
                .question(productFaq.getQuestion().getLocalizedValue())
                .answer(productFaq.getAnswer().getLocalizedValue())
                .creationDate(productFaq.getCreationDate().getValue())
                .lastModificationDate(productFaq.getLastModificationDate().getValue())
                .build();
    }

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