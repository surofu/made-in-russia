package com.surofu.exporteru.core.model.deliveryMethod;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class DeliveryMethodCreationDate implements Serializable {

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", nullable = false, updatable = false, columnDefinition = "timestamptz default now()")
    private ZonedDateTime value = ZonedDateTime.now();

    private DeliveryMethodCreationDate(ZonedDateTime date) {
        this.value = Objects.requireNonNullElseGet(date, ZonedDateTime::now);
    }

    public static DeliveryMethodCreationDate of(ZonedDateTime date) {
        return new DeliveryMethodCreationDate(date);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
