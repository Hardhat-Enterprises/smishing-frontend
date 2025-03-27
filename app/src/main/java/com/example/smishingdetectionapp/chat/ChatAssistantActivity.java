package com.example.smishingdetectionapp.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.detections.DatabaseAccess;

public class ChatAssistantActivity extends AppCompatActivity {
    // UI Components
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ChatAdapter chatAdapter;
    private OllamaClient ollamaClient;
    private ProgressBar progressBar;

    /**
     * Initializes the activity and sets up the UI components
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this); // Enable edge-to-edge display
            setContentView(R.layout.activity_chat_assistant);

        // Initialize and setup UI components
            initializeViews();
            setupRecyclerView();
            setupClickListeners();
            

        } catch (Exception e) {
            // Log error and close activity if initialization fails
            Log.e("ChatAssistantActivity", "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "Error initializing chat", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

        /**
     * Initializes all UI components and the Ollama client
     * Throws exception if required views are not found
     */

    private void initializeViews() {
        try {
            // Find and assign all UI components
            chatRecyclerView = findViewById(R.id.chatRecyclerView);
            messageInput = findViewById(R.id.messageInput);
            sendButton = findViewById(R.id.sendButton);
            progressBar = findViewById(R.id.progressBar);

        // Initialize database access and Ollama client
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        ollamaClient = new OllamaClient(databaseAccess);

            if (chatRecyclerView == null || messageInput == null || sendButton == null || progressBar == null) {
                throw new IllegalStateException("Required views not found in layout");
            }
        } catch (Exception e) {
            Log.e("ChatAssistantActivity", "Error initializing views: " + e.getMessage());
            throw e;
        }
    }
        /**
     * Sets up the RecyclerView with the chat adapter
     */

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(this);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);
    }
        /**
     * Sets up click listeners for back button and send button
     */

    private void setupClickListeners() {
        ImageButton backButton = findViewById(R.id.chat_back);
        backButton.setOnClickListener(v -> finish());

        sendButton.setOnClickListener(v -> sendMessage());
    }

    /**
     * Handles sending a message:
     * 1. Adds user message to chat
     * 2. Clears input field
     * 3. Shows loading indicator
     * 4. Gets response from Ollama client
     * 5. Adds bot response to chat
     */

    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (!message.isEmpty()) {
            // Add user message and clear input
            chatAdapter.addMessage(new ChatMessage(message, ChatMessage.USER_MESSAGE));
            messageInput.setText("");
            // Show loading state
            progressBar.setVisibility(View.VISIBLE);
            sendButton.setEnabled(false);
            // Get response from Ollama client
            ollamaClient.getResponse(message, response -> {
                runOnUiThread(() -> {
                      // Hide loading state
                    progressBar.setVisibility(View.GONE);
                    sendButton.setEnabled(true);
                    // Add bot response and scroll to bottom
                    chatAdapter.addMessage(new ChatMessage(response, ChatMessage.BOT_MESSAGE));
                    chatRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                });
            });
        }
    }
}