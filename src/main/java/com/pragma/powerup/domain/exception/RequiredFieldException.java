package com.pragma.powerup.domain.exception;

public class RequiredFieldException extends DomainException {
    public RequiredFieldException(String field) {
        super("The field '" + field + "' is mandatory.");
    }
}
