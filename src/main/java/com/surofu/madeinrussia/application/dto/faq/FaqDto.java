package com.surofu.madeinrussia.application.dto.faq;

import com.surofu.madeinrussia.infrastructure.persistence.faq.FaqView;
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
}
