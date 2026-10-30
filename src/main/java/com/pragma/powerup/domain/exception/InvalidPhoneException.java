package com.pragma.powerup.domain.exception;

public class InvalidPhoneException extends DomainException {
    public InvalidPhoneException() {
        super("The phone number must contain up to 13 digits and may include '+' prefix.");
    }
}
