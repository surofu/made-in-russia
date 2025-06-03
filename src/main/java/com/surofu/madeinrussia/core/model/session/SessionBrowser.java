package com.surofu.madeinrussia.core.model.session;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class SessionBrowser implements Serializable {

    @Column(name = "browser", nullable = false, updatable = false)
    private String value;

    private SessionBrowser(String browser) {
        this.value = browser;
    }

    public static SessionBrowser of(String browser) {
        return new SessionBrowser(browser);
    }

    @Override
    public String toString() {
        return value;
    }
}
