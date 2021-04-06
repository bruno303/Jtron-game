package com.example.jtron.model.message.impl;

import java.util.List;

import com.example.jtron.model.message.Message;
import com.example.jtron.utils.Constants;

public class StartMessage implements Message {

    private final int senderId;
    private final List<Integer> enemies;

    public StartMessage(int senderId, List<Integer> enemies) {
        this.senderId = senderId;
        this.enemies = enemies;
    }

    @Override
    public String getIdentifier() {
        return Constants.CMD_START;
    }

    @Override
    public int getSenderId() {
        return senderId;
    }

    public List<Integer> getEnemies() {
        return enemies;
    }
}
