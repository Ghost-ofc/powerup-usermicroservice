package com.pragma.powerup.domain.exception;

public class InvalidEmailException extends DomainException {
    public InvalidEmailException() {
        super("The provided email format is invalid.");
    }
}
