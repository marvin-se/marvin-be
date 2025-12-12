package com.marvin.campustrade.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handleEmailExists(EmailAlreadyExistsException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(UniversityNotFoundException.class)
    public ResponseEntity<String> handleUniversityNotFound(UniversityNotFoundException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(InvalidStudentEmailDomainException.class)
    public ResponseEntity<String> handleInvalidStudentEmailDomain(InvalidStudentEmailDomainException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationErrors(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        return ResponseEntity.badRequest().body(message);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleDeserializationErrors(HttpMessageNotReadableException ex) {

        Throwable root = ex.getRootCause();

        if (root instanceof InvalidFormatException invalid) {
            RuntimeException custom = DeserializationErrorResolver.resolve(invalid);
            return ResponseEntity.badRequest().body(custom.getMessage());
        }

        return ResponseEntity.badRequest().body("Malformed request body.");
    }

    @ExceptionHandler(InvalidEnumValueException.class)
    public ResponseEntity<String> handleInvalidEnum(InvalidEnumValueException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(InvalidNumberFormatException.class)
    public ResponseEntity<String> handleInvalidNumber(InvalidNumberFormatException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(InvalidRequestFieldException.class)
    public ResponseEntity<String> handleInvalidField(InvalidRequestFieldException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
