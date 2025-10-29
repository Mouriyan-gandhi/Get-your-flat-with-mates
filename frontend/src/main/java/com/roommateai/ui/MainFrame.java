package com.roommateai.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.roommateai.ui.panels.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main Application Frame
 * Modern Java Swing desktop application for RoomMate.AI
 */
public class MainFrame extends JFrame {
    
    private JTabbedPane tabbedPane;
    private LoginPanel loginPanel;
    private DashboardPanel dashboardPanel;
    private RentalSearchPanel rentalSearchPanel;
    private RoommateMatchPanel roommateMatchPanel;
    private ChatPanel chatPanel;
    private CommunityPanel communityPanel;
    private ProfilePanel profilePanel;
    
    private String currentUserToken;
    private String currentUserName;
    private String currentUserCollege;
    
    public MainFrame() {
        initializeUI();
        setupEventHandlers();
    }
    
    /**
     * Initialize the UI components
     */
    private void initializeUI() {
        // Set application title and icon
        setTitle("RoomMate.AI - Smart Student Rental Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Apply modern theme
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        // Create main layout
        createMainLayout();
        
        // Show login panel initially
        showLoginPanel();
    }
    
    /**
     * Create the main application layout
     */
    private void createMainLayout() {
        setLayout(new BorderLayout());
        
        // Create menu bar
        createMenuBar();
        
        // Create main content area
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create tabbed pane for main features
        tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(JTabbedPane.TOP);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        // Initialize panels
        dashboardPanel = new DashboardPanel();
        rentalSearchPanel = new RentalSearchPanel();
        roommateMatchPanel = new RoommateMatchPanel();
        chatPanel = new ChatPanel();
        communityPanel = new CommunityPanel();
        profilePanel = new ProfilePanel();
        
        // Add panels to tabbed pane
        tabbedPane.addTab("🏠 Dashboard", dashboardPanel);
        tabbedPane.addTab("🔍 Find Rentals", rentalSearchPanel);
        tabbedPane.addTab("👥 Roommates", roommateMatchPanel);
        tabbedPane.addTab("💬 Chat", chatPanel);
        tabbedPane.addTab("📰 Community", communityPanel);
        tabbedPane.addTab("👤 Profile", profilePanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Create login panel
        loginPanel = new LoginPanel();
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    /**
     * Create application menu bar
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        logoutItem.addActionListener(e -> logout());
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // View menu
        JMenu viewMenu = new JMenu("View");
        JMenuItem lightThemeItem = new JMenuItem("Light Theme");
        JMenuItem darkThemeItem = new JMenuItem("Dark Theme");
        
        lightThemeItem.addActionListener(e -> switchTheme(true));
        darkThemeItem.addActionListener(e -> switchTheme(false));
        
        viewMenu.add(lightThemeItem);
        viewMenu.add(darkThemeItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        
        aboutItem.addActionListener(e -> showAboutDialog());
        
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        // Handle login success
        loginPanel.setLoginSuccessListener((token, name, college) -> {
            currentUserToken = token;
            currentUserName = name;
            currentUserCollege = college;
            showMainApplication();
        });
    }
    
    /**
     * Show login panel
     */
    private void showLoginPanel() {
        getContentPane().removeAll();
        add(loginPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
        
        // Hide menu bar during login
        setJMenuBar(null);
    }
    
    /**
     * Show main application after login
     */
    private void showMainApplication() {
        getContentPane().removeAll();
        
        // Show menu bar
        createMenuBar();
        
        // Add main content
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Update user info in panels
        updateUserInfo();
        
        revalidate();
        repaint();
    }
    
    /**
     * Update user information in all panels
     */
    private void updateUserInfo() {
        dashboardPanel.setUserInfo(currentUserToken, currentUserName, currentUserCollege);
        rentalSearchPanel.setUserInfo(currentUserToken, currentUserName, currentUserCollege);
        roommateMatchPanel.setUserInfo(currentUserToken, currentUserName, currentUserCollege);
        chatPanel.setUserInfo(currentUserToken, currentUserName, currentUserCollege);
        communityPanel.setUserInfo(currentUserToken, currentUserName, currentUserCollege);
        profilePanel.setUserInfo(currentUserToken, currentUserName, currentUserCollege);
    }
    
    /**
     * Switch between light and dark themes
     */
    private void switchTheme(boolean light) {
        try {
            if (light) {
                UIManager.setLookAndFeel(new FlatMacLightLaf());
            } else {
                UIManager.setLookAndFeel(new FlatMacDarkLaf());
            }
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Logout user
     */
    private void logout() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Logout",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            currentUserToken = null;
            currentUserName = null;
            currentUserCollege = null;
            showLoginPanel();
        }
    }
    
    /**
     * Show about dialog
     */
    private void showAboutDialog() {
        String aboutText = """
            RoomMate.AI v1.0.0
            
            Smart Student Rental Platform
            
            Features:
            • AI-powered rental search
            • Roommate matching system
            • Real-time chat
            • Rent splitting calculator
            • Community features
            
            Built with Java Swing & Spring Boot
            """;
        
        JOptionPane.showMessageDialog(
            this,
            aboutText,
            "About RoomMate.AI",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Main method to start the application
     */
    public static void main(String[] args) {
        // Set system properties for better UI
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        SwingUtilities.invokeLater(() -> {
            try {
                new MainFrame().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                    null,
                    "Failed to start application: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
