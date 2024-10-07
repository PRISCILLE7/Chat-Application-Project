package com.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerThread extends Thread {
    private static final int PORT = 12345;
    private Map<String, String> userStatusMap = new HashMap<>();
    private Set<ClientHandler> clientHandlers = new HashSet<>();
    private ServerGUI serverGUI;
    private ServerSocket serverSocket;

    public ServerThread(ServerGUI serverGUI) {
        this.serverGUI = serverGUI;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);
            serverGUI.logMessage("Le serveur est en cours d'exécution sur le port " + PORT);

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
                clientHandlers.add(clientHandler);
            }
        } catch (IOException e) {
            serverGUI.logMessage("Erreur serveur: " + e.getMessage());
        }
    }

    public void stopServer() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            for (ClientHandler handler : clientHandlers) {
                handler.closeClient();
            }
            serverSocket.close();
        }
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private BufferedReader input;
        private PrintWriter output;
        private ObjectInputStream fileInput;
        private String username;
        private String status = "online";

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
                output = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true);
                fileInput = new ObjectInputStream(clientSocket.getInputStream());

                username = input.readLine().trim();
                synchronized (userStatusMap) {
                    userStatusMap.put(username, status);
                }

                serverGUI.logMessage(username + " s'est connecté.");
                broadcastUserStatus();

                String message;
                while ((message = input.readLine()) != null) {
                    if (message.startsWith("status:")) {
                        String newStatus = message.split(":")[1];
                        synchronized (userStatusMap) {
                            userStatusMap.put(username, newStatus);
                            serverGUI.updateUserList(userStatusMap.keySet());
                        }
                        serverGUI.logMessage(username + " est maintenant " + newStatus);
                        broadcastUserStatus();
                    } else if (message.equals("FILE")) {
                        String fileName = (String) fileInput.readObject();
                        byte[] fileContent = (byte[]) fileInput.readObject();
                        broadcastFile(fileName, fileContent, username);
                    } else {
                        serverGUI.logMessage(message);
                        broadcastMessage(message);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                serverGUI.logMessage("Erreur avec le client: " + e.getMessage());
            } finally {
                closeClient();
            }
        }

        private void closeClient() {
            try {
                if (username != null) {
                    synchronized (userStatusMap) {
                        userStatusMap.remove(username);
                    }
                    broadcastUserStatus();
                }
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
                serverGUI.logMessage("Connexion fermée pour " + username);
            } catch (IOException e) {
                serverGUI.logMessage("Erreur lors de la fermeture de la connexion du client: " + e.getMessage());
            }
        }

        private void broadcastMessage(String message) {
            for (ClientHandler client : clientHandlers) {
                client.output.println(message);
            }
        }

        private void broadcastUserStatus() {
            StringBuilder userList = new StringBuilder("userlist:");
            for (Map.Entry<String, String> entry : userStatusMap.entrySet()) {
                userList.append(entry.getKey()).append(" (").append(entry.getValue()).append("), ");
            }
            if (userList.length() > 0) {
                userList.setLength(userList.length() - 2);
            }
            for (ClientHandler client : clientHandlers) {
                client.output.println(userList.toString());
            }
        }

        // Méthode pour diffuser un fichier à tous les utilisateurs
     // Méthode pour diffuser un fichier à tous les utilisateurs
        private void broadcastFile(String fileName, byte[] fileContent, String sender) {
            for (ClientHandler client : clientHandlers) {
                try {
                    client.output.println(sender + " a envoyé un fichier : " + fileName);
                    // Utiliser ObjectOutputStream pour envoyer le fichier sans utiliser la méthode println
                    ObjectOutputStream fileOut = new ObjectOutputStream(client.clientSocket.getOutputStream());
                    fileOut.writeObject("FILE");  // Indiquer qu'il s'agit d'un fichier
                    fileOut.writeObject(fileName);  // Envoyer le nom du fichier
                    fileOut.writeObject(fileContent);  // Envoyer le contenu du fichier
                    fileOut.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
