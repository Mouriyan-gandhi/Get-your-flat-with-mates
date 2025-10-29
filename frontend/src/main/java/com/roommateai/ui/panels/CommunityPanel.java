package com.roommateai.ui.panels;

import com.roommateai.utils.UIUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Community Panel
 * Community feed for posts, announcements, and social features
 */
public class CommunityPanel extends JPanel {
    
    private String userToken;
    private String userName;
    private String userCollege;
    
    private JList<String> postsList;
    private JTextArea postDetailsArea;
    private JTextField newPostField;
    private JButton postButton;
    private JComboBox<String> categoryComboBox;
    private JLabel statusLabel;
    
    public CommunityPanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    /**
     * Initialize UI components
     */
    private void initializeComponents() {
        postsList = new JList<>();
        postsList.setFont(UIUtils.BODY_FONT);
        postsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        postDetailsArea = UIUtils.createStyledTextArea(10, 40);
        postDetailsArea.setEditable(false);
        
        newPostField = UIUtils.createStyledTextField(30);
        
        categoryComboBox = new JComboBox<>(new String[]{
            "General", "Buy/Sell", "Roommate Search", "Announcement"
        });
        
        postButton = UIUtils.createPrimaryButton("Post");
        
        statusLabel = UIUtils.createStatusLabel("Community ready", false);
    }
    
    /**
     * Setup panel layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = UIUtils.createHeadingLabel("Community Feed");
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        
        // Posts list panel
        JPanel postsPanel = new JPanel(new BorderLayout());
        postsPanel.setBorder(BorderFactory.createTitledBorder("Community Posts"));
        postsPanel.add(new JScrollPane(postsList), BorderLayout.CENTER);
        
        // Post details panel
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Post Details"));
        detailsPanel.add(new JScrollPane(postDetailsArea), BorderLayout.CENTER);
        
        contentPanel.add(postsPanel);
        contentPanel.add(detailsPanel);
        
        // New post panel
        JPanel newPostPanel = new JPanel(new BorderLayout());
        newPostPanel.setBorder(BorderFactory.createTitledBorder("Create New Post"));
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel("Category:"), BorderLayout.WEST);
        inputPanel.add(categoryComboBox, BorderLayout.CENTER);
        
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(new JLabel("Message:"), BorderLayout.WEST);
        messagePanel.add(newPostField, BorderLayout.CENTER);
        messagePanel.add(postButton, BorderLayout.EAST);
        
        newPostPanel.add(inputPanel, BorderLayout.NORTH);
        newPostPanel.add(messagePanel, BorderLayout.CENTER);
        
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout());
        statusPanel.add(statusLabel);
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(newPostPanel, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        postButton.addActionListener(e -> createPost());
        newPostField.addActionListener(e -> createPost());
        
        postsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showPostDetails();
            }
        });
    }
    
    /**
     * Create new post
     */
    private void createPost() {
        String message = newPostField.getText().trim();
        if (message.isEmpty()) {
            UIUtils.showErrorDialog(this, "Error", "Please enter a message");
            return;
        }
        
        String category = (String) categoryComboBox.getSelectedItem();
        
        // TODO: Implement post creation API call
        statusLabel.setText("Posting...");
        newPostField.setText("");
    }
    
    /**
     * Show post details
     */
    private void showPostDetails() {
        String selectedPost = postsList.getSelectedValue();
        if (selectedPost != null) {
            // TODO: Load post details
            postDetailsArea.setText("Loading post details for: " + selectedPost);
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
