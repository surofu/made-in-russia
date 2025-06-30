package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.faq.Faq;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class FaqDto implements Serializable {

    private Long id;

    private String question;

    private String answer;

    private ZonedDateTime creationDate;

    private ZonedDateTime lastModificationDate;

    public static FaqDto of(Faq faq) {
        return FaqDto.builder()
                .id(faq.getId())
                .question(faq.getQuestion().getValue())
                .answer(faq.getAnswer().getValue())
                .creationDate(faq.getCreationDate().getValue())
                .lastModificationDate(faq.getLastModificationDate().getValue())
                .build();
    }
}
