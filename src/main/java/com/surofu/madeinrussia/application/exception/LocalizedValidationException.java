package com.surofu.madeinrussia.application.exception;

import lombok.Getter;

@Getter
public class LocalizedValidationException extends RuntimeException {
    private final Object[] values;

    public LocalizedValidationException(String messageCode, Object... values) {
        super(messageCode);
        this.values = values;
    }

    public LocalizedValidationException(String messageCode) {
        super(messageCode);
        this.values = new String[0];
    }
}
