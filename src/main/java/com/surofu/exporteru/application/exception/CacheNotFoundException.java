package com.surofu.exporteru.application.exception;

import java.util.concurrent.CompletionException;

public class CacheNotFoundException extends CompletionException {
    private CacheNotFoundException(String cacheName) {
        super(String.format("Error while sending email. Cache with name '%s' not found", cacheName));
    }

    public static CacheNotFoundException of(String cacheName) {
        return new CacheNotFoundException(cacheName);
    }
}
