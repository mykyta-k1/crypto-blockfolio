package com.crypto.blockfolio.persistence.exception;

public class JsonFileIOException extends RuntimeException {

    public JsonFileIOException(String message) {
        super(message);
    }

    public JsonFileIOException(String message, Throwable cause) {
        super(message, cause);
    }
}
