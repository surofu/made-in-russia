package com.surofu.madeinrussia.application.exception;

public class LocalizedValidationException extends RuntimeException {

    public LocalizedValidationException(String messageCode) {
        super(messageCode);
    }
}
