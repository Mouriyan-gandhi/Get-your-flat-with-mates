package com.roommateai.ui.panels;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roommateai.services.ApiService;
import com.roommateai.utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Signup Dialog
 * Handles user registration with college email validation
 */
public class SignupDialog extends JDialog {
    
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField collegeField;
    private JTextField phoneField;
    private JTextArea bioArea;
    private JButton signupButton;
    private JButton cancelButton;
    private JLabel statusLabel;
    
    private SignupSuccessListener signupSuccessListener;
    private ApiService apiService;
    private ObjectMapper objectMapper;
    
    public interface SignupSuccessListener {
        void onSignupSuccess(String token, String name, String college);
    }
    
    public SignupDialog(JFrame parent) {
        super(parent, "Sign Up - RoomMate.AI", true);
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        apiService = new ApiService();
        objectMapper = new ObjectMapper();
        
        setSize(500, 600);
        setLocationRelativeTo(parent);
    }
    
    /**
     * Initialize UI components
     */
    private void initializeComponents() {
        // Text fields
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        collegeField = new JTextField(20);
        phoneField = new JTextField(20);
        
        // Bio text area
        bioArea = new JTextArea(3, 20);
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        
        // Buttons
        signupButton = new JButton("Sign Up");
        signupButton.setPreferredSize(new Dimension(100, 35));
        
        cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        
        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
    }
    
    /**
     * Setup dialog layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        JLabel titleLabel = new JLabel("Create Your Account");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titleLabel.setForeground(new Color(0x2196F3));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);
        
        // Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(nameField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("College Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(emailField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(passwordField, gbc);
        
        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(confirmPasswordField, gbc);
        
        // College
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("College:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(collegeField, gbc);
        
        // Phone
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Phone (Optional):"), gbc);
        gbc.gridx = 1; gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(phoneField, gbc);
        
        // Bio
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        mainPanel.add(new JLabel("Bio (Optional):"), gbc);
        gbc.gridx = 1; gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JScrollPane(bioArea), gbc);
        
        // Status label
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(statusLabel, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(signupButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Info panel
        add(createInfoPanel(), BorderLayout.SOUTH);
    }
    
    /**
     * Create info panel with college email requirements
     */
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("College Email Requirements"));
        
        JTextArea infoText = new JTextArea(3, 40);
        infoText.setText("""
            Only students with verified college email addresses can register.
            Supported domains: .edu, .ac.in, .edu.in, srmuniv.ac.in, vit.ac.in, iit.ac.in, nit.ac.in
            """);
        infoText.setEditable(false);
        infoText.setOpaque(false);
        infoText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        
        infoPanel.add(infoText, BorderLayout.CENTER);
        
        return infoPanel;
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        // Signup button
        signupButton.addActionListener(e -> performSignup());
        
        // Cancel button
        cancelButton.addActionListener(e -> dispose());
        
        // Enter key on confirm password field
        confirmPasswordField.addActionListener(e -> performSignup());
    }
    
    /**
     * Perform user signup
     */
    private void performSignup() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String college = collegeField.getText().trim();
        String phone = phoneField.getText().trim();
        String bio = bioArea.getText().trim();
        
        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || college.isEmpty()) {
            showStatus("Please fill in all required fields", true);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showStatus("Passwords do not match", true);
            return;
        }
        
        if (password.length() < 6) {
            showStatus("Password must be at least 6 characters long", true);
            return;
        }
        
        if (!isValidEmail(email)) {
            showStatus("Please enter a valid email address", true);
            return;
        }
        
        // Disable buttons during signup
        setButtonsEnabled(false);
        showStatus("Creating account...", false);
        
        // Perform signup in background thread
        SwingUtilities.invokeLater(() -> {
            try {
                Map<String, String> signupData = new HashMap<>();
                signupData.put("name", name);
                signupData.put("email", email);
                signupData.put("password", password);
                signupData.put("college", college);
                signupData.put("phone", phone);
                signupData.put("bio", bio);
                
                String response = apiService.post("/api/auth/signup", signupData);
                Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
                
                if (responseMap.containsKey("token")) {
                    String token = (String) responseMap.get("token");
                    String userName = (String) responseMap.get("name");
                    String userCollege = (String) responseMap.get("college");
                    
                    showStatus("Account created successfully!", false);
                    
                    // Notify success
                    if (signupSuccessListener != null) {
                        signupSuccessListener.onSignupSuccess(token, userName, userCollege);
                    }
                    
                    // Close dialog after a short delay
                    Timer timer = new Timer(1000, e -> dispose());
                    timer.setRepeats(false);
                    timer.start();
                } else {
                    String error = (String) responseMap.get("error");
                    String message = (String) responseMap.get("message");
                    showStatus(error + ": " + message, true);
                }
                
            } catch (Exception ex) {
                showStatus("Signup failed: " + ex.getMessage(), true);
            } finally {
                setButtonsEnabled(true);
            }
        });
    }
    
    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
    
    /**
     * Show status message
     */
    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? Color.RED : Color.GREEN);
    }
    
    /**
     * Enable/disable buttons
     */
    private void setButtonsEnabled(boolean enabled) {
        signupButton.setEnabled(enabled);
        cancelButton.setEnabled(enabled);
    }
    
    /**
     * Set signup success listener
     */
    public void setSignupSuccessListener(SignupSuccessListener listener) {
        this.signupSuccessListener = listener;
    }
}
