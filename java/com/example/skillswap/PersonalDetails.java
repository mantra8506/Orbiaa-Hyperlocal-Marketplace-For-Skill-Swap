package com.example.skillswap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PersonalDetails extends AppCompatActivity {

    private EditText occupationEditText, skillEditText, experienceEditText, locationEditText,
            workLinkEditText, descriptionEditText, achievementsEditText;
    private Button submitButton;
    private DatabaseReference databaseReference;
    private String userEmail; // Email used as Firebase key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        // Initialize UI elements
        occupationEditText = findViewById(R.id.occupation);
        skillEditText = findViewById(R.id.skill);
        experienceEditText = findViewById(R.id.rexperience);
        locationEditText = findViewById(R.id.rlocation);
        workLinkEditText = findViewById(R.id.rwl);
        descriptionEditText = findViewById(R.id.rdescription);
        achievementsEditText = findViewById(R.id.rachievemnets);
        submitButton = findViewById(R.id.submit_button);

        // Get email from intent (passed from Login or Signup)
        userEmail = getIntent().getStringExtra("email");
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Error: Email not found!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Convert email to Firebase-friendly format
        String safeEmail = userEmail.replace(".", "_");

        // Debug log
        Log.d("PersonalDetails", "Safe Email Key: " + safeEmail);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Set up the submit button listener
        submitButton.setOnClickListener(v -> saveDataToFirebase(safeEmail));
    }

    private void saveDataToFirebase(String safeEmail) {
        // Get user input
        String occupation = occupationEditText.getText().toString().trim();
        String skill = skillEditText.getText().toString().trim();
        String experience = experienceEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String workLink = workLinkEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String achievements = achievementsEditText.getText().toString().trim();

        // Check if any field is empty
        if (occupation.isEmpty() || skill.isEmpty() || experience.isEmpty() || location.isEmpty() ||
                workLink.isEmpty() || description.isEmpty() || achievements.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a user details object
        UserDetails userDetails = new UserDetails(userEmail, occupation, skill, experience, location, workLink, description, achievements);

        // Store data in Firebase under users/{email}/personalDetails
        databaseReference.child(safeEmail).child("personalDetails")
                .setValue(userDetails)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // ✅ Store email in SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userEmail", userEmail);
                        editor.apply();

                        Toast.makeText(PersonalDetails.this, "Data saved successfully", Toast.LENGTH_SHORT).show();

                        // Navigate to Dashboard
                        Intent intent = new Intent(PersonalDetails.this, Dashboard.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(PersonalDetails.this, "Failed to save data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
