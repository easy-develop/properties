package com.easy.properties.exception;

public class EnumMissingOptionalFieldException extends RuntimeException{

    public EnumMissingOptionalFieldException(String message) {
        super(message);
    }

    public EnumMissingOptionalFieldException(String message, Throwable cause) {
        super(message, cause);
    }
}
