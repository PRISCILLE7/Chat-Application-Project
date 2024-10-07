package com.example;

import java.io.*;
import java.net.*;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileOutputStream;

public class ChatSystemImpl implements ChatSystem {
    private String username;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private ObjectOutputStream fileOutput;  // Pour envoyer des fichiers
    private ChatClientGUI chatClientGUI;  // Référence à ChatClientGUI

    // Modifie le constructeur pour accepter une instance de ChatClientGUI
    public ChatSystemImpl(String serverAddress, int port, String username, ChatClientGUI chatClientGUI) throws IOException {
        this.username = username;
        this.chatClientGUI = chatClientGUI;  // Initialiser la variable chatClientGUI

        // Établir une connexion socket avec le serveur
        socket = new Socket(serverAddress, port);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
        fileOutput = new ObjectOutputStream(socket.getOutputStream());  // Initialiser pour l'envoi de fichiers

        // Envoyer le nom d'utilisateur au serveur pour enregistrer l'utilisateur
        output.println(username);

        // Démarrer un thread pour écouter les messages du serveur
        new Thread(this::listenForMessages).start();
    }

    // Écouter les messages entrants
    private void listenForMessages() {
        String message;
        try {
            while ((message = input.readLine()) != null) {
                if (message.startsWith("FILE")) {
                    // Recevoir le fichier
                    try {
                        ObjectInputStream fileInput = new ObjectInputStream(socket.getInputStream());
                        String fileName = (String) fileInput.readObject();
                        byte[] fileContent = (byte[]) fileInput.readObject();

                        // Sauvegarder le fichier localement
                        saveReceivedFile(fileName, fileContent);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Appeler la méthode de ChatClientGUI pour afficher le message
                    chatClientGUI.displayMessage(message);  // Utiliser l'instance de ChatClientGUI
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveReceivedFile(String fileName, byte[] fileContent) {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(fileContent);  // Sauvegarder le contenu du fichier localement
            chatClientGUI.displayMessage("Fichier reçu : " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void login(String username) {
        output.println(username + " has logged in.");
    }

    @Override
    public void logout(String username) {
        output.println(username + " has logged out.");
        try {
            socket.close();  // Fermer la connexion lors de la déconnexion
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void broadcastMessage(String message, String sender) {
        output.println(sender + ": " + message);  // Envoyer le message au serveur
    }

    @Override
    public void updateUserStatus(String username, String status) {
        output.println("status:" + status);  // Envoyer le changement de statut au serveur
    }

    // Implémenter la méthode sendFile pour envoyer des fichiers au serveur
    @Override
    public void sendFile(String fileName, byte[] fileContent, String username) {
        try {
            // Envoyer le fichier au serveur
            fileOutput.writeObject("FILE");  // Indiquer qu'il s'agit d'un fichier
            fileOutput.writeObject(fileName);  // Envoyer le nom du fichier
            fileOutput.writeObject(fileContent);  // Envoyer le contenu du fichier sous forme d'octets
            fileOutput.flush();  // Assurez-vous que tout est envoyé
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
