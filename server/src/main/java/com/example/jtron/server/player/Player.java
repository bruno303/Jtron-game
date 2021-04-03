package com.example.jtron.server.player;

import com.example.jtron.server.map.Coordinate;
import com.example.jtron.utils.Command;
import com.example.jtron.utils.Constants;
import com.example.jtron.utils.SocketMessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Player {

    private static final Logger LOGGER = LoggerFactory.getLogger(Player.class);

    private final int id;
    private Coordinate coordinate;
    DataOutputStream sendCmd;
    DataInputStream receiveCmd;

    public Player(Socket socket, int id) {
        this.id = id;
        defineCoordinate();
        try {
            this.sendCmd = new DataOutputStream(socket.getOutputStream());
            this.receiveCmd = new DataInputStream(socket.getInputStream());
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

    public void sendCommand(String cmd) {
        try {
            sendCmd.writeUTF(cmd);
            LOGGER.debug("Command sent: {}", cmd);
        } catch(Exception ignored) {
            //
        }
    }

    public void sendPlayerId() {
        try {
            sendCmd.writeInt(id);
        } catch (IOException ignored) {
            //
        }
    }

    public String readCommand() {
        try {
            String cmd = receiveCmd.readUTF();
            LOGGER.debug("Command received: {}", cmd);
            return cmd;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public int getId() {
        return this.id;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void sendStartSignal() {
        String message = SocketMessageUtils.messageToString(id, Command.START);
        sendCommand(message);
    }
}
