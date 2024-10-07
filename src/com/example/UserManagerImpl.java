package com.example;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class UserManagerImpl implements UserManager {
    private Map<String, String> users; // username -> status

    public UserManagerImpl() {
        users = new HashMap<>();
    }

    @Override
    public void registerUser(String username) {
        if (!users.containsKey(username)) {
            users.put(username, "online"); // Default status: online
            System.out.println(username + " has connected.");
        }
    }

    @Override
    public void removeUser(String username) {
        if (users.containsKey(username)) {
            users.remove(username);
            System.out.println(username + " has disconnected.");
        }
    }

    @Override
    public List<String> getConnectedUsers() {
        return new ArrayList<>(users.keySet());
    }

    @Override
    public void updateUserStatus(String username, String status) {
        if (users.containsKey(username)) {
            users.put(username, status);
            System.out.println(username + " is now " + status + ".");
        }
    }
}
