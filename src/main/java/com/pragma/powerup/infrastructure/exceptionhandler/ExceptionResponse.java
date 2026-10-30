package com.pragma.powerup.infrastructure.exceptionhandler;

public enum ExceptionResponse {
    NO_DATA_FOUND("No data found for the requested petition"),
    UNDERAGE_USER("The user must be of legal age (18 years or older)"),
    INVALID_PHONE("The phone number must contain up to 13 digits and can optionally start with '+'"),
    INVALID_DOCUMENT("The identity document must contain only numeric digits"),
    INVALID_EMAIL("The provided email format is invalid"),
    USER_ALREADY_EXISTS("A user with this email or identity document already exists"),
    ROLE_NOT_FOUND("The specified role was not found"),
    ACCESS_DENIED("Access denied: You do not have permissions for this action"),
    UNAUTHORIZED("Authentication failed or token invalid");

    private final String message;

    ExceptionResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
