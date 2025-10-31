package com.surofu.exporteru.application.exception;

public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException(String roleName) {
        super(roleName);
    }
}
