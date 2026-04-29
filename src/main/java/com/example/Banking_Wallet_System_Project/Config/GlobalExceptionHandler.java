package com.example.Banking_Wallet_System_Project.Config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler — catches RuntimeExceptions thrown anywhere
 * and returns a clean 400 response instead of a 500 Internal Server Error.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        System.err.println("Handled exception: " + e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        System.err.println("Unhandled exception: " + e.getMessage());
        return ResponseEntity.internalServerError().body("An internal error occurred. Please try again.");
    }
}