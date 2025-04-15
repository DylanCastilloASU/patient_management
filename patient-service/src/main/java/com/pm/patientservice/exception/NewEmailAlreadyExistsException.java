package com.pm.patientservice.exception;

public class NewEmailAlreadyExistsException extends RuntimeException {
    public NewEmailAlreadyExistsException(String message) {
        super(message);
    }
}
