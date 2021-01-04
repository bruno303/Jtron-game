package com.example.jtron.player;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public abstract class Player extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final String IMG_DIR = "img/";

	protected int backgroundGame;
	protected int player1;
	protected int player2;
	protected int posX;
	protected int posY;
	protected int posX2;
	protected int posY2;
	protected int[][] map = new int[640][640];
	protected String command;

	// Imagens
	protected Image player1Image;
	protected Image player2Image;
	protected Image stop;
	protected Image[] bg = new Image[3];
	protected Socket connectionSocket = null;
	protected DataOutputStream outCharacter = null;
	protected DataInputStream inCharacter = null;
	protected JFrame message = this;
	protected Field field;

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
				bg[player1] = ImageIO.read(getImageAsInputStream("jogador1.png"));
				player1Image = ImageIO.read(getImageAsInputStream("rastro.png"));
				bg[player2] = ImageIO.read(getImageAsInputStream("jogador2.png"));
				player2Image = ImageIO.read(getImageAsInputStream("rastro2.png"));
			} catch (final IOException e) {
				JOptionPane.showMessageDialog(this, "Nao foi possivel carregar as imagens!", "Erro",
						JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		}

		@Override
		public void paint(final Graphics g) {
			super.paint(g);
			g.drawImage(bg[backgroundGame], 0, 0, getSize().width, getSize().height, null);
			g.drawImage(bg[player1], posX, posY, null);
			g.drawImage(bg[player2], posX2, posY2, null);
		}

		@Override
		public Dimension getPreferredSize() {
			return (new Dimension(640, 640));
		}
	}

	protected abstract KeyAdapter getKeyAdapter();

	protected abstract void threadRead();

	private void loadVariablesWithDefaultValues() {
		backgroundGame = 2;
		player1 = 0;
		player2 = 1;
		posX = 20;
		posY = 295;
		posX2 = 600;
		posY2 = 295;
	}

	private void connectOnServer(final String ip, final int port) {
		try {
			connectionSocket = new Socket(ip, port);
			outCharacter = new DataOutputStream(connectionSocket.getOutputStream());
			inCharacter = new DataInputStream(connectionSocket.getInputStream());
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
		loadVariablesWithDefaultValues();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		field = new Field();
		add(field);
		connectOnServer(ip, port);

		// getKeyAdapter() é reescrito nas classes descentes,
		// onde é feita a validação das teclas do proprio jogador
		addKeyListener(getKeyAdapter());
		pack();
		setVisible(true);

		// threadRead() é reescrito nas classes descendentes,
		// onde é escrita a thread que recebe os comandos do outro jogador
		threadRead();
	}

	protected boolean verifyPlayersInsideDimension() {
		return posY > 15 && posY < 605 && posX > 15 && posX < 610 && posY2 > 15 && posY2 < 605 && posX2 > 15
				&& posX2 < 610 && map[posX][posY] != 1 && map[posX2][posY2] != 1;
	}

	protected void sendCommand(final String com) {
		try {
			outCharacter.writeUTF(com);
			outCharacter.flush();
		} catch (final Exception e) {
			//
		}
	}

	protected void setWay(final Image wayImage, final int posX, final int posY) {
		bg[backgroundGame].getGraphics().drawImage(wayImage, posX, posY, null);
		map[posX][posY] = 1;
	}
}
