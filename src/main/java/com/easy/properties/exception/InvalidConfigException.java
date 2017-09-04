package com.easy.properties.exception;

public class InvalidConfigException extends RuntimeException{

    public InvalidConfigException(String message) {
        super(message);
    }

    public InvalidConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
