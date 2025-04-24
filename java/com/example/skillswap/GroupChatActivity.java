package com.example.skillswap;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.List;

public class GroupChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText editMessage;
    private Button btnSend;
    private DatabaseReference chatRef;
    private String groupName;
    private String currentUser;
    private List<Message> messageList;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);

        groupName = getIntent().getStringExtra("groupName");
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        chatRef = FirebaseDatabase.getInstance().getReference("GroupChats").child(groupName);
        messageList = new ArrayList<>();

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter(this, messageList);
        chatRecyclerView.setAdapter(messageAdapter);

        btnSend.setOnClickListener(v -> sendMessage());
        loadMessages();
    }

    private void sendMessage() {
        String messageText = editMessage.getText().toString().trim();
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        String messageId = chatRef.push().getKey();
        Message message = new Message(currentUser, messageText);

        chatRef.child(messageId).setValue(message)
                .addOnSuccessListener(aVoid -> editMessage.setText(""))
                .addOnFailureListener(e -> Toast.makeText(GroupChatActivity.this, "Message failed", Toast.LENGTH_SHORT).show());
    }

    private void loadMessages() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    Message message = messageSnapshot.getValue(Message.class);
                    if (message != null) {
                        messageList.add(message);
                    }
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GroupChatActivity.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
