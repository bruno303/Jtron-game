package com.example.jtron.server.player;

import com.example.jtron.server.map.Coordinate;
import com.example.jtron.server.map.GameMap;
import com.example.jtron.utils.Command;
import com.example.jtron.utils.Constants;
import com.example.jtron.utils.SocketMessageUtils;
import com.example.jtron.utils.ThreadUtils;

import java.util.List;
import java.util.Optional;

public class PlayerListener extends Thread {

    private final Player source;
    private final List<Player> targets;
    private final GameMap gameMap;

    public PlayerListener(Player source, List<Player> targets, GameMap gameMap) {
        this.source = source;
        this.targets = targets;
        this.gameMap = gameMap;
    }

    @Override
    public void run() {
        while (true) {
            String cmd = source.readCommand();
            processMessage(cmd);
            Optional<Command> responseOpt = validate();
            updateMap();
            targets.forEach(t -> t.sendCommand(cmd));

            responseOpt.ifPresent(commandResponse -> {
                final String response = SocketMessageUtils.messageToString(source.getId(), commandResponse);
                source.sendCommand(response);
                targets.forEach(t -> t.sendCommand(response));
            });

            ThreadUtils.sleep(5);
        }
    }

    private void updateMap() {
        Coordinate coordinate = source.getCoordinate();
        gameMap.updatePosition(coordinate.getPosX(), coordinate.getPosY());
    }

    private Optional<Command> validate() {
        if (!verifyPlayersInsideDimension()) {
            return Optional.of(Command.LOSE);
        }

        return Optional.empty();
    }

    private void processMessage(String message) {

        Command command = SocketMessageUtils.getCommand(message);
        Coordinate coordinate = source.getCoordinate();

        switch (command.getValue()) {
            case Constants.CMD_UP:
                coordinate.addInPosY(-10);
                break;
            case Constants.CMD_DOWN:
                coordinate.addInPosY(10);
                break;
            case Constants.CMD_LEFT:
                coordinate.addInPosX(-10);
                break;
            case Constants.CMD_RIGHT:
                coordinate.addInPosX(10);
                break;
            default:
                //
        }
    }

    private boolean verifyPlayersInsideDimension() {
        Coordinate cd = this.source.getCoordinate();
        return  cd.getPosY() > 15 &&
                cd.getPosY() < 605 &&
                cd.getPosX() > 15 &&
                cd.getPosX() < 610 &&
                gameMap.isPositionFree(cd.getPosX(), cd.getPosY());
    }
}
