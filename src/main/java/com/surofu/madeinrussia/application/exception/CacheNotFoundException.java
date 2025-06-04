package com.surofu.madeinrussia.application.exception;

public class CacheNotFoundException extends RuntimeException {
  public CacheNotFoundException(String message) {
    super(message);
  }
}
