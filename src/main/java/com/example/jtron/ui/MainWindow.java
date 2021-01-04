package com.example.jtron.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.springframework.stereotype.Component;

import com.example.jtron.player.Player1;
import com.example.jtron.player.Player2;
import com.example.jtron.server.Server;

@Component
public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final String TITLE = "J-TRON";

	private final JButton btStart = new JButton("Start");
	private final JButton btExit = new JButton("Exit");
	private final JTextField tfIp = new JTextField(15);
	private final JTextField tfPort = new JTextField(4);
	private final JLabel lbI = new JLabel("IP:");
	private final JLabel lbPort = new JLabel("Port:");
	private final JLabel lbChoosePlayer = new JLabel("You will be:");
	private final JRadioButton rbPlayer1 = new JRadioButton("Player 1", true);
	private final JRadioButton rbPlayer2 = new JRadioButton("Player 2", false);
	private final ButtonGroup bgChoosePlayer = new ButtonGroup();
	private final JButton btInfo = new JButton("Info");
	private final String info;

	private int portInt = 0;

	private void addObjectsIntoContainer() {
		this.add(btStart);
		this.add(btExit);
		this.add(tfIp);
		this.add(tfPort);
		this.add(lbI);
		this.add(lbPort);
		this.add(rbPlayer1);
		this.add(rbPlayer2);
		this.add(lbChoosePlayer);
		bgChoosePlayer.add(rbPlayer1);
		bgChoosePlayer.add(rbPlayer2);
		this.add(btInfo);
	}

	private void setDefaultValuesAndListeners() {
		final TextValidator textValidator = new TextValidator();
		final ActionValidator actionValidator = new ActionValidator();
		tfIp.setEnabled(false);
		tfIp.setText("127.0.0.1");
		tfPort.setText("1200");
		tfPort.setColumns(10);
		tfIp.addKeyListener(textValidator);
		tfPort.addKeyListener(textValidator);
		btExit.addActionListener(actionValidator);
		btStart.addActionListener(actionValidator);
		rbPlayer1.addActionListener(actionValidator);
		rbPlayer2.addActionListener(actionValidator);
		btInfo.addActionListener(actionValidator);
	}

	private void resizeObjects() {
		tfIp.setBounds(90, 40, 90, 20);
		tfPort.setBounds(260, 40, 90, 20);
		lbPort.setBounds(220, 40, 50, 20);
		lbI.setBounds(70, 40, 20, 20);
		btStart.setBounds(90, 135, 90, 25);
		btExit.setBounds(260, 135, 90, 25);
		lbChoosePlayer.setBounds(400, 30, 90, 20);
		rbPlayer1.setBounds(400, 55, 90, 20);
		rbPlayer2.setBounds(400, 75, 90, 20);
		btInfo.setBounds(400, 135, 90, 25);
	}

	private String getInfoText() {
		return "Obs:\n\n" + "O servidor sera obrigatoriamente o jogador 1. Esse devera passar\n"
				+ "    o seu IP e porta de acesso para o outro jogador." + "\n"
				+ "No campo Imagens, deve ser inserido o caminho (ou prefixo) da pasta\n"
				+ "    onde estao as imagens do jogo.\n"
				+ "Caso o campo esteja vazio, as imagens serao lidas do diretorio\n" + "    raiz do jogo";
	}

	public MainWindow() {
		super(TITLE);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(550, 200));
		setSize(550, 200);
		setResizable(false);
		setLayout(null);
		addObjectsIntoContainer();
		resizeObjects();
		setDefaultValuesAndListeners();
		this.info = getInfoText();
		getRootPane().setDefaultButton(btStart);
		pack();
		setVisible(true);
	}

	private class TextValidator extends KeyAdapter {
		String characters;

		@Override
		public void keyTyped(final KeyEvent event) {
			if (event.getSource() == tfPort) {
				characters = "0123456789";
				if (tfPort.getText().length() >= 4) {
					event.consume();
				}
			} else {
				characters = "0123456789.";
				if (tfIp.getText().length() >= 13) {
					event.consume();
				}
			}
			if (!characters.contains(event.getKeyChar() + "")) {
				event.consume();
			}
		}
	}

	private class ActionValidator implements ActionListener {

		private void showText(final String text) {
			showText(text, JOptionPane.INFORMATION_MESSAGE);
		}

		private void showText(final String text, final int messageType) {
			JOptionPane.showMessageDialog(MainWindow.this, text, TITLE, messageType);
		}

		private void showInfo() {
			showText(info);
		}

		private void startServer() {
			btStart.setEnabled(false);
			Server.startServer(tfIp.getText(), portInt);
		}

		private boolean verify() {
			try {
				portInt = Integer.parseInt(tfPort.getText());
			} catch (final Exception e) {
				return false;
			}
			return true;
		}

		private void startClient() {
			if (!verify()) {
				showText("Porta invalida!", JOptionPane.ERROR_MESSAGE);
			} else {
				if (rbPlayer1.isSelected()) {
					startServer();
					MainWindow.this.setVisible(false);
					Player1.createPlayer1(tfIp.getText(), portInt);
				} else {
					MainWindow.this.setVisible(false);
					Player2.createPlayer2(tfIp.getText(), portInt);
				}
			}
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (e.getSource() == btExit) {
				System.exit(0);
			} else if (e.getSource() == btStart) {
				startClient();
			} else if (e.getSource() == rbPlayer1) {
				tfIp.setText("127.0.0.1");
				tfIp.setEnabled(false);
			} else if (e.getSource() == rbPlayer2) {
				tfIp.setEnabled(true);
			} else if (e.getSource() == btInfo) {
				showInfo();
			}
		}
	}
}
