package com.roommateai.utils;

import javax.swing.*;
import java.awt.*;

/**
 * UI Utility class
 * Common UI helper methods and constants
 */
public class UIUtils {
    
    // Color constants
    public static final Color PRIMARY_COLOR = new Color(0x2196F3);
    public static final Color SECONDARY_COLOR = new Color(0x03DAC6);
    public static final Color SUCCESS_COLOR = new Color(0x4CAF50);
    public static final Color ERROR_COLOR = new Color(0xF44336);
    public static final Color WARNING_COLOR = new Color(0xFF9800);
    public static final Color INFO_COLOR = new Color(0x2196F3);
    
    // Font constants
    public static final Font HEADING_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 18);
    public static final Font SUBHEADING_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    public static final Font BODY_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    public static final Font SMALL_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
    
    /**
     * Create a styled button
     */
    public static JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(BODY_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }
    
    /**
     * Create a primary button
     */
    public static JButton createPrimaryButton(String text) {
        return createStyledButton(text, PRIMARY_COLOR);
    }
    
    /**
     * Create a success button
     */
    public static JButton createSuccessButton(String text) {
        return createStyledButton(text, SUCCESS_COLOR);
    }
    
    /**
     * Create an error button
     */
    public static JButton createErrorButton(String text) {
        return createStyledButton(text, ERROR_COLOR);
    }
    
    /**
     * Create a styled text field
     */
    public static JTextField createStyledTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(BODY_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return textField;
    }
    
    /**
     * Create a styled password field
     */
    public static JPasswordField createStyledPasswordField(int columns) {
        JPasswordField passwordField = new JPasswordField(columns);
        passwordField.setFont(BODY_FONT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return passwordField;
    }
    
    /**
     * Create a styled text area
     */
    public static JTextArea createStyledTextArea(int rows, int columns) {
        JTextArea textArea = new JTextArea(rows, columns);
        textArea.setFont(BODY_FONT);
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return textArea;
    }
    
    /**
     * Create a styled label
     */
    public static JLabel createStyledLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }
    
    /**
     * Create a heading label
     */
    public static JLabel createHeadingLabel(String text) {
        return createStyledLabel(text, HEADING_FONT, PRIMARY_COLOR);
    }
    
    /**
     * Create a subheading label
     */
    public static JLabel createSubheadingLabel(String text) {
        return createStyledLabel(text, SUBHEADING_FONT, Color.BLACK);
    }
    
    /**
     * Create a status label
     */
    public static JLabel createStatusLabel(String text, boolean isError) {
        return createStyledLabel(text, BODY_FONT, isError ? ERROR_COLOR : SUCCESS_COLOR);
    }
    
    /**
     * Create a card panel with shadow effect
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setBackground(Color.WHITE);
        return panel;
    }
    
    /**
     * Create a loading panel
     */
    public static JPanel createLoadingPanel(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel loadingLabel = new JLabel(message);
        loadingLabel.setFont(BODY_FONT);
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(false);
        
        panel.add(loadingLabel, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Show error dialog
     */
    public static void showErrorDialog(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Show success dialog
     */
    public static void showSuccessDialog(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Show confirmation dialog
     */
    public static boolean showConfirmDialog(Component parent, String title, String message) {
        int result = JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Center component on screen
     */
    public static void centerComponent(Component component) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension componentSize = component.getSize();
        
        int x = (screenSize.width - componentSize.width) / 2;
        int y = (screenSize.height - componentSize.height) / 2;
        
        component.setLocation(x, y);
    }
    
    /**
     * Format currency
     */
    public static String formatCurrency(double amount) {
        return String.format("₹%.0f", amount);
    }
    
    /**
     * Format date
     */
    public static String formatDate(java.util.Date date) {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd MMM yyyy");
        return formatter.format(date);
    }
    
    /**
     * Format date and time
     */
    public static String formatDateTime(java.util.Date date) {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd MMM yyyy HH:mm");
        return formatter.format(date);
    }
}
