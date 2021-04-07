package com.example.jtron.utils.exceptions;

public class CommandNotImplementedException extends RuntimeException {

    public CommandNotImplementedException(String command) {
        super(String.format("Command '%s' not implemented", command));
    }

}
