package com.example.jtron.client.player;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.*;

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
import com.example.jtron.utils.Command;
import com.example.jtron.utils.Constants;
import com.example.jtron.utils.ThreadUtils;

public class Player extends JFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(Player.class);
    private static final String IMG_DIR = "img/";

    private int id = -1;
    private int enemyId = -1;

    private int backgroundGame;
    private PlayerData currentPlayerData;
    private List<PlayerData> enemiesData;
    private transient Coordinate coordinateCurrentPlayer;
    private transient List<Coordinate> coordinateEnemies;

    // Imagens
    private transient Image playerImage;
    private transient Image playerEnemyImage;
    private transient Image stop;
    private final transient Image[] bg = new Image[Constants.MAX_SIZE_PLAYERS + 1];
    private transient ObjectOutputStream outStream = null;
    private transient ObjectInputStream inStream = null;
    private boolean canProcessCommand = false;
    private final Field field;

    private class Field extends JPanel {
        private static final long serialVersionUID = 1L;

        private InputStream getImageAsInputStream(final String imgName) {
            return this.getClass().getClassLoader().getResourceAsStream(IMG_DIR + imgName);
        }

        public void loadImages(PlayerData playerData) {
            doLoadImages(playerData);
        }

        private void doLoadImages(PlayerData playerData) {
            try {
                final PlayerImages imgs = playerData.getImages();
                bg[backgroundGame] = ImageIO.read(getImageAsInputStream(imgs.getBackground()));
                stop = ImageIO.read(getImageAsInputStream(imgs.getStop()));
                bg[id] = ImageIO.read(getImageAsInputStream(imgs.getBackgroundCurrentPlayer()));
                playerImage = ImageIO.read(getImageAsInputStream(imgs.getCurrentPlayer()));
                bg[enemyId] = ImageIO.read(getImageAsInputStream(imgs.getBackgroundEnemy()));
                playerEnemyImage = ImageIO.read(getImageAsInputStream(imgs.getEnemyPlayer()));

            } catch (final IOException e) {
                LOGGER.error("Error loading game assets.", e);
                JOptionPane.showMessageDialog(this, "Error loading game assets!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }

        @Override
        public void paint(final Graphics g) {
            super.paint(g);
            g.drawImage(bg[backgroundGame], 0, 0, getSize().width, getSize().height, null);

            if (coordinateCurrentPlayer != null) {
                g.drawImage(bg[id], coordinateCurrentPlayer.getPosX(),
                        coordinateCurrentPlayer.getPosY(), null);
            }

            if (coordinateEnemies != null) {
                coordinateEnemies.forEach(ce -> g.drawImage(bg[enemyId], ce.getPosX(), ce.getPosY(), null));
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return (new Dimension(640, 640));
        }
    }

    private KeyAdapter getKeyAdapter() {
        return (new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {

                if (!canProcessCommand) {
                    e.consume();
                    return;
                }

                switch (Character.toLowerCase(e.getKeyChar())) {
                    case 'w':
                        handleKeyPressed(Command.UP, 0, -10);
                        break;
                    case 's':
                        handleKeyPressed(Command.DOWN, 0, 10);
                        break;
                    case 'a':
                        handleKeyPressed(Command.LEFT, -10, 0);
                        break;
                    case 'd':
                        handleKeyPressed(Command.RIGHT, 10, 0);
                        break;
                    default:
                        //
                }
                repaint();
            }
        });
    }

    private void handleKeyPressed(Command command, int addX, int addY) {
        setWay(playerImage, coordinateCurrentPlayer);
        coordinateCurrentPlayer.addInPosX(addX);
        coordinateCurrentPlayer.addInPosY(addY);
        sendCommand(command);
    }

    private void threadRead() {
        new Thread(() -> {
            while (true) {
                repaint();
                try {
                    final Object messageObj = inStream.readObject();
                    final Message message = (Message) messageObj;
                    LOGGER.debug("Message received: {}", message);

                    final int senderId = message.getSenderId();
                    final Command command = Command.parseCommand(message.getIdentifier());

                    if (command == Command.START) {
                        processStartCommand(message);
                        continue;
                    }

                    if (command == Command.UP) {
                        processCommandReceived(senderId, 0, -10);
                        continue;
                    }

                    if (command == Command.DOWN) {
                        processCommandReceived(senderId, 0, 10);
                        continue;
                    }

                    if (command == Command.LEFT) {
                        processCommandReceived(senderId, -10, 0);
                        continue;
                    }

                    if (command == Command.RIGHT) {
                        processCommandReceived(senderId, 10, 0);
                        continue;
                    }

                    if (command == Command.LOSE) {
                        processLoseCommand(senderId);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                ThreadUtils.sleep(5);
            }
        }).start();
    }

    private void processStartCommand(Message message) {
        if (!(message instanceof StartMessage)) {
            throw new InvalidMessageException(message);
        }

        StartMessage msg = (StartMessage) message;
        this.currentPlayerData = msg.getPlayerData();
        this.enemiesData = msg.getEnemiesData();
        this.coordinateEnemies = msg.getEnemiesData().stream().map(PlayerData::getCoordinate).collect(Collectors.toList());
        this.coordinateCurrentPlayer = msg.getPlayerData().getCoordinate();

        this.enemyId = this.coordinateEnemies.get(0).getId();

        this.field.loadImages(this.currentPlayerData);

        this.canProcessCommand = true;
    }

    private void processLoseCommand(int senderId) {
        try {
            bg[backgroundGame].getGraphics().drawImage(stop, 0, 0, null);
            repaint();
            inStream.close();
            outStream.close();
            String responseMessage = senderId == id ? "You lose!" : "You win!";
            JOptionPane.showMessageDialog(this, responseMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private void processCommandReceived(int senderId, int addInX, int addInY) {
        final Coordinate coord = getCoord(senderId);
        setWay(playerEnemyImage, coord);
        coord.addInPosX(addInX);
        coord.addInPosY(addInY);
        repaint();
    }

    private Coordinate getCoord(int id) {
        return this.coordinateEnemies.stream().filter(e -> e.getId() == id).findAny()
                .orElseThrow(() -> new RuntimeException("Player not found: " + id));
    }

    private void loadVariablesWithDefaultValues() {
        backgroundGame = Constants.MAX_SIZE_PLAYERS;
    }

    private void connectOnServer(final String ip, final int port) {
        try {
            Socket connectionSocket = new Socket(ip, port);
            outStream = new ObjectOutputStream(connectionSocket.getOutputStream());
            inStream = new ObjectInputStream(connectionSocket.getInputStream());
            final Object message = inStream.readObject();

            if (!(message instanceof InitialIdMessage)) {
                throw new InvalidMessageException(message);
            }

            InitialIdMessage msg = (InitialIdMessage) message;
            this.id = msg.getId();
            updateWindowTitle();

        } catch (final IOException | ClassNotFoundException | InvalidMessageException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void updateWindowTitle() {
        this.setTitle(String.format("%s - Player %d", this.getTitle(), id + 1));
    }

    public Player(final String title, final String ip, final int port) {
        super(title);
        setResizable(false);
        pack(); // avoid that setResizable change window's size
        connectOnServer(ip, port);
        loadVariablesWithDefaultValues();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.field = new Field();
        add(this.field);

        addKeyListener(getKeyAdapter());
        pack();
        setVisible(true);

        // Read commands from server
        threadRead();
    }

    protected void sendCommand(Command command) {
        try {
            Message message = new DefaultMessage(id, command.getValue());
            outStream.writeObject(message);
            outStream.flush();
        } catch (Exception ignored) {
            //
        }
    }

    protected void setWay(final Image wayImage, Coordinate coord) {
        bg[backgroundGame].getGraphics().drawImage(wayImage, coord.getPosX(), coord.getPosY(), null);
    }
}
