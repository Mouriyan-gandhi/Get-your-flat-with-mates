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
 * Login Panel
 * Handles user authentication with college email validation
 */
public class LoginPanel extends JPanel {
    
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private JLabel statusLabel;
    private JCheckBox rememberMeCheckBox;
    
    private LoginSuccessListener loginSuccessListener;
    private ApiService apiService;
    private ObjectMapper objectMapper;
    
    public interface LoginSuccessListener {
        void onLoginSuccess(String token, String name, String college);
    }
    
    public LoginPanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        apiService = new ApiService();
        objectMapper = new ObjectMapper();
    }
    
    /**
     * Initialize UI components
     */
    private void initializeComponents() {
        // Email field
        emailField = new JTextField(20);
        emailField.setToolTipText("Enter your college email address");
        
        // Password field
        passwordField = new JPasswordField(20);
        passwordField.setToolTipText("Enter your password");
        
        // Buttons
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 35));
        
        signupButton = new JButton("Sign Up");
        signupButton.setPreferredSize(new Dimension(100, 35));
        
        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        
        // Remember me checkbox
        rememberMeCheckBox = new JCheckBox("Remember me");
    }
    
    /**
     * Setup panel layout
     */
    private void setupLayout() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Title
        JLabel titleLabel = new JLabel("RoomMate.AI");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
        titleLabel.setForeground(new Color(0x2196F3));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Smart Student Rental Platform");
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 14));
        subtitleLabel.setForeground(Color.GRAY);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        add(subtitleLabel, gbc);
        
        // Email label and field
        JLabel emailLabel = new JLabel("College Email:");
        emailLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(emailLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(emailField, gbc);
        
        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        add(passwordLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        add(passwordField, gbc);
        
        // Remember me checkbox
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        add(rememberMeCheckBox, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);
        
        // Status label
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        add(statusLabel, gbc);
        
        // Info panel
        JPanel infoPanel = createInfoPanel();
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        add(infoPanel, gbc);
    }
    
    /**
     * Create info panel with college email requirements
     */
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("College Email Requirements"));
        
        JTextArea infoText = new JTextArea(4, 30);
        infoText.setText("""
            Only students with verified college email addresses can register.
            
            Supported domains: .edu, .ac.in, .edu.in, srmuniv.ac.in, vit.ac.in, iit.ac.in, nit.ac.in
            
            Example: john.doe@srmuniv.ac.in
            """);
        infoText.setEditable(false);
        infoText.setOpaque(false);
        infoText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        
        infoPanel.add(infoText, BorderLayout.CENTER);
        
        return infoPanel;
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        // Login button
        loginButton.addActionListener(e -> performLogin());
        
        // Signup button
        signupButton.addActionListener(e -> showSignupDialog());
        
        // Enter key on password field
        passwordField.addActionListener(e -> performLogin());
        
        // Enter key on email field
        emailField.addActionListener(e -> passwordField.requestFocus());
    }
    
    /**
     * Perform user login
     */
    private void performLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (email.isEmpty() || password.isEmpty()) {
            showStatus("Please enter both email and password", true);
            return;
        }
        
        if (!isValidEmail(email)) {
            showStatus("Please enter a valid email address", true);
            return;
        }
        
        // Disable buttons during login
        setButtonsEnabled(false);
        showStatus("Logging in...", false);
        
        // Perform login in background thread
        SwingUtilities.invokeLater(() -> {
            try {
                Map<String, String> loginData = new HashMap<>();
                loginData.put("email", email);
                loginData.put("password", password);
                
                String response = apiService.post("/api/auth/login", loginData);
                Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
                
                if (responseMap.containsKey("token")) {
                    String token = (String) responseMap.get("token");
                    String name = (String) responseMap.get("name");
                    String college = (String) responseMap.get("college");
                    
                    showStatus("Login successful!", false);
                    
                    // Clear fields
                    emailField.setText("");
                    passwordField.setText("");
                    
                    // Notify success
                    if (loginSuccessListener != null) {
                        loginSuccessListener.onLoginSuccess(token, name, college);
                    }
                } else {
                    String error = (String) responseMap.get("error");
                    String message = (String) responseMap.get("message");
                    showStatus(error + ": " + message, true);
                }
                
            } catch (Exception ex) {
                showStatus("Login failed: " + ex.getMessage(), true);
            } finally {
                setButtonsEnabled(true);
            }
        });
    }
    
    /**
     * Show signup dialog
     */
    private void showSignupDialog() {
        SignupDialog signupDialog = new SignupDialog((JFrame) SwingUtilities.getWindowAncestor(this));
        signupDialog.setSignupSuccessListener((token, name, college) -> {
            if (loginSuccessListener != null) {
                loginSuccessListener.onLoginSuccess(token, name, college);
            }
        });
        signupDialog.setVisible(true);
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
        loginButton.setEnabled(enabled);
        signupButton.setEnabled(enabled);
    }
    
    /**
     * Set login success listener
     */
    public void setLoginSuccessListener(LoginSuccessListener listener) {
        this.loginSuccessListener = listener;
    }
}
