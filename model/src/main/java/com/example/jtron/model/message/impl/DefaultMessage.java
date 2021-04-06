package com.example.jtron.model.message.impl;

import com.example.jtron.model.message.Message;

public class DefaultMessage implements Message {

    private final int senderId;
    private String command;

    public DefaultMessage(int senderId, String identifier) {
        this.senderId = senderId;
        this.command = identifier;
    }

    @Override
    public String getIdentifier() {
        return command;
    }

    @Override
    public int getSenderId() {
        return senderId;
    }
}
