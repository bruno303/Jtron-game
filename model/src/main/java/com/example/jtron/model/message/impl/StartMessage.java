package com.example.jtron.model.message.impl;

import java.util.List;

import com.example.jtron.model.message.Message;
import com.example.jtron.model.player.PlayerData;
import com.example.jtron.utils.Constants;

public class StartMessage implements Message {

    private final int senderId;
    private final PlayerData playerData;
    private final List<PlayerData> enemiesData;

    public StartMessage(int senderId, PlayerData playerData, List<PlayerData> enemiesData) {
        this.senderId = senderId;
        this.playerData = playerData;
        this.enemiesData = enemiesData;
    }

    @Override
    public String getIdentifier() {
        return Constants.CMD_START;
    }

    @Override
    public int getSenderId() {
        return senderId;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public List<PlayerData> getEnemiesData() {
        return enemiesData;
    }
}
