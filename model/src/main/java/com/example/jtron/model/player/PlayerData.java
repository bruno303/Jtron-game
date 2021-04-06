package com.example.jtron.model.player;

import java.io.Serializable;

import com.example.jtron.model.coordinate.Coordinate;

public class PlayerData implements Serializable {

    private final int id;
    private final Coordinate coordinate;
    private final PlayerImages images;

    public PlayerData(int id, Coordinate coordinate, PlayerImages images) {
        this.id = id;
        this.coordinate = coordinate;
        this.images = images;
    }

    public PlayerImages getImages() {
        return images;
    }

    public int getId() {
        return id;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }
}
