package com.surofu.madeinrussia.core.model.faq;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class FaqLastModificationDate implements Serializable {

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modification_date", nullable = false, columnDefinition = "timestamptz default now()")
    private ZonedDateTime value = ZonedDateTime.now();

    private FaqLastModificationDate(ZonedDateTime date) {
        this.value = Objects.requireNonNullElseGet(date, ZonedDateTime::now);
    }

    public static FaqLastModificationDate of(ZonedDateTime date) {
        return new FaqLastModificationDate(date);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
