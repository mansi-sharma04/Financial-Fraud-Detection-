package com.fraudanalyzer.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to read and write CSV files.
 */
public class CSVHandler {

    public static List<String[]> readCSV(String filePath) {
        List<String[]> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip header
                    continue;
                }
                if (line.trim().isEmpty()) continue;
                String[] values = line.split(",");
                lines.add(values);
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV from " + filePath + ": " + e.getMessage());
        }
        return lines;
    }

    public static void writeLineToCSV(String filePath, String[] values) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                sb.append(values[i]);
                if (i < values.length - 1) {
                    sb.append(",");
                }
            }
            bw.write(sb.toString());
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to CSV " + filePath + ": " + e.getMessage());
        }
    }
}
