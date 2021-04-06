package com.example.jtron.model.message;

import java.io.Serializable;

public interface Message extends Serializable {

    String getIdentifier();
    int getSenderId();

}
