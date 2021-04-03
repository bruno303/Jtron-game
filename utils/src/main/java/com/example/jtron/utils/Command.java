package com.example.jtron.utils;

import java.util.stream.Stream;

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
                .orElseThrow(() -> new RuntimeException(String.format("Command %s not implemented", value)));
    }

    public String getValue() {
        return this.value;
    }
}
