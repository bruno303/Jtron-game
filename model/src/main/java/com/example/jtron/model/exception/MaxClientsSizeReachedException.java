package com.example.jtron.model.exception;

public class MaxClientsSizeReachedException extends RuntimeException {

    public MaxClientsSizeReachedException(int maxSize) {
        super(String.format("Reached max clients size: %d", maxSize));
    }

}
