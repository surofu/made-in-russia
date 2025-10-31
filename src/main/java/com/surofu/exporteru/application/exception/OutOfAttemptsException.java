package com.surofu.exporteru.application.exception;

public class OutOfAttemptsException extends RuntimeException {
    public OutOfAttemptsException() {
        super("Out of attempts");
    }
}
