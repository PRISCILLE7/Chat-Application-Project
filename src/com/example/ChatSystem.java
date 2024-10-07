package com.example;

public interface ChatSystem {
    void broadcastMessage(String message, String sender);
    void login(String username);
    void logout(String username);
    void updateUserStatus(String username, String status);
    
    // Ajouter cette méthode pour envoyer des fichiers
    void sendFile(String fileName, byte[] fileContent, String username);
}
