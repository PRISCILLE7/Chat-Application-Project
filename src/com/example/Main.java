package com.example;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            // Ask for the server IP and port
            System.out.print("Enter server IP address: ");
            String serverAddress = scanner.nextLine();

            // Ask for username
            System.out.print("Enter your username: ");
            String username = scanner.nextLine();

            // Créer une interface graphique pour le client (si besoin)
            ChatClientGUI gui = new ChatClientGUI(serverAddress, username);

            // Create a ChatSystem with socket connection
            ChatSystem chatSystem = new ChatSystemImpl(serverAddress, 12345, username, gui);
            chatSystem.login(username);

            String input;
            do {
                System.out.println("Enter message, 'status' to change status, or 'exit' to quit: ");
                input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("status")) {
                    System.out.print("Enter your new status (online/away/busy): ");
                    String status = scanner.nextLine().trim();
                    chatSystem.updateUserStatus(username, status);
                } else if (!input.equalsIgnoreCase("exit")) {
                    chatSystem.broadcastMessage(input, username);  // Use the correct method signature
                }
            } while (!input.equalsIgnoreCase("exit"));

            chatSystem.logout(username);
            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
