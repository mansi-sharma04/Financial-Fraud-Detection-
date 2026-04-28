package com.fraudanalyzer.ui;

import com.fraudanalyzer.dao.TransactionDAO;
import com.fraudanalyzer.dao.UserDAO;
import com.fraudanalyzer.model.Transaction;
import com.fraudanalyzer.model.User;
import com.fraudanalyzer.service.FraudDetectionService;
import com.fraudanalyzer.service.RealTimeSimulator;
import com.fraudanalyzer.util.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {

    private User currentUser;
    private final UserDAO userDAO = new UserDAO();
    private final TransactionDAO txDAO = new TransactionDAO();
    private final FraudDetectionService fraudService = new FraudDetectionService();
    private RealTimeSimulator simulator;

    private JPanel rootPanel;
    private CardLayout cardLayout;

    private JPanel mainAppPanel;
    private JPanel sidebar;
    private JPanel contentPanel;
    private CardLayout contentCardLayout;

    // Panels
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private DashboardPanel dashboardPanel;
    private TransactionPanel transactionPanel;
    private AlertsPanel alertsPanel;

    private List<Transaction> allTransactions;

    public MainFrame() {
        setTitle("Financial Fraud Pattern Analyzer");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center

        initDaosAndServices();
        initUI();
    }

    private void initDaosAndServices() {
        // Load initial transactions and evaluate any pending
        allTransactions = txDAO.loadAllTransactions();
        for (Transaction t : allTransactions) {
            if ("Pending".equals(t.getStatus())) {
                fraudService.evaluateTransaction(t);
            }
        }

        // Setup real time simulator
        simulator = new RealTimeSimulator(fraudService, this::onNewTransaction);
    }

    private void initUI() {
        rootPanel = new JPanel();
        cardLayout = new CardLayout();
        rootPanel.setLayout(cardLayout);

        // Pre-Auth panels
        loginPanel = new LoginPanel(this, userDAO);
        registerPanel = new RegisterPanel(this, userDAO);

        rootPanel.add(loginPanel, "Login");
        rootPanel.add(registerPanel, "Register");

        // Main App Layout (Sidebar + Content)
        mainAppPanel = new JPanel(new BorderLayout());
        buildSidebar();
        buildContentPanel();
        mainAppPanel.add(sidebar, BorderLayout.WEST);
        mainAppPanel.add(contentPanel, BorderLayout.CENTER);

        rootPanel.add(mainAppPanel, "MainApp");

        add(rootPanel);
        cardLayout.show(rootPanel, "Login");
    }

    private void buildSidebar() {
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Constants.SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(200, getHeight()));

        JLabel brandStr = new JLabel("Fraud Analyzer");
        brandStr.setFont(new Font("Segoe UI", Font.BOLD, 22));
        brandStr.setForeground(Color.WHITE);
        brandStr.setAlignmentX(Component.CENTER_ALIGNMENT);
        brandStr.setBorder(new EmptyBorder(20, 10, 30, 10));

        sidebar.add(brandStr);

        sidebar.add(createSidebarBtn("Dashboard"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createSidebarBtn("Transactions"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createSidebarBtn("Alerts"));
        
        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = createSidebarBtn("Logout");
        logoutBtn.addActionListener(e -> logout());
        sidebar.add(logoutBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
    }

    private JButton createSidebarBtn(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setBackground(Constants.SIDEBAR_COLOR);
        btn.setForeground(Constants.SIDEBAR_TEXT_COLOR);
        btn.setFont(Constants.FONT_HEADER);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(Constants.SIDEBAR_HOVER_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Constants.SIDEBAR_COLOR);
            }
        });

        if (!text.equals("Logout")) {
            btn.addActionListener(e -> contentCardLayout.show(contentPanel, text));
        }
        return btn;
    }

    private void buildContentPanel() {
        contentPanel = new JPanel();
        contentCardLayout = new CardLayout();
        contentPanel.setLayout(contentCardLayout);

        // Core Views Map
        dashboardPanel = new DashboardPanel();
        transactionPanel = new TransactionPanel(this::handleManualTransactionAdd);
        alertsPanel = new AlertsPanel();

        contentPanel.add(dashboardPanel, "Dashboard");
        contentPanel.add(transactionPanel, "Transactions");
        contentPanel.add(alertsPanel, "Alerts");
    }

    private void refreshDataViews() {
        // Run on EDT just in case (e.g., from Simulator thread)
        SwingUtilities.invokeLater(() -> {
            dashboardPanel.updateStats(allTransactions);
            transactionPanel.updateTransactions(allTransactions);
            alertsPanel.updateAlerts(allTransactions);
        });
    }

    // Callbacks & Handlers

    public void showRegisterPanel() { cardLayout.show(rootPanel, "Register"); }
    public void showLoginPanel() { cardLayout.show(rootPanel, "Login"); }

    public void onLoginSuccess(User u) {
        this.currentUser = u;
        cardLayout.show(rootPanel, "MainApp");
        refreshDataViews();
        simulator.startSimulation(); // Start generating random transactions
    }

    private void logout() {
        this.currentUser = null;
        simulator.stopSimulation();
        cardLayout.show(rootPanel, "Login");
    }

    private void onNewTransaction(Transaction t) {
        // System / Background thread generated
        allTransactions.add(t);
        txDAO.addTransaction(t); // persist changes

        // Display High Risk popup if fraud
        if ("Fraud".equals(t.getStatus())) {
            showFraudPopup(t);
        }

        refreshDataViews();
    }

    private void handleManualTransactionAdd(Transaction t) {
        // UI thread generated
        fraudService.evaluateTransaction(t);
        allTransactions.add(t);
        txDAO.addTransaction(t);
        refreshDataViews();

        if ("Fraud".equals(t.getStatus())) {
            showFraudPopup(t);
        }
    }

    private void showFraudPopup(Transaction t) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, 
                "CRITICAL ALERT!\nFraudulent transaction detected: $" + String.format("%.2f", t.getAmount()) + 
                "\nRisk Score: " + t.getRiskScore() + "\nTransaction ID: " + t.getTransactionId(), 
                "High Risk Transaction Blocked", 
                JOptionPane.WARNING_MESSAGE);
        });
    }
}
