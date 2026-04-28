package com.fraudanalyzer.ui;

import com.fraudanalyzer.model.Transaction;
import com.fraudanalyzer.util.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class AlertsPanel extends JPanel {

    private JPanel alertsContainer;

    public AlertsPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Constants.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Fraud & Suspicious Alerts map");
        title.setFont(Constants.FONT_TITLE);
        add(title, BorderLayout.NORTH);

        alertsContainer = new JPanel();
        alertsContainer.setLayout(new BoxLayout(alertsContainer, BoxLayout.Y_AXIS));
        alertsContainer.setBackground(Constants.CARD_BACKGROUND);

        JScrollPane scrollPane = new JScrollPane(alertsContainer);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateAlerts(List<Transaction> transactions) {
        alertsContainer.removeAll();
        
        // Show only Suspicious/Fraud, reverse order to keep latest on top
        for (int i = transactions.size() - 1; i >= 0; i--) {
            Transaction t = transactions.get(i);
            if ("Fraud".equals(t.getStatus())) {
                alertsContainer.add(createDetailedAlertItem(t, Constants.COLOR_FRAUD, "CRITICAL: Fraudulent Activity Detected"));
            } else if ("Suspicious".equals(t.getStatus())) {
                alertsContainer.add(createDetailedAlertItem(t, Constants.COLOR_SUSPICIOUS, "WARNING: Suspicious Pattern Observed"));
            }
        }
        alertsContainer.revalidate();
        alertsContainer.repaint();
    }

    private JPanel createDetailedAlertItem(Transaction t, Color color, String headerMsg) {
        JPanel item = new JPanel(new BorderLayout(10, 10));
        item.setBackground(Constants.CARD_BACKGROUND);
        item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 1, 0, color),
                new EmptyBorder(15, 15, 15, 15)
        ));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Constants.CARD_BACKGROUND);
        
        JLabel headerLbl = new JLabel(headerMsg);
        headerLbl.setFont(Constants.FONT_HEADER);
        headerLbl.setForeground(color);
        headerPanel.add(headerLbl, BorderLayout.WEST);

        JLabel scoreLbl = new JLabel("Risk Score: " + t.getRiskScore() + "/100");
        scoreLbl.setFont(Constants.FONT_HEADER);
        scoreLbl.setForeground(color);
        headerPanel.add(scoreLbl, BorderLayout.EAST);

        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        contentPanel.setBackground(Constants.CARD_BACKGROUND);
        contentPanel.add(new JLabel("Transaction ID: " + t.getTransactionId()));
        contentPanel.add(new JLabel("User ID: " + t.getUserId()));
        contentPanel.add(new JLabel(String.format("Amount: $%.2f", t.getAmount())));
        contentPanel.add(new JLabel("Location: " + t.getLocation() + " at " + t.getTime()));

        item.add(headerPanel, BorderLayout.NORTH);
        item.add(contentPanel, BorderLayout.CENTER);

        return item;
    }
}
