package com.roommateai.ui.panels;

import com.roommateai.utils.UIUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Profile Panel
 * User profile management and settings
 */
public class ProfilePanel extends JPanel {
    
    private String userToken;
    private String userName;
    private String userCollege;
    
    private JTextField nameField;
    private JTextField emailField;
    private JTextField collegeField;
    private JTextField phoneField;
    private JTextArea bioArea;
    private JButton updateButton;
    private JLabel statusLabel;
    
    public ProfilePanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    /**
     * Initialize UI components
     */
    private void initializeComponents() {
        nameField = UIUtils.createStyledTextField(20);
        emailField = UIUtils.createStyledTextField(20);
        emailField.setEditable(false); // Email cannot be changed
        
        collegeField = UIUtils.createStyledTextField(20);
        phoneField = UIUtils.createStyledTextField(20);
        
        bioArea = UIUtils.createStyledTextArea(5, 30);
        
        updateButton = UIUtils.createPrimaryButton("Update Profile");
        
        statusLabel = UIUtils.createStatusLabel("Profile ready", false);
    }
    
    /**
     * Setup panel layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = UIUtils.createHeadingLabel("My Profile");
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createTitledBorder("Profile Information"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        contentPanel.add(nameField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 1;
        contentPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        contentPanel.add(emailField, gbc);
        
        // College
        gbc.gridx = 0; gbc.gridy = 2;
        contentPanel.add(new JLabel("College:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        contentPanel.add(collegeField, gbc);
        
        // Phone
        gbc.gridx = 0; gbc.gridy = 3;
        contentPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        contentPanel.add(phoneField, gbc);
        
        // Bio
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        contentPanel.add(new JLabel("Bio:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(new JScrollPane(bioArea), gbc);
        
        // Update button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(updateButton);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(buttonPanel, gbc);
        
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
        updateButton.addActionListener(e -> updateProfile());
    }
    
    /**
     * Update user profile
     */
    private void updateProfile() {
        String name = nameField.getText().trim();
        String college = collegeField.getText().trim();
        String phone = phoneField.getText().trim();
        String bio = bioArea.getText().trim();
        
        if (name.isEmpty() || college.isEmpty()) {
            UIUtils.showErrorDialog(this, "Error", "Name and College are required");
            return;
        }
        
        // TODO: Implement profile update API call
        statusLabel.setText("Updating profile...");
    }
    
    /**
     * Set user information
     */
    public void setUserInfo(String token, String name, String college) {
        this.userToken = token;
        this.userName = name;
        this.userCollege = college;
        
        // Populate fields
        nameField.setText(name);
        emailField.setText(""); // Will be populated from API
        collegeField.setText(college);
        
        // TODO: Load full profile data from API
    }
}
