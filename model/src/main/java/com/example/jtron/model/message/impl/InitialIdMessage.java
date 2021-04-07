package com.example.jtron.model.message.impl;

import com.example.jtron.model.message.Message;
import com.example.jtron.utils.Constants;

public class InitialIdMessage implements Message {

    private final int id;

    public InitialIdMessage(int id) {
        this.id = id;
    }

    @Override
    public String getIdentifier() {
        return Constants.CMD_ID;
    }

    @Override
    public int getSenderId() {
        return id;
    }
}
