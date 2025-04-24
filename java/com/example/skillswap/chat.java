package com.example.skillswap;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class chat extends AppCompatActivity {

    private EditText messageInput;
    private ImageView sendButton, btnBack;
    private RecyclerView recyclerViewMessages;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private DatabaseReference chatReference;
    private String senderEmail, receiverEmail, chatId;
    private View scheduleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageInput = findViewById(R.id.editTextMessage);
        scheduleButton = findViewById(R.id.ScheduleBtn);
        sendButton = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatMessages);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(chatAdapter);

        senderEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        receiverEmail = getIntent().getStringExtra("receiverEmail");

        if (senderEmail == null || receiverEmail == null) {
            Toast.makeText(this, "Error: User data missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        chatId = generateChatId(senderEmail, receiverEmail);
        chatReference = FirebaseDatabase.getInstance().getReference("chats").child(chatId);

        loadChatMessages();

        sendButton.setOnClickListener(v -> sendMessage());
        btnBack.setOnClickListener(v -> finish());

        scheduleButton.setOnClickListener(v -> {
            Intent intent = new Intent(chat.this, ScheduleClass.class);
            startActivityForResult(intent, 1);
        });
    }

    private void loadChatMessages() {
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatMessages.clear();
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    ChatMessage message = messageSnapshot.getValue(ChatMessage.class);
                    chatMessages.add(message);
                }
                chatAdapter.notifyDataSetChanged();
                recyclerViewMessages.scrollToPosition(chatMessages.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(chat.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (TextUtils.isEmpty(messageText)) {
            return;
        }

        sendChatMessage(messageText);
        messageInput.setText("");
    }

    private void sendChatMessage(String messageText) {
        String messageId = chatReference.push().getKey();
        ChatMessage chatMessage = new ChatMessage(senderEmail, receiverEmail, messageText, System.currentTimeMillis());

        Map<String, Object> messageValues = new HashMap<>();
        messageValues.put(messageId, chatMessage);

        chatReference.updateChildren(messageValues);
    }

    private String generateChatId(String email1, String email2) {
        String formattedEmail1 = email1.replace(".", "_");
        String formattedEmail2 = email2.replace(".", "_");
        return (formattedEmail1.compareTo(formattedEmail2) < 0) ? formattedEmail1 + "_" + formattedEmail2 : formattedEmail2 + "_" + formattedEmail1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String date = data.getStringExtra("date");
            String time = data.getStringExtra("time");
            String meetingLink = data.getStringExtra("meetingLink");
            String skill = data.getStringExtra("skill");
            String topics = data.getStringExtra("topics");

            String scheduleMessage = "Scheduled Class\n" +
                    " Skill: " + skill + "\n" +
                    " Date: " + date + "\n" +
                    " Time: " + time + "\n" +
                    " Link: " + meetingLink;

            if (topics != null && !topics.isEmpty()) {
                scheduleMessage += "\n Topics to Learn:\n" + topics;
            }

            sendChatMessage(scheduleMessage);
        }
    }

}
