package com.example.jtron.model.message.impl;

import java.util.List;

import com.example.jtron.model.coordinate.Coordinate;
import com.example.jtron.model.message.Message;
import com.example.jtron.utils.Constants;

public class StartMessage implements Message {

    private final int senderId;
    private final Coordinate coordinate;
    private final List<Coordinate> enemiesCoordinates;

    public StartMessage(int senderId, Coordinate coordinate, List<Coordinate> enemiesCoordinates) {
        this.senderId = senderId;
        this.coordinate = coordinate;
        this.enemiesCoordinates = enemiesCoordinates;
    }

    @Override
    public String getIdentifier() {
        return Constants.CMD_START;
    }

    @Override
    public int getSenderId() {
        return senderId;
    }

    public List<Coordinate> getEnemiesCoordinates() {
        return enemiesCoordinates;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }
}
