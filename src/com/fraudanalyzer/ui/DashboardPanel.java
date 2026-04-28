package com.fraudanalyzer.ui;

import com.fraudanalyzer.model.Transaction;
import com.fraudanalyzer.util.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {

    private JLabel totalLabel;
    private JLabel fraudLabel;
    private JLabel suspiciousLabel;
    private JPanel recentAlertsContainer;

    public DashboardPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Constants.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top Section: Stat Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBackground(Constants.BACKGROUND_COLOR);
        
        statsPanel.add(createStatCard("Total Transactions", totalLabel = createValueLabel("0"), Constants.PRIMARY_COLOR));
        statsPanel.add(createStatCard("Suspicious Alerts", suspiciousLabel = createValueLabel("0"), Constants.COLOR_SUSPICIOUS));
        statsPanel.add(createStatCard("Fraud Alerts", fraudLabel = createValueLabel("0"), Constants.COLOR_FRAUD));

        add(statsPanel, BorderLayout.NORTH);

        // Center Section: Recent Alerts List
        JPanel alertsPanel = new JPanel(new BorderLayout());
        alertsPanel.setBackground(Constants.CARD_BACKGROUND);
        alertsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel alertsTitle = new JLabel("Recent Fraud & Suspicious Activities");
        alertsTitle.setFont(Constants.FONT_HEADER);
        alertsPanel.add(alertsTitle, BorderLayout.NORTH);

        recentAlertsContainer = new JPanel();
        recentAlertsContainer.setLayout(new BoxLayout(recentAlertsContainer, BoxLayout.Y_AXIS));
        recentAlertsContainer.setBackground(Constants.CARD_BACKGROUND);
        
        JScrollPane scrollPane = new JScrollPane(recentAlertsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        alertsPanel.add(scrollPane, BorderLayout.CENTER);

        add(alertsPanel, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Constants.CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Constants.FONT_REGULAR);
        titleLabel.setForeground(Constants.TEXT_MUTED);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Constants.CARD_BACKGROUND);
        contentPanel.add(valueLabel, BorderLayout.CENTER);
        
        // Add a colored bottom strip
        JPanel strip = new JPanel();
        strip.setBackground(accent);
        strip.setPreferredSize(new Dimension(0, 5));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        card.add(strip, BorderLayout.SOUTH);

        return card;
    }

    private JLabel createValueLabel(String initial) {
        JLabel lbl = new JLabel(initial);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lbl.setForeground(Constants.TEXT_DARK);
        return lbl;
    }

    public void updateStats(List<Transaction> allTransactions) {
        int total = allTransactions.size();
        int fraud = 0;
        int suspicious = 0;
        
        recentAlertsContainer.removeAll();

        // Reverse iterate to show recent first
        int alertCount = 0;
        for (int i = allTransactions.size() - 1; i >= 0; i--) {
            Transaction t = allTransactions.get(i);
            if ("Fraud".equals(t.getStatus())) {
                fraud++;
                if (alertCount < 10) {
                    recentAlertsContainer.add(createAlertItem(t, Constants.COLOR_FRAUD));
                    alertCount++;
                }
            } else if ("Suspicious".equals(t.getStatus())) {
                suspicious++;
                if (alertCount < 10) {
                    recentAlertsContainer.add(createAlertItem(t, Constants.COLOR_SUSPICIOUS));
                    alertCount++;
                }
            }
        }

        totalLabel.setText(String.valueOf(total));
        fraudLabel.setText(String.valueOf(fraud));
        suspiciousLabel.setText(String.valueOf(suspicious));
        
        recentAlertsContainer.revalidate();
        recentAlertsContainer.repaint();
    }

    private JPanel createAlertItem(Transaction t, Color color) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(Constants.CARD_BACKGROUND);
        item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 1, 0, color), // Left strip and bottom divider
            new EmptyBorder(10, 10, 10, 10)
        ));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel info = new JLabel("Tx: " + t.getTransactionId() + " | User: " + t.getUserId() + " | Amount: $" + t.getAmount());
        info.setFont(Constants.FONT_REGULAR);
        
        JLabel status = new JLabel(t.getStatus() + " (Score: " + t.getRiskScore() + ")");
        status.setFont(Constants.FONT_SMALL);
        status.setForeground(color);

        item.add(info, BorderLayout.CENTER);
        item.add(status, BorderLayout.EAST);
        
        return item;
    }
}
