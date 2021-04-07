package com.example.jtron.utils;

import java.util.stream.Stream;

import com.example.jtron.utils.exceptions.CommandNotImplementedException;

public enum Command {

    UP(Constants.CMD_UP),
    DOWN(Constants.CMD_DOWN),
    LEFT(Constants.CMD_LEFT),
    RIGHT(Constants.CMD_RIGHT),
    LOSE(Constants.CMD_LOSE),
    START(Constants.CMD_START);

    private final String value;

    Command(String value) {
        this.value= value;
    }

    public static Command parseCommand(String value) {
        return Stream.of(Command.values())
                .filter(c -> c.value.equals(value))
                .findAny()
                .orElseThrow(() -> new CommandNotImplementedException(value));
    }

    public String getValue() {
        return this.value;
    }
}
