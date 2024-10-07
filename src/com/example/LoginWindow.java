package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginWindow extends JFrame {
    private JTextField ipField;          // Champ pour l'adresse IP
    private JTextField usernameField;    // Champ pour le nom d'utilisateur
    private JButton connectButton;       // Bouton de connexion
    private JLabel titleLabel;           // Titre du formulaire
    private JLabel registerLabel;        // Label d'enregistrement

    public LoginWindow() {
        // Définir le titre de la fenêtre
        setTitle("Connexion à la messagerie");

        // Définir la taille de la fenêtre
        setSize(600, 400);  // Taille de la fenêtre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Utiliser un JLayeredPane pour superposer le fond blanc, l'image, et le formulaire
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(600, 400));
        setContentPane(layeredPane);

        // Fond blanc
        JPanel whiteBackground = new JPanel();
        whiteBackground.setBackground(Color.WHITE);
        whiteBackground.setBounds(0, 0, 600, 400);
        layeredPane.add(whiteBackground, Integer.valueOf(0));  // Couche 0 (la plus basse)

        // Charger l'image de fond et la redimensionner
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/com/example/image.png"));
        Image image = originalIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);  // Redimensionner l'image
        ImageIcon resizedIcon = new ImageIcon(image);
        JLabel imageLabel = new JLabel(resizedIcon);
        imageLabel.setBounds(50, 75, 250, 250);  // Positionner l'image au-dessus du fond blanc
        layeredPane.add(imageLabel, Integer.valueOf(1));  // Couche 1 (au-dessus du fond)

        // Panneau pour la connexion
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS)); // Utilisation du BoxLayout
        loginPanel.setOpaque(false);  // Fond transparent pour laisser voir l'image derrière
        loginPanel.setBounds(320, 50, 250, 300);  // Positionner le panneau de connexion
        layeredPane.add(loginPanel, Integer.valueOf(2));  // Couche 2 (au-dessus de l'image)

        // Titre de la fenêtre
        titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));  // Taille de police légèrement plus grande
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Centrer le titre
        loginPanel.add(titleLabel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 20)));  // Ajouter de l'espace vertical

        // Champ pour l'adresse IP
        JLabel ipLabel = new JLabel("Adresse IP:");
        ipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Centrer l'étiquette
        loginPanel.add(ipLabel);

        ipField = new JTextField();
        ipField.setMaximumSize(new Dimension(250, 35));  // Limite maximale de la taille
        ipField.setPreferredSize(new Dimension(250, 35));  // Taille préférée
        loginPanel.add(ipField);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 10)));  // Ajouter de l'espace vertical

        // Champ pour le nom d'utilisateur
        JLabel usernameLabel = new JLabel("Nom d'utilisateur:");
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Centrer l'étiquette
        loginPanel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(250, 35));  // Limite maximale de la taille
        usernameField.setPreferredSize(new Dimension(250, 35));  // Taille préférée
        loginPanel.add(usernameField);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 20)));  // Ajouter de l'espace vertical

        // Bouton pour se connecter
        connectButton = new JButton("Login");
        connectButton.setAlignmentX(Component.CENTER_ALIGNMENT);  // Centrer le bouton
        loginPanel.add(connectButton);

        // Action déclenchée lors du clic sur le bouton "Login"
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String serverAddress = ipField.getText();    // Récupérer l'adresse IP
                String username = usernameField.getText();   // Récupérer le nom d'utilisateur

                // Vérification que les champs ne sont pas vides
                if (!serverAddress.isEmpty() && !username.isEmpty()) {
                    // Lancer la fenêtre de messagerie avec les infos de connexion
                    ChatClientGUI chatClientGUI = new ChatClientGUI(serverAddress, username);
                    chatClientGUI.setVisible(true);  // Afficher la fenêtre de messagerie
                    dispose();  // Fermer la fenêtre de connexion
                } else {
                    JOptionPane.showMessageDialog(null, "Veuillez remplir tous les champs !", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Label d'enregistrement
        registerLabel = new JLabel("");
        registerLabel.setFont(new Font("Arial", Font.PLAIN, 12));  // Taille de police plus petite
        registerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Centrer le label
        loginPanel.add(registerLabel);

        // Rendre la fenêtre visible
        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginWindow();  // Lancer la fenêtre de connexion
    }
}
