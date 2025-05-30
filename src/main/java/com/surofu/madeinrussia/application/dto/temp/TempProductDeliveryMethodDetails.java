package com.surofu.madeinrussia.application.dto.temp;

import lombok.Getter;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public final class TempProductDeliveryMethodDetails implements Serializable {

    private final String name;
    private final String value;
    private final ZonedDateTime creationDate;
    private final ZonedDateTime lastModificationDate;

    public TempProductDeliveryMethodDetails() {
        name = List.of("Россия", "Китай").get(ThreadLocalRandom.current().nextInt(0, 1));
        value = List.of("2-4 недели", "1-2 мес.").get(ThreadLocalRandom.current().nextInt(0, 1));
        creationDate = ZonedDateTime.now();
        lastModificationDate = ZonedDateTime.now();
    }
}
