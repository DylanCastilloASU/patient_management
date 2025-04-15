package com.pm.patientservice.exception;

import com.pm.patientservice.dto.PatientResponseDTOClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //handles things that have failed the @Valid annotation
    //@Valid comes from jakarta.validation-api-3.0.2.jar and uses the constraints
    @ExceptionHandler(MethodArgumentNotValidException.class)
    //                   FIELD ERROR, ERROR Message
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(
            EmailAlreadyExistsException ex
    ) {
        log.warn("Email already exists warning: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("email", "An account with that email already exists");
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(InvalidIdException.class)
    public ResponseEntity<Map<String, String>> handleInvalidIdException(
            InvalidIdException ex
    ) {
        log.warn("Invalid id warning : {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("id", "Invalid id");
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<Map<String, String>> handleInvalidEmailException(
            InvalidEmailException ex
    ) {
        log.warn("Invalid email warning : {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("email", "Invalid email");
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePatientNotFoundException(
            PatientNotFoundException ex
    ) {
        log.warn("Patient not found warning : {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("patient", "Patient not found");
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(NewEmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleNewEmailAlreadyExistsException(
            NewEmailAlreadyExistsException ex
    ) {
        log.warn("New email already exists warning: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("email", "New email already has an account");
        return ResponseEntity.badRequest().body(errors);
    }

}
