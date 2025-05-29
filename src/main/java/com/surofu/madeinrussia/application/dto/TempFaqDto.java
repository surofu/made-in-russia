package com.surofu.madeinrussia.application.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Random;

@Data
public class TempFaqDto implements Serializable {

    private Long id = new Random().nextLong();

    private String question = "Как добавить медиафайлы к отзыву на товар?";

    private String answer = "Вы можете прикрепить фото или видео к своему отзыву при его написании или редактировании.";

    private ZonedDateTime creationDate = ZonedDateTime.now();

    private ZonedDateTime lastModificationDate = ZonedDateTime.now();
}
