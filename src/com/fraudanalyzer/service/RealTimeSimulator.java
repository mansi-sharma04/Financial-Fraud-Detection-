package com.fraudanalyzer.service;

import com.fraudanalyzer.model.Transaction;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Simulates real-time incoming transactions using Multithreading.
 */
public class RealTimeSimulator {

    private ExecutorService executorService;
    private final FraudDetectionService fraudService;
    private Consumer<Transaction> onTransactionProcessed;
    private boolean isRunning = false;

    private static final String[] USERS = {"U001", "U002", "U003", "U004"};
    private static final String[] LOCATIONS = {"New York", "London", "Tokyo", "Mumbai", "Delhi", "Paris"};
    private static final String[] TYPES = {"PAYMENT", "TRANSFER", "WITHDRAWAL", "DEPOSIT"};

    public RealTimeSimulator(FraudDetectionService fraudService, Consumer<Transaction> onTransactionProcessed) {
        this.fraudService = fraudService;
        this.onTransactionProcessed = onTransactionProcessed;
    }

    public void startSimulation() {
        if (isRunning) return;

        executorService = Executors.newSingleThreadExecutor();
        isRunning = true;

        executorService.submit(() -> {
            Random random = new Random();
            int txCounter = 2000;

            while (isRunning) {
                try {
                    // Wait for 3 to 8 seconds between transactions
                    Thread.sleep(3000 + random.nextInt(5000));

                    // Generate a random transaction
                    String id = "T" + txCounter++;
                    String userId = USERS[random.nextInt(USERS.length)];
                    
                    // Inject occassional massive frauds or keep normal
                    double amount = (random.nextDouble() > 0.95) ? (50000 + random.nextInt(20000)) : (10 + random.nextInt(4000));
                    
                    String type = TYPES[random.nextInt(TYPES.length)];
                    String location = LOCATIONS[random.nextInt(LOCATIONS.length)];
                    
                    // Generate a random hour to maybe hit midnight rule
                    int hour = (random.nextDouble() > 0.90) ? random.nextInt(4) : (5 + random.nextInt(18));
                    int minute = random.nextInt(60);
                    String time = String.format("%02d:%02d", hour, minute);

                    Transaction t = new Transaction(id, userId, amount, type, location, time);

                    // Process transaction
                    fraudService.evaluateTransaction(t);

                    // Notify UI / System
                    if (onTransactionProcessed != null) {
                        onTransactionProcessed.accept(t);
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    public void stopSimulation() {
        isRunning = false;
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }
}
