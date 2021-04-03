package com.example.jtron.utils;

public class SocketMessageUtils {

    private SocketMessageUtils() {}

    public static String messageToString(int playerIndex, Command command) {
        return messageToString(playerIndex, command.getValue());
    }

    public static String messageToString(int playerIndex, String command) {
        return String.format("%d|%s", playerIndex, command);
    }

    public static int getPlayerIndex(String message) {
        String indexStr = message.split("\\|")[0];
        return Integer.parseInt(indexStr);
    }

    public static Command getCommand(String message) {
        String commandStr = message.split("\\|")[1];
        return Command.parseCommand(commandStr);
    }
}
