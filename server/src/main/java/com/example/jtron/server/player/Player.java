package com.example.jtron.server.player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.jtron.model.coordinate.Coordinate;
import com.example.jtron.model.exception.InvalidMessageException;
import com.example.jtron.model.message.Message;
import com.example.jtron.model.message.impl.DefaultMessage;
import com.example.jtron.model.message.impl.InitialIdMessage;
import com.example.jtron.model.message.impl.StartMessage;
import com.example.jtron.model.player.PlayerData;
import com.example.jtron.model.player.PlayerImages;

public class Player {

    private static final Logger LOGGER = LoggerFactory.getLogger(Player.class);

    private final int id;
    private Coordinate coordinate;
    private ObjectOutputStream sendCmd;
    private ObjectInputStream receiveCmd;
    private PlayerImages images;

    public Player(Socket socket, int id) {
        this.id = id;
        definePlayerData();
        try {
            this.sendCmd = new ObjectOutputStream(socket.getOutputStream());
            this.receiveCmd = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            LOGGER.error("Error when getting socket information.", e);
        }
    }

    private void definePlayerData() {
        switch (id) {
            case 0:
                coordinate = new Coordinate(id, 20, 295);
                images = new PlayerImages("jogador1.png",
                        "rastro.png",
                        "jogador2.png",
                        "rastro2.png",
                        "fundo.png",
                        "gameover.png");
                break;
            case 1:
                coordinate = new Coordinate(id, 600, 295);
                images = new PlayerImages("jogador2.png",
                        "rastro2.png",
                        "jogador1.png",
                        "rastro.png",
                        "fundo.png",
                        "gameover.png");
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
        final List<PlayerData> enemiesData = enemies.stream().map(Player::getPlayerData).collect(Collectors.toList());
        Message message = new StartMessage(id, getPlayerData(), enemiesData);
        sendCommand(message);
    }

    public PlayerData getPlayerData() {
        return new PlayerData(id, coordinate, images);
    }
}
