package com.fraudanalyzer.dao;

import com.fraudanalyzer.model.Transaction;
import com.fraudanalyzer.util.CSVHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Transaction related operations.
 */
public class TransactionDAO {
    private static final String FILE_PATH = "Dataset/transactions.csv";

    public TransactionDAO() {
        File file = new File(FILE_PATH);
        if(!file.exists()) {
             System.err.println("transactions.csv not found at " + FILE_PATH);
        }
    }

    /**
     * Loads all transactions from the dataset.
     */
    public List<Transaction> loadAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        List<String[]> rows = CSVHandler.readCSV(FILE_PATH);

        for (String[] row : rows) {
            if (row.length >= 6) {
                Transaction t = new Transaction(
                        row[0], // id
                        row[1], // userId
                        Double.parseDouble(row[2]), // amount
                        row[3], // type
                        row[4], // location
                        row[5]  // time
                );
                // Also parse status and riskScore if they exist in the row (for saved state)
                if (row.length >= 8) {
                    t.setStatus(row[6]);
                    t.setRiskScore(Integer.parseInt(row[7]));
                }
                transactions.add(t);
            }
        }
        return transactions;
    }

    /**
     * Adds a new transaction to the dataset.
     */
    public void addTransaction(Transaction t) {
        String[] row = {
                t.getTransactionId(),
                t.getUserId(),
                String.valueOf(t.getAmount()),
                t.getTransactionType(),
                t.getLocation(),
                t.getTime(),
                t.getStatus(),
                String.valueOf(t.getRiskScore())
        };
        CSVHandler.writeLineToCSV(FILE_PATH, row);
    }
}
