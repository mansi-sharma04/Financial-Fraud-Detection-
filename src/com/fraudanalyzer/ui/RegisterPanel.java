package com.fraudanalyzer.ui;

import com.fraudanalyzer.dao.UserDAO;
import com.fraudanalyzer.model.User;
import com.fraudanalyzer.util.Constants;

import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JPanel {

    private MainFrame mainFrame;
    private UserDAO userDAO;

    public RegisterPanel(MainFrame mainFrame, UserDAO userDAO) {
        this.mainFrame = mainFrame;
        this.userDAO = userDAO;
        initUI();
    }

    private void initUI() {
        setBackground(Constants.BACKGROUND_COLOR);
        setLayout(new GridBagLayout()); 

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Constants.CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));

        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(Constants.FONT_TITLE);
        titleLabel.setForeground(Constants.TEXT_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Inputs
        JTextField userField = new JTextField(15);
        userField.setMaximumSize(new Dimension(300, 40));
        userField.setFont(Constants.FONT_REGULAR);
        
        JPasswordField passField = new JPasswordField(15);
        passField.setMaximumSize(new Dimension(300, 40));
        passField.setFont(Constants.FONT_REGULAR);

        JButton regBtn = new JButton("Register");
        regBtn.setFont(Constants.FONT_HEADER);
        regBtn.setForeground(Color.WHITE);
        regBtn.setBackground(Constants.PRIMARY_COLOR);
        regBtn.setFocusPainted(false);
        regBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        regBtn.setMaximumSize(new Dimension(300, 40));

        JLabel messageLabel = new JLabel(" ");
        messageLabel.setFont(Constants.FONT_SMALL);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginBtn = new JButton("Already have an account? Login");
        loginBtn.setFont(Constants.FONT_SMALL);
        loginBtn.setForeground(Constants.PRIMARY_COLOR);
        loginBtn.setBackground(Constants.CARD_BACKGROUND);
        loginBtn.setBorderPainted(false);
        loginBtn.setFocusPainted(false);
        loginBtn.setContentAreaFilled(false);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Actions
        regBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setForeground(Constants.COLOR_FRAUD);
                messageLabel.setText("Please fill all fields.");
                return;
            }
            User u = new User(null, username, password);
            boolean success = userDAO.register(u);
            if (success) {
                messageLabel.setForeground(Constants.COLOR_NORMAL);
                messageLabel.setText("Registration successful! You can login now.");
            } else {
                messageLabel.setForeground(Constants.COLOR_FRAUD);
                messageLabel.setText("Username already exists.");
            }
        });

        loginBtn.addActionListener(e -> mainFrame.showLoginPanel());

        // Assembly
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        card.add(wrapField("Username", userField));
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(wrapField("Password", passField));
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(messageLabel);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(regBtn);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(loginBtn);

        add(card);
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
