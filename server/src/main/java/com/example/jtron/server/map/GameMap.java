package com.example.jtron.server.map;

public class GameMap {

    private final int[][] map = new int[640][640];

    public synchronized void updatePosition(int posX, int posY) {
        map[posX][posY] = 1;
    }

    public synchronized boolean isPositionFree(int posX, int posY) {
        return map[posX][posY] != 1;
    }
}
