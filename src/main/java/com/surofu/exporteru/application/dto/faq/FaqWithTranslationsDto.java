package com.surofu.exporteru.application.dto.faq;

import com.surofu.exporteru.infrastructure.persistence.faq.FaqWithTranslationsView;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class FaqWithTranslationsDto implements Serializable {

  private Long id;

  private String question;

  private Map<String, String> questionTranslations;

  private String answer;

  private Map<String, String> answerTranslations;

  private ZonedDateTime creationDate;

  private ZonedDateTime lastModificationDate;

  public static FaqWithTranslationsDto of(FaqWithTranslationsView view) {
    return FaqWithTranslationsDto.builder()
        .id(view.getId())
        .question(view.getQuestion())
        .questionTranslations(view.getQuestionTranslations())
        .answer(view.getAnswer())
        .answerTranslations(view.getAnswerTranslations())
        .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
        .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
        .build();
  }
}
