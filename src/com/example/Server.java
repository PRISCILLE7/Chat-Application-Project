package com.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static Set<String> connectedUsers = new HashSet<>();
    private static Set<PrintWriter> clientWriters = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Chat server is running...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader input;
        private PrintWriter output;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);
                clientWriters.add(output);

                // Get the username and notify everyone
                username = input.readLine();
                synchronized (connectedUsers) {
                    connectedUsers.add(username);
                }
                broadcastConnectedUsers();

                String message;
                while ((message = input.readLine()) != null) {
                    System.out.println("Received: " + message);
                    broadcastMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (username != null) {
                    synchronized (connectedUsers) {
                        connectedUsers.remove(username);
                    }
                    broadcastConnectedUsers();
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                clientWriters.remove(output);
            }
        }

        private void broadcastMessage(String message) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }

        private void broadcastConnectedUsers() {
            String userList = "Connected users: " + connectedUsers.toString();
            for (PrintWriter writer : clientWriters) {
                writer.println(userList);
            }
        }
    }
}
