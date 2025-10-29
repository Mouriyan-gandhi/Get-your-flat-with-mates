package com.roommateai.ui.panels;

import com.roommateai.utils.UIUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Dashboard Panel
 * Main dashboard showing user overview and quick actions
 */
public class DashboardPanel extends JPanel {
    
    private String userToken;
    private String userName;
    private String userCollege;
    
    private JLabel welcomeLabel;
    private JLabel collegeLabel;
    private JPanel statsPanel;
    private JPanel quickActionsPanel;
    
    public DashboardPanel() {
        initializeComponents();
        setupLayout();
    }
    
    /**
     * Initialize UI components
     */
    private void initializeComponents() {
        welcomeLabel = UIUtils.createHeadingLabel("Welcome to RoomMate.AI!");
        collegeLabel = UIUtils.createSubheadingLabel("");
        
        statsPanel = createStatsPanel();
        quickActionsPanel = createQuickActionsPanel();
    }
    
    /**
     * Setup panel layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(collegeLabel, BorderLayout.EAST);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.add(statsPanel);
        contentPanel.add(quickActionsPanel);
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Create stats panel
     */
    private JPanel createStatsPanel() {
        JPanel panel = UIUtils.createCardPanel();
        panel.setLayout(new BorderLayout());
        
        JLabel titleLabel = UIUtils.createSubheadingLabel("Your Stats");
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel statsContent = new JPanel(new GridLayout(4, 1, 0, 10));
        
        JLabel rentalsLabel = new JLabel("📋 My Rentals: 0");
        JLabel matchesLabel = new JLabel("👥 Matches: 0");
        JLabel messagesLabel = new JLabel("💬 Unread Messages: 0");
        JLabel reviewsLabel = new JLabel("⭐ Reviews Given: 0");
        
        statsContent.add(rentalsLabel);
        statsContent.add(matchesLabel);
        statsContent.add(messagesLabel);
        statsContent.add(reviewsLabel);
        
        panel.add(statsContent, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create quick actions panel
     */
    private JPanel createQuickActionsPanel() {
        JPanel panel = UIUtils.createCardPanel();
        panel.setLayout(new BorderLayout());
        
        JLabel titleLabel = UIUtils.createSubheadingLabel("Quick Actions");
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel actionsContent = new JPanel(new GridLayout(3, 1, 0, 10));
        
        JButton searchRentalsButton = UIUtils.createPrimaryButton("🔍 Search Rentals");
        JButton findRoommatesButton = UIUtils.createPrimaryButton("👥 Find Roommates");
        JButton postRentalButton = UIUtils.createSuccessButton("📝 Post Rental");
        
        actionsContent.add(searchRentalsButton);
        actionsContent.add(findRoommatesButton);
        actionsContent.add(postRentalButton);
        
        panel.add(actionsContent, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Set user information
     */
    public void setUserInfo(String token, String name, String college) {
        this.userToken = token;
        this.userName = name;
        this.userCollege = college;
        
        welcomeLabel.setText("Welcome back, " + name + "!");
        collegeLabel.setText("🎓 " + college);
        
        // Refresh stats
        refreshStats();
    }
    
    /**
     * Refresh user stats
     */
    private void refreshStats() {
        // TODO: Implement API calls to fetch user stats
        // For now, just show placeholder data
    }
}
