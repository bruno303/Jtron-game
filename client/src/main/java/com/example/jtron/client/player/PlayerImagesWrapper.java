package com.example.jtron.client.player;

import java.awt.*;

public class PlayerImagesWrapper {
    private final int idPlayer;
    private final Image imgPlayer;
    private final Image imgPlayerPath;

    public PlayerImagesWrapper(int idPlayer, Image imgPlayer, Image imgPlayerPath) {
        this.idPlayer = idPlayer;
        this.imgPlayer = imgPlayer;
        this.imgPlayerPath = imgPlayerPath;
    }

    public int getIdPlayer() {
        return idPlayer;
    }

    public Image getImgPlayer() {
        return imgPlayer;
    }

    public Image getImgPlayerPath() {
        return imgPlayerPath;
    }
}
