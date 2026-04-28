package com.fraudanalyzer.service;

import com.fraudanalyzer.model.Transaction;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service to evaluate rules and classify transactions.
 */
public class FraudDetectionService {

    // Keep track of transactions per user to detect frequency and location changes
    private Map<String, List<Transaction>> userTransactionHistory = new HashMap<>();

    /**
     * Rules to evaluate:
     * 1. amount > 50,000 -> Suspicious
     * 2. more than 3 transactions within 1 minute -> Fraud
     * 3. location changes rapidly (different city within short time) -> Fraud
     * 4. unusual hours (midnight 12 AM – 4 AM) -> Suspicious
     */
    public void evaluateTransaction(Transaction t) {
        int riskScore = 0;
        boolean isFraud = false;
        boolean isSuspicious = false;

        // Rule 1: High amount
        if (t.getAmount() > 50000) {
            isSuspicious = true;
            riskScore += 40;
        }

        // Rule 4: Unusual hours (12 AM - 4 AM)
        if (isUnusualHour(t.getTime())) {
            isSuspicious = true;
            riskScore += 30;
        }

        // Add to history for temporal/spatial rules
        List<Transaction> history = userTransactionHistory.getOrDefault(t.getUserId(), new ArrayList<>());

        // Rule 2 & 3 evaluations
        int recentTxCount = 0;
        boolean rapidLocationChange = false;

        LocalTime currentTime = parseTime(t.getTime());
        
        for (Transaction past : history) {
            LocalTime pastTime = parseTime(past.getTime());

            // Check if within 1 minute
            if (currentTime != null && pastTime != null) {
                long minutesDiff = Math.abs(java.time.Duration.between(pastTime, currentTime).toMinutes());
                if (minutesDiff <= 1) {
                    recentTxCount++;
                    // Rule 3: Different city within short time (e.g. <= 1 hr, let's say 1 min for this simulation since they test together)
                    if (!past.getLocation().equalsIgnoreCase(t.getLocation())) {
                        rapidLocationChange = true;
                    }
                }
            }
        }

        // Check recent transaction count (includes current one theoretically, but we compare history to > 2 (so >= 3 total within 1 min))
        if (recentTxCount >= 3) {
            isFraud = true;
            riskScore += 80;
        }

        if (rapidLocationChange) {
            isFraud = true;
            riskScore += 80;
        }

        // Finalize Status
        if (isFraud) {
            t.setStatus("Fraud");
            t.setRiskScore(Math.min(100, riskScore));
        } else if (isSuspicious) {
            t.setStatus("Suspicious");
            t.setRiskScore(Math.min(100, riskScore));
        } else {
            t.setStatus("Normal");
            t.setRiskScore(Math.min(20, riskScore)); // Normal could have tiny risk
        }

        // Update history
        history.add(t);
        userTransactionHistory.put(t.getUserId(), history);
    }

    private LocalTime parseTime(String timeStr) {
        try {
            // Assumes HH:mm format
            return LocalTime.parse(timeStr);
        } catch (DateTimeParseException e) {
            // Default to some safe value or ignore
            return null;
        }
    }

    private boolean isUnusualHour(String timeStr) {
        LocalTime time = parseTime(timeStr);
        if (time != null) {
            return time.isAfter(LocalTime.of(23, 59)) || time.isBefore(LocalTime.of(4, 1));
        }
        return false;
    }
}
