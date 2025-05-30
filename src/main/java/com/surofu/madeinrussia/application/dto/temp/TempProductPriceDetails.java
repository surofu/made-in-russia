package com.surofu.madeinrussia.application.dto.temp;

import lombok.Getter;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public final class TempProductPriceDetails implements Serializable {

    private final Integer from;
    private final Integer to;
    private final String currency;
    private final String unit;
    private final Double originalPrice;
    private final Double discount;
    private final Double discountedPrice;
    private final Double minimumOrderQuantity;
    private final ZonedDateTime discountExpiryDate;
    private final ZonedDateTime creationDate;
    private final ZonedDateTime lastModificationDate;

    public TempProductPriceDetails(Integer from, Integer to) {
        this.from = from;
        this.to = to;
        currency = List.of("USD", "EUR", "CNY").get(ThreadLocalRandom.current().nextInt(0, 2));
        unit = List.of("т.", "кг.", "㎡").get(ThreadLocalRandom.current().nextInt(0, 2));
        originalPrice = (double) ThreadLocalRandom.current().nextInt(1, 1000) * 10;
        discount = (double) ThreadLocalRandom.current().nextInt(1, 6) * 10;
        discountedPrice = originalPrice * (discount / 100);
        minimumOrderQuantity = (double) ThreadLocalRandom.current().nextInt(1, 100);
        discountExpiryDate = ZonedDateTime.now().plusDays(ThreadLocalRandom.current().nextInt(1, 30));
        creationDate = ZonedDateTime.now();
        lastModificationDate = ZonedDateTime.now();
    }
}
