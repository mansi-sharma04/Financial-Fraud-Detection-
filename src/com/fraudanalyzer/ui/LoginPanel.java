package com.fraudanalyzer.ui;

import com.fraudanalyzer.dao.UserDAO;
import com.fraudanalyzer.model.User;
import com.fraudanalyzer.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {

    private MainFrame mainFrame;
    private UserDAO userDAO;

    public LoginPanel(MainFrame mainFrame, UserDAO userDAO) {
        this.mainFrame = mainFrame;
        this.userDAO = userDAO;
        initUI();
    }

    private void initUI() {
        setBackground(Constants.BACKGROUND_COLOR);
        setLayout(new GridBagLayout()); // Center everything

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Constants.CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));

        JLabel titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(Constants.FONT_TITLE);
        titleLabel.setForeground(Constants.TEXT_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Financial Fraud Pattern Analyzer");
        subtitleLabel.setFont(Constants.FONT_SMALL);
        subtitleLabel.setForeground(Constants.TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Inputs
        JTextField userField = new JTextField(15);
        userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        userField.setFont(Constants.FONT_REGULAR);
        
        JPasswordField passField = new JPasswordField(15);
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passField.setFont(Constants.FONT_REGULAR);

        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(Constants.FONT_HEADER);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBackground(Constants.PRIMARY_COLOR);
        loginBtn.setFocusPainted(false);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Constants.COLOR_FRAUD);
        errorLabel.setFont(Constants.FONT_SMALL);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton registerBtn = new JButton("Don't have an account? Register");
        registerBtn.setFont(Constants.FONT_SMALL);
        registerBtn.setForeground(Constants.PRIMARY_COLOR);
        registerBtn.setBackground(Constants.CARD_BACKGROUND);
        registerBtn.setBorderPainted(false);
        registerBtn.setFocusPainted(false);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Actions
        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please fill all fields.");
                return;
            }
            User u = userDAO.login(username, password);
            if (u != null) {
                // Success
                mainFrame.onLoginSuccess(u);
            } else {
                errorLabel.setText("Invalid credentials!");
            }
        });

        registerBtn.addActionListener(e -> mainFrame.showRegisterPanel());

        // Assembly
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(subtitleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        card.add(wrapField("Username", userField));
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(wrapField("Password", passField));
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(errorLabel);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(loginBtn);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(registerBtn);

        add(card); // Add card to centered GridBagLayout
    }

    private JPanel wrapField(String labelText, JComponent field) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Constants.CARD_BACKGROUND);
        p.setMaximumSize(new Dimension(300, 60));
        
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(Constants.FONT_REGULAR);
        lbl.setForeground(Constants.TEXT_DARK);
        
        p.add(lbl, BorderLayout.NORTH);
        p.add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.CENTER);
        p.add(field, BorderLayout.SOUTH);
        return p;
    }
}
