package com.example.jtron.model.exception;

public class InvalidMessageException extends RuntimeException {

    private final Object objectMsg;

    public InvalidMessageException(Object objectMsg) {
        this.objectMsg = objectMsg;
    }

    public Object getObjectMsg() {
        return objectMsg;
    }
}
