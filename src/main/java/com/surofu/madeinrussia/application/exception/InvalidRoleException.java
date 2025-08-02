package com.surofu.madeinrussia.application.exception;

public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException(String roleName) {
        super(roleName);
    }
}
