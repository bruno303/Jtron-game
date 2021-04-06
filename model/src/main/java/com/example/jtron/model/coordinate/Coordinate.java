package com.example.jtron.model.coordinate;

import java.io.Serializable;

public class Coordinate implements Serializable {

    private final int id;
    private int posX;
    private int posY;

    public Coordinate(int id, int posX, int posY) {
        this.id = id;
        this.posX = posX;
        this.posY = posY;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getId() {
        return this.id;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void addInPosX(int posXIncrement) {
        this.setPosX(this.posX + posXIncrement);
    }

    public void addInPosY(int posYIncrement) {
        this.setPosY(this.posY + posYIncrement);
    }
}
