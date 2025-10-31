package com.surofu.exporteru.application.dto.faq;

import com.surofu.exporteru.core.model.faq.Faq;
import com.surofu.exporteru.infrastructure.persistence.faq.FaqView;
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
public final class FaqDto implements Serializable {

    private Long id;

    private String question;

    private String answer;

    private ZonedDateTime creationDate;

    private ZonedDateTime lastModificationDate;

    public static FaqDto of(FaqView view) {
        return FaqDto.builder()
                .id(view.getId())
                .question(view.getQuestion())
                .answer(view.getAnswer())
                .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
                .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
                .build();
    }

    public static FaqDto of(Faq faq) {
        return FaqDto.builder()
                .id(faq.getId())
                .question(faq.getQuestion().toString())
                .answer(faq.getAnswer().toString())
                .creationDate(faq.getCreationDate().getValue())
                .lastModificationDate(faq.getLastModificationDate().getValue())
                .build();
    }
}
