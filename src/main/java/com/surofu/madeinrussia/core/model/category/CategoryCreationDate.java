package com.surofu.madeinrussia.core.model.category;

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

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class CategoryCreationDate implements Serializable {

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", nullable = false, updatable = false, columnDefinition = "timestamptz default now()")
    private ZonedDateTime value = ZonedDateTime.now();

    private CategoryCreationDate(ZonedDateTime date) {
        if (date == null) {
            throw new IllegalArgumentException("Дата создания категории не может быть пустой");
        }

        this.value = date;
    }

    public static CategoryCreationDate of(ZonedDateTime date) {
        return new CategoryCreationDate(date);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
