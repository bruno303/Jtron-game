package com.example.jtron.client.player;

import com.example.jtron.utils.Command;
import com.example.jtron.utils.Constants;
import com.example.jtron.utils.SocketMessageUtils;
import com.example.jtron.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
    private transient DataOutputStream outCharacter = null;
    private transient DataInputStream inCharacter = null;
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
                    final String message = inCharacter.readUTF();
                    LOGGER.debug("Message received: {}", message);

                    final int senderId = SocketMessageUtils.getPlayerIndex(message);
                    final Command command = SocketMessageUtils.getCommand(message);
                    Coordinate enemyCoord = getCorrectEnemy(senderId);

                    if (command == Command.UP) {
                        setWay(playerEnemyImage, enemyCoord);
                        enemyCoord.addInPosY(-10);
                        repaint();
                    } else if (command == Command.DOWN) {
                        setWay(playerEnemyImage, enemyCoord);
                        enemyCoord.addInPosY(10);
                        repaint();
                    } else if (command == Command.LEFT) {
                        setWay(playerEnemyImage, enemyCoord);
                        enemyCoord.addInPosX(-10);
                        repaint();
                    } else if (command == Command.RIGHT) {
                        setWay(playerEnemyImage, enemyCoord);
                        enemyCoord.addInPosX(10);
                        repaint();
                    } else if (command == Command.START) {
                        this.canProcessCommand = true;
                    } else if (command == Command.LOSE) {
                        bg[backgroundGame].getGraphics().drawImage(stop, 0, 0, null);
                        repaint();
                        inCharacter.close();
                        String responseMessage = senderId == id ? "You lose!" : "You win!";
                        JOptionPane.showMessageDialog(this, responseMessage);
                        System.exit(0);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ThreadUtils.sleep(5);
            }
        }).start();
    }

    private Coordinate getCorrectEnemy(int id) {
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
            outCharacter = new DataOutputStream(connectionSocket.getOutputStream());
            inCharacter = new DataInputStream(connectionSocket.getInputStream());
            this.id = inCharacter.readInt();
            this.enemyId = this.id == 0 ? 1 : 0;
        } catch (final IOException e) {
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
            String message = SocketMessageUtils.messageToString(id, command);
            outCharacter.writeUTF(message);
            outCharacter.flush();
        } catch (Exception ignored) {
            //
        }
    }

    protected void setWay(final Image wayImage, Coordinate coord) {
        bg[backgroundGame].getGraphics().drawImage(wayImage, coord.getPosX(), coord.getPosY(), null);
    }
}
