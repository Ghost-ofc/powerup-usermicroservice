package com.pragma.powerup.domain.exception;

public class InvalidDocumentException extends DomainException {
    public InvalidDocumentException() {
        super("The identity document must contain only numeric digits.");
    }
}
