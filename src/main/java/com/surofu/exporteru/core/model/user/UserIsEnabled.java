package com.surofu.exporteru.core.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class UserIsEnabled implements Serializable {

    @Column(name = "is_enabled", nullable = false, columnDefinition = "bool default true")
    private Boolean value = true;

    private UserIsEnabled(Boolean state) {
        this.value = state;
    }

    public static UserIsEnabled of(Boolean state) {
        return new UserIsEnabled(state != null && state);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
