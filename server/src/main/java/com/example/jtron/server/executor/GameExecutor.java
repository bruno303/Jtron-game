package com.example.jtron.server.executor;

import com.example.jtron.server.map.GameMap;
import com.example.jtron.server.player.Player;
import com.example.jtron.server.player.PlayerListener;
import com.example.jtron.server.player.ServerClientCollection;

import java.util.List;

public class GameExecutor {
    private final Player player1;
    private final Player player2;
    private final GameMap gameMap;

    public GameExecutor(ServerClientCollection clients){
        player1 = clients.getById(0);
        player2 = clients.getById(1);
        this.gameMap = new GameMap();
    }

    public void run() {
        new PlayerListener(player1, List.of(player2), gameMap).start();
        new PlayerListener(player2, List.of(player1), gameMap).start();
        startClients();
    }

    private void startClients() {
        player1.sendStartInformation(List.of(player2));
        player2.sendStartInformation(List.of(player1));
    }
}

