package com.example.jtron.client.player;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.jtron.model.exception.InvalidMessageException;
import com.example.jtron.model.message.Message;
import com.example.jtron.model.message.impl.DefaultMessage;
import com.example.jtron.model.message.impl.InitialIdMessage;
import com.example.jtron.utils.Command;
import com.example.jtron.utils.Constants;
import com.example.jtron.utils.ThreadUtils;

public class Player extends JFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(Player.class);
    private static final String IMG_DIR = "img/";

    private int id = 0;
    private int enemyId = 0;

    private int backgroundGame;
    private transient Coordinate coordinateCurrentPlayer;
    private final List<Coordinate> coordinateEnemies = new ArrayList<>();

    // Imagens
    private transient Image playerImage;
    private transient Image playerEnemyImage;
    private transient Image stop;
    private transient final Image[] bg = new Image[Constants.MAX_SIZE_PLAYERS + 1];
    private transient ObjectOutputStream outStream = null;
    private transient ObjectInputStream inStream = null;
    private boolean canProcessCommand = false;

    private class Field extends JPanel {
        private static final long serialVersionUID = 1L;

        Field() {
            loadImages();
        }

        private InputStream getImageAsInputStream(final String imgName) {
            return this.getClass().getClassLoader().getResourceAsStream(IMG_DIR + imgName);
        }

        private void loadImages() {
            try {
                bg[backgroundGame] = ImageIO.read(getImageAsInputStream("fundo.png"));
                stop = ImageIO.read(getImageAsInputStream("gameover.png"));

                if (id == 0) {
                    bg[id] = ImageIO.read(getImageAsInputStream("jogador1.png"));
                    playerImage = ImageIO.read(getImageAsInputStream("rastro.png"));
                    bg[enemyId] = ImageIO.read(getImageAsInputStream("jogador2.png"));
                    playerEnemyImage = ImageIO.read(getImageAsInputStream("rastro2.png"));
                    return;
                }

                bg[id] = ImageIO.read(getImageAsInputStream("jogador2.png"));
                playerImage = ImageIO.read(getImageAsInputStream("rastro2.png"));
                bg[enemyId] = ImageIO.read(getImageAsInputStream("jogador1.png"));
                playerEnemyImage = ImageIO.read(getImageAsInputStream("rastro.png"));

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
            g.drawImage(bg[id], coordinateCurrentPlayer.getPosX(),
                    coordinateCurrentPlayer.getPosY(), null);

            coordinateEnemies.forEach(ce -> g.drawImage(bg[enemyId], ce.getPosX(), ce.getPosY(), null));
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
                        setWay(playerImage, coordinateCurrentPlayer);
                        coordinateCurrentPlayer.addInPosY(-10);
                        sendCommand(Command.UP);
                        repaint();
                        break;
                    case 's':
                        setWay(playerImage, coordinateCurrentPlayer);
                        coordinateCurrentPlayer.addInPosY(10);
                        sendCommand(Command.DOWN);
                        repaint();
                        break;
                    case 'a':
                        setWay(playerImage, coordinateCurrentPlayer);
                        coordinateCurrentPlayer.addInPosX(-10);
                        sendCommand(Command.LEFT);
                        repaint();
                        break;
                    case 'd':
                        setWay(playerImage, coordinateCurrentPlayer);
                        coordinateCurrentPlayer.addInPosX(+10);
                        sendCommand(Command.RIGHT);
                        repaint();
                        break;
                    default:
                        //
                }
                repaint();
            }
        });
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
                        this.canProcessCommand = true;
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

        if (id == 0) {
            coordinateCurrentPlayer = new Coordinate(id, 20, 295);
            coordinateEnemies.add(new Coordinate(enemyId, 600, 295));
            return;
        }

        coordinateCurrentPlayer = new Coordinate(id, 600, 295);
        coordinateEnemies.add(new Coordinate(enemyId, 20, 295));
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
            this.enemyId = this.id == 0 ? 1 : 0;

        } catch (final IOException | ClassNotFoundException | InvalidMessageException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public Player(final String title, final String ip, final int port) {
        super(title);
        setResizable(false);
        pack(); // Esse pack() é para corrigir o bug do setResizable alterar o tamanho da janela
        // às vezes
        connectOnServer(ip, port);
        loadVariablesWithDefaultValues();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Field field = new Field();
        add(field);

        addKeyListener(getKeyAdapter());
        pack();
        setVisible(true);

        // Read commands from other players
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
