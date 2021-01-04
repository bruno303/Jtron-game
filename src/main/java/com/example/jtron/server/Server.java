package com.example.jtron.server;

import com.example.jtron.utils.ThreadUtils;

import java.net.*;
import java.io.*;

public class Server {

	public static synchronized void startServer(String ip, int port) {
		new Thread(() -> new Server(ip, port)).start();
	}
	
	public Server(String ip, int port){
	    ServerSocket serverSocket = null;
	    Socket player1 = null, player2 = null;

	    try {
	    	serverSocket = new ServerSocket(port);
	    	System.out.println("Iniciando o serverSocket...\n");
	    	System.out.println("Lendo o serverSocket: " + ip + "\n");
	    	System.out.println("Lendo a porta: " + port + "\n");
	    } catch (IOException e) {
	    	System.out.println("Erro de comunica��o com a porta " + port +", " + e);
	    	System.exit(1);
	    }

		try {
    		System.out.println("Aguardando um novo cliente...");
    		player1 = serverSocket.accept();
    		System.out.println("\nCliente: 1");
    		System.out.println("\nLocal: " + player1.getInetAddress() + " - Porta: " + player1.getPort());
    		System.out.println("Aguardando um novo cliente...");
    		player2 = serverSocket.accept();
    		System.out.println("\nCliente: 2");
    		System.out.println("\nLocal: " + player2.getInetAddress() + " - Porta: " + player2.getPort());
    	} catch (IOException e) {
    		System.out.println("Aceitação falhou : " + port + ", " + e);
    		System.exit(1);
    	}
    	System.out.println("\nConexão estabelecida !");
		new ExecutionPlayer1(player1, player2).start();
		new ExecutionPlayer2(player1, player2).start();
	    try {
	    	serverSocket.close();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
}

abstract class ExecutionGame extends Thread {
	Socket player1Socket, player2Socket;
	String command1, command2;
	DataOutputStream send1, send2;
	DataInputStream receive1, receive2;
	
	ExecutionGame(Socket player1Socket, Socket player2Socket){
		this.player1Socket = player1Socket;
		this.player2Socket = player2Socket;
	}
	
	protected abstract void sendCommand();
	
	public void run() {
		try {
			while (true) {
				receive1 = new DataInputStream(player1Socket.getInputStream());
				send1 = new DataOutputStream(player1Socket.getOutputStream());
				receive2 = new DataInputStream(player2Socket.getInputStream());
				send2 = new DataOutputStream(player2Socket.getOutputStream());
				sendCommand();
				ThreadUtils.sleep(5);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class ExecutionPlayer1 extends ExecutionGame {
	ExecutionPlayer1(Socket jogador1, Socket jogador2) {
		super(jogador1, jogador2);
	}
	
	@Override
	protected void sendCommand(){
		try {
			command1 = receive1.readUTF();
			System.out.println("Comando recebido: " + command1 + "\n");
			send2.writeUTF(command1);
			System.out.println("\nComando(index) enviado ao jogador 2: " + command1 + "\n");
		} catch(Exception e) {}
	}
};


class ExecutionPlayer2 extends ExecutionGame {
	ExecutionPlayer2(Socket jogador1, Socket jogador2) {
		super(jogador1, jogador2);
	}
	
	@Override
	protected void sendCommand(){
		try {
			command2 = receive2.readUTF();
			System.out.println("Comando recebido: " + command2 + "\n");
			send1.writeUTF(command2);
			System.out.println("\nComando(index) enviado ao jogador 1: " + command2 + "\n");
		} catch(Exception e) {}
	}
};
