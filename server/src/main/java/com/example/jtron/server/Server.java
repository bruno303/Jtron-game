package com.example.jtron.server;

import com.example.jtron.server.executor.GameExecutor;
import com.example.jtron.server.player.Player;
import com.example.jtron.server.player.ServerClientCollection;
import com.example.jtron.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private ServerSocket serverSocket;
    private final int port;
    private final ServerClientCollection clients = new ServerClientCollection(Constants.MAX_SIZE_PLAYERS);

    public Server(int port){
        this.port = port;

        startSocket();
        waitForClients();

        LOGGER.info("Starting game...");
        new GameExecutor(clients).run();

        closeSocket();
    }

    private void closeSocket() {
        try {
            LOGGER.info("Closing socket server");
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.error("Error when closing socket server", e);
        }
    }

    private void waitForClients() {
        while (clients.canAddPlayer()) {
            try {
                LOGGER.info("Waiting for clients...");

                Socket client = serverSocket.accept();
                Player player = clients.addClient(client);
                player.sendPlayerId();
                LOGGER.info("Client {} connected: {}:{}", player.getId(), client.getInetAddress(), client.getPort());

            } catch (IOException e) {
                LOGGER.error("Error when waiting for client", e);
            }
        }
    }

    private void startSocket() {
        try {
            LOGGER.info("Starting socket server...");
            serverSocket = new ServerSocket(port);
            LOGGER.info("Listening port {}", port);
        } catch (IOException e) {
            LOGGER.error("Error starting socket server.", e);
            System.exit(1);
        }
    }
}
