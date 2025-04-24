package com.example.skillswap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class requests extends AppCompatActivity {

    private LinearLayout requestContainer;
    private DatabaseReference databaseReference;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        requestContainer = findViewById(R.id.requestContainer);
        currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (currentUserEmail != null) {
            fetchSwapRequests();
        }
    }

    private void fetchSwapRequests() {
        String formattedEmail = currentUserEmail.replace(".", "_");
        databaseReference = FirebaseDatabase.getInstance().getReference("swapRequests").child(formattedEmail);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestContainer.removeAllViews(); // Clear previous views

                for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                    String senderEmail = requestSnapshot.getKey();
                    String senderName = requestSnapshot.child("senderName").getValue(String.class);
                    String senderSkill = requestSnapshot.child("senderSkill").getValue(String.class);
                    String status = requestSnapshot.child("status").getValue(String.class);

                    if (senderName != null && senderSkill != null) {
                        addRequestView(senderEmail, senderName, senderSkill, status);

                        // If request is "accepted", send back a request automatically
                        if ("accepted".equals(status)) {
                            sendBackRequest(senderEmail, senderSkill);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requests.this, "Failed to load requests.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendBackRequest(String senderEmail, String senderSkill) {
        String formattedEmail = currentUserEmail.replace(".", "_");
        String senderFormattedEmail = senderEmail.replace(".", "_");

        DatabaseReference senderRequestRef = FirebaseDatabase.getInstance().getReference("swapRequests")
                .child(senderFormattedEmail).child(formattedEmail);

        senderRequestRef.child("senderName").setValue(currentUserEmail)
                .addOnSuccessListener(aVoid -> {
                    senderRequestRef.child("senderSkill").setValue(senderSkill); // Set the skill
                    senderRequestRef.child("status").setValue("accepted");
                    Log.d("Firebase", "Request sent back and accepted successfully.");
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to send request back.", e));
    }

    private void addRequestView(String senderEmail, String senderName, String senderSkill, String status) {
        View requestView = getLayoutInflater().inflate(R.layout.request_item, null);

        TextView senderNameTextView = requestView.findViewById(R.id.senderNameTextView);
        TextView senderSkillTextView = requestView.findViewById(R.id.senderSkillTextView);
        Button btnAction = requestView.findViewById(R.id.btnAccept);
        Button btnDecline = requestView.findViewById(R.id.btnDecline);

        senderNameTextView.setText(senderName);
        senderSkillTextView.setText("Skill: " + senderSkill);

        if ("accepted".equals(status)) {
            // Change "ACCEPT" button to "MESSAGE"
            btnAction.setText("MESSAGE");
            btnAction.setOnClickListener(v -> openChat(senderEmail));
        } else {
            // Show "ACCEPT" button
            btnAction.setText("ACCEPT");
            btnAction.setOnClickListener(v -> acceptSwapRequest(senderEmail, senderSkill, btnAction));
        }

        btnDecline.setOnClickListener(v -> declineSwapRequest(senderEmail));

        requestContainer.addView(requestView);
    }

    private void acceptSwapRequest(String senderEmail, String senderSkill, Button btnAction) {
        String formattedEmail = currentUserEmail.replace(".", "_");
        String senderFormattedEmail = senderEmail.replace(".", "_");

        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("swapRequests")
                .child(formattedEmail).child(senderFormattedEmail);

        // Update current user's request to "accepted"
        requestRef.child("status").setValue("accepted")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requests.this, "Request Accepted!", Toast.LENGTH_SHORT).show();
                    btnAction.setText("MESSAGE");
                    btnAction.setOnClickListener(v -> openChat(senderEmail));

                    // Automatically send the request back and accept it
                    autoAcceptForSender(senderEmail, senderSkill);
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to accept request.", e));
    }

    private void autoAcceptForSender(String senderEmail, String senderSkill) {
        String senderFormattedEmail = senderEmail.replace(".", "_");
        String formattedEmail = currentUserEmail.replace(".", "_");

        DatabaseReference senderRequestRef = FirebaseDatabase.getInstance().getReference("swapRequests")
                .child(senderFormattedEmail).child(formattedEmail);

        senderRequestRef.child("senderName").setValue(currentUserEmail)
                .addOnSuccessListener(aVoid -> {
                    senderRequestRef.child("senderSkill").setValue(senderSkill);
                    senderRequestRef.child("status").setValue("accepted");
                    Log.d("Firebase", "Auto-accepted request for sender.");
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to auto-accept request.", e));
    }

    private void declineSwapRequest(String senderEmail) {
        String formattedEmail = currentUserEmail.replace(".", "_");
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("swapRequests")
                .child(formattedEmail).child(senderEmail.replace(".", "_"));

        requestRef.removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(requests.this, "Request Declined!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to decline request.", e));
    }

    private void openChat(String senderEmail) {
        Intent intent = new Intent(requests.this, chat.class);
        intent.putExtra("receiverEmail", senderEmail.replace("_", "."));
        startActivity(intent);
    }
}
