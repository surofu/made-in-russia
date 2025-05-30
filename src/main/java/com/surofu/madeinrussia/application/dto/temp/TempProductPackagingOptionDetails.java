package com.surofu.madeinrussia.application.dto.temp;

import lombok.Getter;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public final class TempProductPackagingOptionDetails implements Serializable {

    private final String name;
    private final ZonedDateTime creationDate;
    private final ZonedDateTime lastModificationDate;

    public TempProductPackagingOptionDetails() {
        name = List.of("Паллеты", "Коробки", "Мешок", "Пластиковый ящик", "Металлический контейнер").get(ThreadLocalRandom.current().nextInt(0, 4));
        creationDate = ZonedDateTime.now();
        lastModificationDate = ZonedDateTime.now();
    }
}
