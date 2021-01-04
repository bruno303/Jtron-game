package com.example.jtron.player;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JOptionPane;

public class Player2 extends Player {

	private static final long serialVersionUID = 1L;

	public static void createPlayer2(final String ip, final int port) {
		new Player2(ip, port);
	}

	public Player2(final String ip, final int port) {
		super("J-TRON - Player 2", ip, port);
	}

	@Override
	protected KeyAdapter getKeyAdapter() {
		return (new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				try {
					if (verifyPlayersInsideDimension()) {

						switch (Character.toLowerCase(e.getKeyChar())) {
						case 'w':
							posY2 -= 10;
							setWay(player2Image, posX2, posY2 + 10);
							sendCommand("UP");
							repaint();
							break;
						case 's':
							posY2 += 10;
							setWay(player2Image, posX2, posY2 - 10);
							sendCommand("DOWN");
							repaint();
							break;
						case 'a':
							posX2 -= 10;
							setWay(player2Image, posX2 + 10, posY2);
							sendCommand("LEFT");
							repaint();
							break;
						case 'd':
							posX2 += 10;
							setWay(player2Image, posX2 - 10, posY2);
							sendCommand("RIGHT");
							repaint();
							break;
						default:
							//
						}
					} else {
						bg[backgroundGame].getGraphics().drawImage(stop, 0, 0, null);
						sendCommand("LOSE");
						outCharacter.close();
						repaint();
						JOptionPane.showMessageDialog(message, "Voce perdeu!");
						System.exit(0);
					}
				} catch (final IOException excecao) {
					//
				}
				repaint();
			}
		});
	}

	@Override
	protected void threadRead() {
		new Thread() {
			@Override
			public void run() {
				while (true) {
					repaint();
					try {
						if (verifyPlayersInsideDimension()) {
							command = inCharacter.readUTF();
							if (command.equals("UP")) {
								posY -= 10;
								setWay(player1Image, posX, posY + 10);
								repaint();
							} else if (command.equals("DOWN")) {
								posY += 10;
								setWay(player1Image, posX, posY - 10);
								repaint();
							} else if (command.equals("LEFT")) {
								posX -= 10;
								setWay(player1Image, posX + 10, posY);
								repaint();
							} else if (command.equals("RIGHT")) {
								posX += 10;
								setWay(player1Image, posX - 10, posY);
								repaint();
							}
						} else {
							command = inCharacter.readUTF();
							if (command.equals("LOSE")) {
								bg[backgroundGame].getGraphics().drawImage(stop, 0, 0, null);
								repaint();
								inCharacter.close();
								Thread.interrupted();
								JOptionPane.showMessageDialog(message, "Voce venceu!");
								System.exit(0);
							}
						}
					} catch (final IOException e) {
						//
					}
					try {
						Thread.sleep(5);
					} catch (final Exception e) {
						//
					}
				}
			}
		}.start();
	}
}
