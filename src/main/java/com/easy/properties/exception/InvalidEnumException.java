package com.easy.properties.exception;

public class InvalidEnumException extends RuntimeException{

    public InvalidEnumException(String message) {
        super(message);
    }

    public InvalidEnumException(String message, Throwable cause) {
        super(message, cause);
    }
}
