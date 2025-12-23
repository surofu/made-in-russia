package com.surofu.exporteru.core.model.user;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
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

    public UserAvatar(String url) {
        if (url != null && url.length() > 20_000) {
            throw new LocalizedValidationException("validation.avatar_url.max_length");
        }
        this.url = url;
    }
    @Override
    public String toString() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserAvatar that)) {
            return false;
        }
      return Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(url);
    }
}
