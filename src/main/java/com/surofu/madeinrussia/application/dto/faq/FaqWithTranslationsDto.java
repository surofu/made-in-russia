package com.surofu.madeinrussia.application.dto.faq;

import com.surofu.madeinrussia.application.dto.translation.TranslationDto;
import com.surofu.madeinrussia.application.utils.HstoreParser;
import com.surofu.madeinrussia.infrastructure.persistence.faq.FaqWithTranslationsView;
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
public final class FaqWithTranslationsDto implements Serializable {

    private Long id;

    private String question;

    private TranslationDto questionTranslations;

    private String answer;

    private TranslationDto answerTranslations;

    private ZonedDateTime creationDate;

    private ZonedDateTime lastModificationDate;

    public static FaqWithTranslationsDto of(FaqWithTranslationsView view) {
        return FaqWithTranslationsDto.builder()
                .id(view.getId())
                .question(view.getQuestion())
                .questionTranslations(TranslationDto.of(HstoreParser.fromString(view.getQuestionTranslations())))
                .answer(view.getAnswer())
                .answerTranslations(TranslationDto.of(HstoreParser.fromString(view.getAnswerTranslations())))
                .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
                .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
                .build();
    }
}
