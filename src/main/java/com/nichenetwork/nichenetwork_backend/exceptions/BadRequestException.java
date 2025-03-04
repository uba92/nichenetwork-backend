package com.nichenetwork.nichenetwork_backend.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);  // Passa il messaggio al costruttore della classe base
    }
}