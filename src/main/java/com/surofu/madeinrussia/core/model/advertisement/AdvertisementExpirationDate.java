package com.surofu.madeinrussia.core.model.advertisement;

import com.surofu.madeinrussia.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class AdvertisementExpirationDate implements Serializable {

    @Column(name = "expiration_date")
    private ZonedDateTime value;

    private AdvertisementExpirationDate(ZonedDateTime date) {
        if (date == null) {
            throw new LocalizedValidationException("validation.advertisement.expiration_date.empty");
        }

        this.value = date;
    }

    public static AdvertisementExpirationDate of(ZonedDateTime date) {
        return new AdvertisementExpirationDate(date);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
