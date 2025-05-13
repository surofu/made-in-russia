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

    @Column(nullable = false)
    private String browser;

    private SessionBrowser(String browser) {
        this.browser = browser;
    }

    public static SessionBrowser of(String browser) {
        return new SessionBrowser(browser);
    }
}
