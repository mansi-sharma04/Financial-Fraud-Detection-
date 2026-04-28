package com.fraudanalyzer.model;

/**
 * Encapsulates the details of a single financial transaction.
 */
public class Transaction {
    private String transactionId;
    private String userId;
    private double amount;
    private String transactionType;
    private String location;
    private String time; // Can be in format "HH:mm" or "yyyy-MM-dd HH:mm:ss"
    
    // Evaluated fields
    private String status; // "Normal", "Suspicious", "Fraud"
    private int riskScore; // 0 - 100
    
    public Transaction() {}

    public Transaction(String transactionId, String userId, double amount, String transactionType, String location, String time) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.location = location;
        this.time = time;
        this.status = "Pending";
        this.riskScore = 0;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getRiskScore() { return riskScore; }
    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }
    
    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + transactionId + '\'' +
                ", userId='" + userId + '\'' +
                ", amount=" + amount +
                ", type='" + transactionType + '\'' +
                ", location='" + location + '\'' +
                ", time='" + time + '\'' +
                ", status='" + status + '\'' +
                ", score=" + riskScore +
                '}';
    }
}
