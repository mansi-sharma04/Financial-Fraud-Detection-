package com.fraudanalyzer.dao;

import com.fraudanalyzer.model.User;
import com.fraudanalyzer.util.CSVHandler;

import java.io.File;
import java.util.List;

/**
 * Data Access Object for User related operations.
 */
public class UserDAO {
    private static final String FILE_PATH = "Dataset/users.csv";

    public UserDAO() {
        // Ensure directory and file exists, though we assume the system provides it.
        File file = new File(FILE_PATH);
        if(!file.exists()) {
             System.err.println("users.csv not found at " + FILE_PATH);
        }
    }

    /**
     * Authenticates a user.
     */
    public User login(String username, String password) {
        List<String[]> rows = CSVHandler.readCSV(FILE_PATH);
        for (String[] row : rows) {
            if (row.length >= 3) {
                if (row[1].equals(username) && row[2].equals(password)) {
                    return new User(row[0], row[1], row[2]);
                }
            }
        }
        return null; // Login failed
    }

    /**
     * Registers a new user.
     */
    public boolean register(User user) {
        List<String[]> rows = CSVHandler.readCSV(FILE_PATH);
        // Check if username already exists
        for (String[] row : rows) {
            if (row.length >= 2 && row[1].equals(user.getUsername())) {
                return false; // Username taken
            }
        }
        
        // userId strategy: U + (count + 1)
        String newId = "U" + String.format("%03d", rows.size() + 1);
        user.setUserId(newId);

        String[] newUserRow = {
                user.getUserId(),
                user.getUsername(),
                user.getPassword()
        };

        CSVHandler.writeLineToCSV(FILE_PATH, newUserRow);
        return true;
    }
}
