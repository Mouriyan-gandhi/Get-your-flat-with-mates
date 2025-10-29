package com.roommateai.ui.panels;

import com.roommateai.utils.UIUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Chat Panel
 * Real-time chat interface using Firebase
 */
public class ChatPanel extends JPanel {
    
    private String userToken;
    private String userName;
    private String userCollege;
    
    private JList<String> chatList;
    private JTextArea messageArea;
    private JTextField messageField;
    private JButton sendButton;
    private JLabel statusLabel;
    
    public ChatPanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    /**
     * Initialize UI components
     */
    private void initializeComponents() {
        chatList = new JList<>();
        chatList.setFont(UIUtils.BODY_FONT);
        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        messageArea = UIUtils.createStyledTextArea(15, 40);
        messageArea.setEditable(false);
        
        messageField = UIUtils.createStyledTextField(30);
        
        sendButton = UIUtils.createPrimaryButton("Send");
        
        statusLabel = UIUtils.createStatusLabel("Chat ready", false);
    }
    
    /**
     * Setup panel layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = UIUtils.createHeadingLabel("Chat with Your Roommates");
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        
        // Chat list panel
        JPanel chatListPanel = new JPanel(new BorderLayout());
        chatListPanel.setBorder(BorderFactory.createTitledBorder("Active Chats"));
        chatListPanel.add(new JScrollPane(chatList), BorderLayout.CENTER);
        
        // Message panel
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createTitledBorder("Messages"));
        
        messagePanel.add(new JScrollPane(messageArea), BorderLayout.CENTER);
        
        // Message input panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        messagePanel.add(inputPanel, BorderLayout.SOUTH);
        
        contentPanel.add(chatListPanel);
        contentPanel.add(messagePanel);
        
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
        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());
        
        chatList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadChatMessages();
            }
        });
    }
    
    /**
     * Send message
     */
    private void sendMessage() {
        String message = messageField.getText().trim();
        if (message.isEmpty()) {
            return;
        }
        
        // TODO: Implement message sending
        messageField.setText("");
    }
    
    /**
     * Load chat messages
     */
    private void loadChatMessages() {
        String selectedChat = chatList.getSelectedValue();
        if (selectedChat != null) {
            // TODO: Load messages for selected chat
            messageArea.setText("Loading messages for: " + selectedChat);
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
