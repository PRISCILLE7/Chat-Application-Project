package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChatClientGUI extends JFrame {
    private JTextArea messageArea;  // Zone pour afficher les messages
    private JList<User> userStatusList;  // Liste pour afficher les utilisateurs et leur statut
    private DefaultListModel<User> userListModel;  // Modèle pour la liste des utilisateurs
    private JTextField messageInput;  // Champ de saisie pour écrire des messages
    private JButton sendButton;  // Bouton d'envoi de messages
    private JComboBox<String> statusDropdown;  // Pour changer le statut
    private ChatSystem chatSystem;  // Instance de ChatSystem pour gérer les messages et fichiers

    public ChatClientGUI(String serverAddress, String username) {
        // Titre de la fenêtre
        setTitle("Messagerie Instantanée - " + username);
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Zone de texte pour afficher les messages reçus
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(messageArea);
        add(scrollPane, BorderLayout.CENTER);

        // Liste des utilisateurs avec leur statut
        userListModel = new DefaultListModel<>();
        userStatusList = new JList<>(userListModel);
        userStatusList.setCellRenderer(new UserListCellRenderer());  // Appliquer un renderer pour personnaliser l'affichage
        JScrollPane userScrollPane = new JScrollPane(userStatusList);
        userScrollPane.setPreferredSize(new Dimension(150, 0));
        add(userScrollPane, BorderLayout.EAST);

        // Panneau pour la saisie de texte et le changement de statut
        JPanel inputPanel = new JPanel(new BorderLayout());

        // Champ de saisie pour les messages
        messageInput = new JTextField();
        messageInput.setFont(new Font("Arial", Font.PLAIN, 16));
        inputPanel.add(messageInput, BorderLayout.CENTER);

        // Action pour envoyer un message en appuyant sur la touche Enter
        messageInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage(username);
                }
            }
        });

        // Bouton pour envoyer un message
        sendButton = new JButton("Envoyer");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.setBackground(new Color(59, 89, 182));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setPreferredSize(new Dimension(100, 40));
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(username);
            }
        });
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Menu déroulant pour changer le statut
        statusDropdown = new JComboBox<>(new String[]{"online", "busy", "offline"});
        statusDropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeStatus(username);
            }
        });
        inputPanel.add(statusDropdown, BorderLayout.WEST);

        add(inputPanel, BorderLayout.SOUTH);

        // Connexion au serveur via ChatSystemImpl
        try {
            chatSystem = new ChatSystemImpl(serverAddress, 12345, username, this);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion au serveur", "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        setVisible(true);
    }

    // Envoi d'un message
    private void sendMessage(String username) {
        String message = messageInput.getText();
        if (!message.isEmpty()) {
            chatSystem.broadcastMessage(message, username);  // Envoyer au serveur
            messageInput.setText("");
        }
    }

    // Changement de statut avec la gestion de la possibilité d'envoyer des messages
    private void changeStatus(String username) {
        String selectedStatus = (String) statusDropdown.getSelectedItem();
        chatSystem.updateUserStatus(username, selectedStatus);  // Envoyer le statut au serveur

        // Gestion de la possibilité d'envoyer un message en fonction du statut
        if (selectedStatus.equals("offline")) {
            messageInput.setEditable(false);  // Désactiver le champ de saisie
            sendButton.setEnabled(false);     // Désactiver le bouton d'envoi
        } else {
            messageInput.setEditable(true);   // Activer le champ de saisie
            sendButton.setEnabled(true);      // Activer le bouton d'envoi
        }
    }

    // Affichage des messages reçus
    public void displayMessage(String message) {
        String cleanMessage = message.replaceAll("[^\\p{Print}]", "").trim();
        if (message.startsWith("userlist:")) {
            displayUserStatus(message.substring(9));  // Afficher la liste des utilisateurs si elle est reçue
        } else {
            messageArea.append(message + "\n");
        }
    }

    // Affichage de la liste des utilisateurs et de leurs statuts
    public void displayUserStatus(String userList) {
        // Vider la liste des utilisateurs
        userListModel.clear();

        // Exemple de format reçu : "dav (online), frede (busy)"
        String[] users = userList.split(", ");
        for (String userInfo : users) {
            String[] parts = userInfo.split(" \\(");  // Séparer le nom de l'utilisateur et son statut
            String username = parts[0];
            String status = parts[1].replace(")", "");  // Enlever la parenthèse fermante
            userListModel.addElement(new User(username, status));  // Ajouter l'utilisateur et son statut à la liste
        }
     // Rafraîchir la liste après la mise à jour
        userStatusList.repaint();
    }

    // Classe représentant un utilisateur et son statut
    public static class User {
        private String username;
        private String status;

        public User(String username, String status) {
            this.username = username;
            this.status = status;
        }

        public String getUsername() {
            return username;
        }

        public String getStatus() {
            return status;
        }
    }
    
    
    

    // Custom ListCellRenderer pour personnaliser l'affichage des utilisateurs
    public static class UserListCellRenderer extends JPanel implements ListCellRenderer<User> {
        private JLabel nameLabel;
        private JLabel statusLabel;

        public UserListCellRenderer() {
            setLayout(new BorderLayout());
            nameLabel = new JLabel();
            statusLabel = new JLabel();
            add(nameLabel, BorderLayout.WEST);
            add(statusLabel, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends User> list, User user, int index, boolean isSelected, boolean cellHasFocus) {
            nameLabel.setText(user.getUsername());

            // Appliquer une couleur et un style selon le statut de l'utilisateur
            switch (user.getStatus()) {
                case "online":
                    statusLabel.setText("● Online");
                    statusLabel.setForeground(Color.GREEN);
                    break;
                case "busy":
                    statusLabel.setText("● Busy");
                    statusLabel.setForeground(Color.ORANGE);
                    break;
                case "offline":
                    statusLabel.setText("● Offline");
                    statusLabel.setForeground(Color.RED);
                    break;
            }

            // Personnaliser le rendu lorsque la cellule est sélectionnée
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            return this;
        }
        
        
        
    }
}
