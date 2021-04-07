package com.example.jtron.model.exception;

public class PlayerNotFoundException extends RuntimeException {

    public PlayerNotFoundException(int id) {
        super("Player not found!" + id);
    }

}
