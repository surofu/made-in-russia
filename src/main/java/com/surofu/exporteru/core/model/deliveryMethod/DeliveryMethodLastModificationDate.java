package com.surofu.exporteru.core.model.deliveryMethod;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class DeliveryMethodLastModificationDate implements Serializable {

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modification_date", nullable = false, columnDefinition = "timestamptz default now()")
    private ZonedDateTime value = ZonedDateTime.now();

    private DeliveryMethodLastModificationDate(ZonedDateTime date) {
        this.value = Objects.requireNonNullElseGet(date, ZonedDateTime::now);
    }

    public static DeliveryMethodLastModificationDate of(ZonedDateTime date) {
        return new DeliveryMethodLastModificationDate(date);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
