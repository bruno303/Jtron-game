package com.example.jtron.server.player;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerClientCollection {
    private final List<Player> clients = new ArrayList<>();
    private final int maxSize;

    public ServerClientCollection(int maxSize) {
        this.maxSize = maxSize;
    }

    public boolean canAddPlayer() {
        return clients.size() < maxSize;
    }

    public Player addClient(Socket client) {
        if (canAddPlayer()) {
            Player player = new Player(client, getNextIndex());
            clients.add(player);
            return player;
        }

        throw new RuntimeException(String.format("Reached max clients size: %d", maxSize));
    }

    private short getNextIndex() {
        return (short) clients.size();
    }

    public Player getById(int id) {
        return this.clients.stream().filter(c -> c.getId() == id)
                .findAny()
                .orElseThrow(() -> new RuntimeException("Player not found!" + id));
    }
}
