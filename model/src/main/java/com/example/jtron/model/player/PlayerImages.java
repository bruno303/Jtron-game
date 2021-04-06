package com.example.jtron.model.player;

import java.io.Serializable;

public class PlayerImages implements Serializable {

    private final String backgroundCurrentPlayer;
    private final String currentPlayer;
    private final String backgroundEnemy;
    private final String enemyPlayer;
    private final String background;
    private final String stop;

    public PlayerImages(String backgroundCurrentPlayer, String currentPlayer, String backgroundEnemy, String enemyPlayer,
                        String background, String stop) {
        this.backgroundCurrentPlayer = backgroundCurrentPlayer;
        this.currentPlayer = currentPlayer;
        this.backgroundEnemy = backgroundEnemy;
        this.enemyPlayer = enemyPlayer;
        this.background = background;
        this.stop = stop;
    }

    public String getBackgroundCurrentPlayer() {
        return backgroundCurrentPlayer;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public String getBackgroundEnemy() {
        return backgroundEnemy;
    }

    public String getEnemyPlayer() {
        return enemyPlayer;
    }

    public String getBackground() {
        return background;
    }

    public String getStop() {
        return stop;
    }
}
