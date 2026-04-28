package com.fraudanalyzer.util;

import java.awt.*;

/**
 * Shared constants for UI aesthetics (Colors, Fonts).
 */
public class Constants {
    
    // UI Colors
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185); // Blue
    public static final Color BACKGROUND_COLOR = new Color(245, 247, 250); // Light Grayish Blue
    public static final Color SIDEBAR_COLOR = new Color(44, 62, 80); // Dark Midnight Blue
    public static final Color SIDEBAR_HOVER_COLOR = new Color(52, 73, 94); // Slightly lighter Dark Blue
    public static final Color SIDEBAR_TEXT_COLOR = Color.WHITE;
    
    public static final Color TEXT_DARK = new Color(33, 33, 33);
    public static final Color TEXT_MUTED = new Color(117, 117, 117);
    
    // Alert Colors
    public static final Color COLOR_FRAUD = new Color(231, 76, 60); // Red
    public static final Color COLOR_SUSPICIOUS = new Color(243, 156, 18); // Orange/Yellow
    public static final Color COLOR_NORMAL = new Color(46, 204, 113); // Green

    public static final Color CARD_BACKGROUND = Color.WHITE;

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
}
