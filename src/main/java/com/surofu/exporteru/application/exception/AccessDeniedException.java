package com.surofu.exporteru.application.exception;

/**
 * Исключение для случаев отказа в доступе
 */
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}