package com.example;

import java.util.List;

public interface UserManager {
    void registerUser(String username);
    void removeUser(String username);
    List<String> getConnectedUsers();
    void updateUserStatus(String username, String status);
}
