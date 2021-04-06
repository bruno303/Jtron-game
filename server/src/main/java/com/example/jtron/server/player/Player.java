package com.example.jtron.server.player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.jtron.model.exception.InvalidMessageException;
import com.example.jtron.model.message.Message;
import com.example.jtron.model.message.impl.DefaultMessage;
import com.example.jtron.model.message.impl.InitialIdMessage;
import com.example.jtron.model.message.impl.StartMessage;
import com.example.jtron.server.map.Coordinate;

public class Player {

    private static final Logger LOGGER = LoggerFactory.getLogger(Player.class);

    private final int id;
    private Coordinate coordinate;
    private ObjectOutputStream sendCmd;
    private ObjectInputStream receiveCmd;

    public Player(Socket socket, int id) {
        this.id = id;
        defineCoordinate();
        try {
            this.sendCmd = new ObjectOutputStream(socket.getOutputStream());
            this.receiveCmd = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            LOGGER.error("Error when getting socket information.", e);
        }
    }

    private void defineCoordinate() {
        switch (id) {
            case 0:
                coordinate = new Coordinate(id, 20, 295);
                break;
            case 1:
                coordinate = new Coordinate(id, 600, 295);
                break;
            default:
                throw new RuntimeException("Invalid player id!" + id);
        }
    }

    public void sendCommand(Message msg) {
        try {
            sendCmd.writeObject(msg);
            LOGGER.debug("Command sent: {}", msg);
        } catch(Exception ignored) {
            //
        }
    }

    public void sendPlayerId() {
        try {
            Message msg = new InitialIdMessage(id);
            sendCmd.writeObject(msg);
        } catch (IOException ignored) {
            //
        }
    }

    public DefaultMessage readCommand() {
        try {
            final Object msgObj = receiveCmd.readObject();
            if (!(msgObj instanceof DefaultMessage)) {
                throw new InvalidMessageException(msgObj);
            }

            DefaultMessage msg = (DefaultMessage) msgObj;
            LOGGER.debug("Command received: {}", msg);
            return msg;
        } catch (IOException | ClassNotFoundException | InvalidMessageException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getId() {
        return this.id;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void sendStartInformation(List<Player> enemies) {
        final List<Integer> enemiesIds = enemies.stream().map(Player::getId).collect(Collectors.toList());
        Message message = new StartMessage(id, enemiesIds);
        sendCommand(message);
    }
}
