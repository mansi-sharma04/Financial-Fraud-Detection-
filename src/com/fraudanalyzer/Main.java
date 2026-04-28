package com.fraudanalyzer;

import com.fraudanalyzer.ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set modern Look and Feel if available
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            // Enhance Nimbus
            UIManager.put("nimbusBase", new java.awt.Color(41, 128, 185)); // Primary highlight
            UIManager.put("nimbusBlueGrey", new java.awt.Color(245, 247, 250)); // General default
        } catch (Exception e) {
            // Fallback to default
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame app = new MainFrame();
            app.setVisible(true);
        });
    }
}
