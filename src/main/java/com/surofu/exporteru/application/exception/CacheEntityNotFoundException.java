package com.surofu.exporteru.application.exception;

public class CacheEntityNotFoundException extends RuntimeException {
    public CacheEntityNotFoundException() {
        super("Cache entity not found");
    }
}
