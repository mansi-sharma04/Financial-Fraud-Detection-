package com.fraudanalyzer.ui;

import com.fraudanalyzer.model.Transaction;
import com.fraudanalyzer.util.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class TransactionPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private Consumer<Transaction> onAddManualTransaction;

    public TransactionPanel(Consumer<Transaction> onAddManualTransaction) {
        this.onAddManualTransaction = onAddManualTransaction;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Constants.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header and Add Button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Constants.BACKGROUND_COLOR);
        
        JLabel title = new JLabel("All Transactions");
        title.setFont(Constants.FONT_TITLE);
        
        JButton addBtn = new JButton("+ Add Transaction");
        addBtn.setBackground(Constants.PRIMARY_COLOR);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(Constants.FONT_REGULAR);
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> showAddTransactionDialog());

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(addBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Table setup
        String[] columns = {"ID", "User ID", "Amount", "Type", "Location", "Time", "Status", "Risk Score"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only grid
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(Constants.FONT_REGULAR);
        table.getTableHeader().setFont(Constants.FONT_HEADER);
        table.getTableHeader().setBackground(Color.WHITE);
        
        // Custom cell renderer to color code rows based on Status
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) tableModel.getValueAt(row, 6);
                
                if (!isSelected) {
                    if ("Fraud".equals(status)) {
                        c.setBackground(new Color(253, 237, 236)); // Light red background
                        c.setForeground(Constants.COLOR_FRAUD);
                    } else if ("Suspicious".equals(status)) {
                        c.setBackground(new Color(254, 249, 231)); // Light orange background
                        c.setForeground(Constants.COLOR_SUSPICIOUS);
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Constants.TEXT_DARK);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateTransactions(List<Transaction> transactions) {
        tableModel.setRowCount(0); // clear existing
        for (Transaction t : transactions) {
            Object[] row = {
                t.getTransactionId(), 
                t.getUserId(), 
                String.format("$%.2f", t.getAmount()), 
                t.getTransactionType(), 
                t.getLocation(), 
                t.getTime(), 
                t.getStatus(), 
                t.getRiskScore()
            };
            tableModel.addRow(row);
        }
    }

    private void showAddTransactionDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Manual Transaction", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField userIdField = new JTextField(15);
        JTextField amountField = new JTextField(15);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"PAYMENT", "TRANSFER", "WITHDRAWAL", "DEPOSIT"});
        JTextField locationField = new JTextField(15);
        JTextField timeField = new JTextField("12:00", 15);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("User ID:"), gbc);
        gbc.gridx = 1; dialog.add(userIdField, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1; dialog.add(amountField, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1; dialog.add(typeCombo, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1; dialog.add(locationField, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Time (HH:mm):"), gbc);
        gbc.gridx = 1; dialog.add(timeField, gbc);

        row++; gbc.gridx = 1; gbc.gridy = row;
        JButton submitBtn = new JButton("Submit");
        submitBtn.addActionListener(e -> {
            try {
                String uId = userIdField.getText().trim();
                double amt = Double.parseDouble(amountField.getText().trim());
                String type = (String) typeCombo.getSelectedItem();
                String loc = locationField.getText().trim();
                String time = timeField.getText().trim();
                
                String newId = "M" + System.currentTimeMillis(); // manual ID
                Transaction t = new Transaction(newId, uId, amt, type, loc, time);
                
                if (onAddManualTransaction != null) {
                    onAddManualTransaction.accept(t); // Will evaluate and save
                }
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input. Check Amount.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(submitBtn, gbc);

        dialog.setVisible(true);
    }
}
