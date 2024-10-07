package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ServerGUI extends JFrame {
    private JTextArea serverLogArea;  // Zone pour afficher les logs du serveur
    private JButton stopServerButton;  // Bouton pour arrêter le serveur
    private ServerThread serverThread;  // Thread pour gérer le serveur
    private JList<String> connectedUsersList;  // Liste des utilisateurs connectés
    private DefaultListModel<String> connectedUsersModel;  // Modèle pour la liste des utilisateurs

    public ServerGUI() {
        // Titre de la fenêtre
        setTitle("Serveur de Messagerie");

        // Taille de la fenêtre
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialisation de la zone de texte pour les logs
        serverLogArea = new JTextArea();
        serverLogArea.setEditable(false);
        serverLogArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(serverLogArea);

        
     // Initialisation de la liste des utilisateurs connectés
        connectedUsersModel = new DefaultListModel<>();
        connectedUsersList = new JList<>(connectedUsersModel);
        JScrollPane userScrollPane = new JScrollPane(connectedUsersList);
        userScrollPane.setPreferredSize(new Dimension(150, 0));

        // Ajouter un panneau divisé pour séparer les logs et la liste des utilisateurs
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, userScrollPane);
        splitPane.setDividerLocation(400);
        
        
        // Bouton pour arrêter le serveur
        stopServerButton = new JButton("Arrêter le Serveur");
        stopServerButton.setFont(new Font("Arial", Font.BOLD, 14));
        stopServerButton.setBackground(new Color(59, 89, 182));
        stopServerButton.setForeground(Color.WHITE);
        stopServerButton.setFocusPainted(false);
        stopServerButton.setEnabled(false);  // Désactivé jusqu'à ce que le serveur soit lancé
        stopServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	int confirmation = JOptionPane.showConfirmDialog(null, 
                        "Êtes-vous sûr de vouloir arrêter le serveur ?", 
                        "Confirmation", 
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirmation == JOptionPane.YES_OPTION) {
                        stopServer();
                    }
            }
        });

        // Ajouter les composants à la fenêtre
        add(scrollPane, BorderLayout.CENTER);
        add(stopServerButton, BorderLayout.SOUTH);

        setVisible(true);
        startServer();  // Démarrer le serveur dès que l'interface est lancée
    }
    
    // Méthode pour démarrer le serveur
    private void startServer() {
        serverThread = new ServerThread(this);  // Crée et démarre le serveur dans un thread séparé
        serverThread.start();
        stopServerButton.setEnabled(true);  // Activer le bouton pour arrêter le serveur
        logMessage("Serveur démarré...");
    }

    // Méthode pour arrêter le serveur
    private void stopServer() {
        try {
            if (serverThread != null) {
                serverThread.stopServer();  // Arrête le serveur
                logMessage("Serveur arrêté.");
            }
        } catch (IOException e) {
            logMessage("Erreur lors de l'arrêt du serveur: " + e.getMessage());
        }
        stopServerButton.setEnabled(false);  // Désactiver le bouton d'arrêt
    }

    // Méthode pour enregistrer un message dans l'interface
    public void logMessage(String message) {
        String cleanMessage = message.trim();  // Nettoyer le message avant de l'afficher
        serverLogArea.append(message + "\n");
    }
    
    
 // Méthode pour mettre à jour la liste des utilisateurs connectés
    public void updateUserList(Set<String> users) {
        connectedUsersModel.clear();
        for (String user : users) {
            connectedUsersModel.addElement(user);
        }
    }

    // Main pour démarrer l'interface du serveur
    public static void main(String[] args) {
        new ServerGUI();
    }
}

