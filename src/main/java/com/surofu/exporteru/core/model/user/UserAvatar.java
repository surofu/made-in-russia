package com.surofu.exporteru.core.model.user;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class UserAvatar implements Serializable {

    @Column(name = "avatar_url", columnDefinition = "text")
    private String url;

    private UserAvatar(String url) {
        if (url != null && url.length() > 20_000) {
            throw new LocalizedValidationException("validation.avatar_url.max_length");
        }

        this.url = url;
    }

    public static UserAvatar of(String url) {
        return new UserAvatar(url);
    }

    @Override
    public String toString() {
        return url;
    }
}
