package com.surofu.exporteru.application.exception;

public class EmptyTranslationException extends RuntimeException {
    public EmptyTranslationException(String moduleName) {
        super(moduleName);
    }

    public EmptyTranslationException() {
        super("Empty translation");
    }
}
