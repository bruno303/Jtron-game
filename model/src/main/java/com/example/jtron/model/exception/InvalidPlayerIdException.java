package com.example.jtron.model.exception;

public class InvalidPlayerIdException extends RuntimeException {

    public InvalidPlayerIdException(int id) {
        super("Invalid player id!" + id);
    }

}
