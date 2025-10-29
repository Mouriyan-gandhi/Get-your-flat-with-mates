package com.roommateai.ui.panels;

import com.roommateai.utils.UIUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Roommate Match Panel
 * Tinder-like roommate matching interface
 */
public class RoommateMatchPanel extends JPanel {
    
    private String userToken;
    private String userName;
    private String userCollege;
    
    private JLabel titleLabel;
    private JPanel cardPanel;
    private JButton likeButton;
    private JButton passButton;
    private JLabel statusLabel;
    
    public RoommateMatchPanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    /**
     * Initialize UI components
     */
    private void initializeComponents() {
        titleLabel = UIUtils.createHeadingLabel("Find Your Perfect Roommate");
        
        cardPanel = new JPanel(new BorderLayout());
        cardPanel.setPreferredSize(new Dimension(400, 500));
        cardPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        
        likeButton = UIUtils.createSuccessButton("❤️ Like");
        passButton = UIUtils.createErrorButton("👎 Pass");
        
        statusLabel = UIUtils.createStatusLabel("Loading potential roommates...", false);
    }
    
    /**
     * Setup panel layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        
        // Card panel (center)
        JPanel cardContainer = new JPanel(new FlowLayout());
        cardContainer.add(cardPanel);
        contentPanel.add(cardContainer, BorderLayout.CENTER);
        
        // Action buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(passButton);
        buttonPanel.add(likeButton);
        
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout());
        statusPanel.add(statusLabel);
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        likeButton.addActionListener(e -> performLike());
        passButton.addActionListener(e -> performPass());
    }
    
    /**
     * Perform like action
     */
    private void performLike() {
        statusLabel.setText("Processing like...");
        // TODO: Implement like API call
    }
    
    /**
     * Perform pass action
     */
    private void performPass() {
        statusLabel.setText("Processing pass...");
        // TODO: Implement pass API call
    }
    
    /**
     * Set user information
     */
    public void setUserInfo(String token, String name, String college) {
        this.userToken = token;
        this.userName = name;
        this.userCollege = college;
    }
}
