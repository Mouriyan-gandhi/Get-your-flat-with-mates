package com.roommateai.ui.panels;

import com.roommateai.utils.UIUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Rental Search Panel
 * AI-powered rental search with natural language processing
 */
public class RentalSearchPanel extends JPanel {
    
    private String userToken;
    private String userName;
    private String userCollege;
    
    private JTextField searchField;
    private JButton searchButton;
    private JButton aiSearchButton;
    private JList<String> resultsList;
    private JTextArea detailsArea;
    private JLabel statusLabel;
    
    public RentalSearchPanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    /**
     * Initialize UI components
     */
    private void initializeComponents() {
        // Search field
        searchField = UIUtils.createStyledTextField(30);
        searchField.setToolTipText("Enter search terms or use natural language (e.g., 'Find a room near SRM under ₹10k with AC')");
        
        // Buttons
        searchButton = UIUtils.createPrimaryButton("🔍 Search");
        aiSearchButton = UIUtils.createSuccessButton("🤖 AI Search");
        
        // Results list
        resultsList = new JList<>();
        resultsList.setFont(UIUtils.BODY_FONT);
        resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Details area
        detailsArea = UIUtils.createStyledTextArea(10, 30);
        detailsArea.setEditable(false);
        
        // Status label
        statusLabel = UIUtils.createStatusLabel("Ready to search", false);
    }
    
    /**
     * Setup panel layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = UIUtils.createHeadingLabel("Find Your Perfect Rental");
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Rentals"));
        
        JPanel searchInputPanel = new JPanel(new BorderLayout());
        searchInputPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        searchInputPanel.add(searchField, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(searchButton);
        buttonPanel.add(aiSearchButton);
        
        searchPanel.add(searchInputPanel, BorderLayout.NORTH);
        searchPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        
        // Results panel
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Search Results"));
        resultsPanel.add(new JScrollPane(resultsList), BorderLayout.CENTER);
        
        // Details panel
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Rental Details"));
        detailsPanel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        
        contentPanel.add(resultsPanel);
        contentPanel.add(detailsPanel);
        
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout());
        statusPanel.add(statusLabel);
        
        add(headerPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        // Search button
        searchButton.addActionListener(e -> performSearch());
        
        // AI Search button
        aiSearchButton.addActionListener(e -> performAiSearch());
        
        // Enter key on search field
        searchField.addActionListener(e -> performSearch());
        
        // List selection
        resultsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showRentalDetails();
            }
        });
    }
    
    /**
     * Perform regular search
     */
    private void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            UIUtils.showErrorDialog(this, "Error", "Please enter a search term");
            return;
        }
        
        statusLabel.setText("Searching...");
        // TODO: Implement API call for regular search
    }
    
    /**
     * Perform AI-powered search
     */
    private void performAiSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            UIUtils.showErrorDialog(this, "Error", "Please enter a search query");
            return;
        }
        
        statusLabel.setText("AI is processing your request...");
        // TODO: Implement API call for AI search
    }
    
    /**
     * Show rental details
     */
    private void showRentalDetails() {
        String selectedItem = resultsList.getSelectedValue();
        if (selectedItem != null) {
            // TODO: Fetch and display rental details
            detailsArea.setText("Loading rental details...");
        }
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
