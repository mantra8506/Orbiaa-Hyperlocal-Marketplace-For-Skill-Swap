package com.example.skillswap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class UserProfile extends AppCompatActivity {

    private TextView userProfileName, emailTextView, mobileTextView, skillTextView,
            experienceTextView, locationTextView, occupationTextView,
            achievementsTextView, descriptionTextView, workLinkTextView;
    private Button btnSwap;
    private DatabaseReference databaseReference;
    private String userEmail;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize views
        userProfileName = findViewById(R.id.userProfileName);
        emailTextView = findViewById(R.id.emailTextView);
        mobileTextView = findViewById(R.id.mobileTextView);
        skillTextView = findViewById(R.id.skillTextView);
        experienceTextView = findViewById(R.id.experienceTextView);
        locationTextView = findViewById(R.id.locationTextView);
        occupationTextView = findViewById(R.id.occupationTextView);
        achievementsTextView = findViewById(R.id.achievementsTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        workLinkTextView = findViewById(R.id.workLinkTextView);
        btnSwap = findViewById(R.id.btnSwap);
        ImageView btnBack = findViewById(R.id.btnBack);

        // Get userEmail from Intent
        userEmail = getIntent().getStringExtra("userEmail");

        // Get the logged-in user's email from FirebaseAuth
        currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (userEmail != null) {
            fetchUserData(userEmail);
        }

        // Back Button Click
        btnBack.setOnClickListener(v -> finish());

        // Set Swap button click listener
        btnSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSwapRequest();
            }
        });

        workLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = workLinkTextView.getText().toString().trim();
                if (!url.isEmpty() && (url.startsWith("http://") || url.startsWith("https://"))) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(UserProfile.this, "Invalid or missing link", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchUserData(String email) {
        String formattedEmail = email.replace(".", "_"); // Replace dots with underscores
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(formattedEmail);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Retrieve values
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);
                    String fullName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");

                    String email = snapshot.child("email").getValue(String.class);
                    String mobile = snapshot.child("mobile").getValue(String.class);
                    String skill = snapshot.child("personalDetails/skill").getValue(String.class);
                    String experience = snapshot.child("personalDetails/experience").getValue(String.class);
                    String location = snapshot.child("personalDetails/location").getValue(String.class);
                    String occupation = snapshot.child("personalDetails/occupation").getValue(String.class);
                    String achievements = snapshot.child("personalDetails/achievements").getValue(String.class);
                    String description = snapshot.child("personalDetails/description").getValue(String.class);
                    String workLink = snapshot.child("personalDetails/workLink").getValue(String.class);

                    // Set values to TextViews (Avoid setting null values)
                    userProfileName.setText(!fullName.trim().isEmpty() ? fullName : "Unknown");
                    emailTextView.setText(email != null ? email : "N/A");
                    mobileTextView.setText(mobile != null ? mobile : "N/A");
                    skillTextView.setText(skill != null ? skill : "Not specified");
                    experienceTextView.setText(experience != null ? experience : "Not specified");
                    locationTextView.setText(location != null ? location : "Not specified");
                    occupationTextView.setText(occupation != null ? occupation : "Not specified");
                    achievementsTextView.setText(achievements != null ? achievements : "Not specified");
                    descriptionTextView.setText(description != null ? description : "No description available");
                    workLinkTextView.setText(workLink != null ? workLink : "No link provided");
                } else {
                    Log.e("FirebaseData", "No data found for this user.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseData", "Database Error: " + error.getMessage());
            }
        });
    }

    private void sendSwapRequest() {
        if (currentUserEmail == null || userEmail == null) {
            Toast.makeText(UserProfile.this, "Error: User data is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        String senderEmail = currentUserEmail.replace(".", "_");
        String receiverEmail = userEmail.replace(".", "_");

        if (senderEmail.equals(receiverEmail)) {
            Toast.makeText(UserProfile.this, "You can't send a swap request to yourself.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch sender's name from Firebase
        DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference("users").child(senderEmail);

        senderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String senderFirstName = snapshot.child("firstName").getValue(String.class);
                    String senderLastName = snapshot.child("lastName").getValue(String.class);
                    String senderFullName = (senderFirstName != null ? senderFirstName : "") + " " +
                            (senderLastName != null ? senderLastName : "");

                    // Fetch sender's skill
                    String senderSkill = snapshot.child("personalDetails/skill").getValue(String.class);
                    senderSkill = senderSkill != null ? senderSkill : "Unknown Skill";

                    // Create the swap request
                    DatabaseReference swapRequestRef = FirebaseDatabase.getInstance()
                            .getReference("swapRequests").child(receiverEmail).child(senderEmail);

                    SwapRequest swapRequest = new SwapRequest(senderFullName.trim(), senderSkill, "pending");

                    swapRequestRef.setValue(swapRequest)
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(UserProfile.this, "Swap request sent successfully!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(UserProfile.this, "Failed to send swap request.", Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(UserProfile.this, "Error: Could not fetch sender details.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfile.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Create a SwapRequest model class
    public static class SwapRequest {
        public String senderName;
        public String senderSkill;
        public String status;

        public SwapRequest(String senderName, String senderSkill, String status) {
            this.senderName = senderName;
            this.senderSkill = senderSkill;
            this.status = status;
        }
    }
}
