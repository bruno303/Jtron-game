package com.example.jtron.client.ui;

import com.example.jtron.client.player.Player;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

@Component
public class MainWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final String TITLE = "J-TRON";
    private static final String DEFAULT_IP = "192.168.0.20";
    private static final int DEFAULT_PORT = 7887;

    private final JButton btStart = new JButton("Start");
    private final JButton btExit = new JButton("Exit");
    private final JTextField tfIp = new JTextField(15);
    private final JTextField tfPort = new JTextField(4);
    private final JLabel lbIp = new JLabel("IP:");
    private final JLabel lbPort = new JLabel("Port:");

    private int portInt = 0;

    private void addObjectsIntoContainer() {
        this.add(btStart);
        this.add(btExit);
        this.add(tfIp);
        this.add(tfPort);
        this.add(lbIp);
        this.add(lbPort);
    }

    private void setDefaultValuesAndListeners() {
        final TextValidator textValidator = new TextValidator();
        final ActionValidator actionValidator = new ActionValidator();
        tfIp.setText(DEFAULT_IP);
        tfPort.setText(String.valueOf(DEFAULT_PORT));
        tfPort.setColumns(10);
        tfIp.addKeyListener(textValidator);
        tfPort.addKeyListener(textValidator);
        btExit.addActionListener(actionValidator);
        btStart.addActionListener(actionValidator);
    }

    private void resizeObjects() {
        tfIp.setBounds(90, 40, 100, 20);
        tfPort.setBounds(270, 40, 100, 20);
        lbPort.setBounds(230, 40, 50, 20);
        lbIp.setBounds(70, 40, 20, 20);
        btStart.setBounds(90, 125, 100, 25);
        btExit.setBounds(270, 125, 100, 25);
    }

    public MainWindow() {
        super(TITLE);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(450, 200));
        setSize(450, 200);
        setResizable(false);
        setLayout(null);
        addObjectsIntoContainer();
        resizeObjects();
        setDefaultValuesAndListeners();
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

        private void showText(final String text, final int messageType) {
            JOptionPane.showMessageDialog(MainWindow.this, text, TITLE, messageType);
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
                return;
            }

            MainWindow.this.setVisible(false);
            new Player(TITLE, tfIp.getText(), portInt);
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            if (e.getSource() == btExit) {
                System.exit(0);
            } else if (e.getSource() == btStart) {
                startClient();
            }
        }
    }
}
