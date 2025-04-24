package com.example.skillswap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PersonalDetails2 extends AppCompatActivity {

    private static final int IMAGE_PICK_CODE = 1000;
    private ImageView uploadImageView;
    private Uri selectedImageUri;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details2);

        // Receiving data from PersonalDetails.java
        Intent intent = getIntent();
        userEmail = intent.getStringExtra("email");
        String occupation = intent.getStringExtra("occupation");
        String skill = intent.getStringExtra("skill");
        String experience = intent.getStringExtra("experience"); // Store experience as String
        String achievements = intent.getStringExtra("achievements");
        String location = intent.getStringExtra("location");
        String workLink = intent.getStringExtra("workLink");
        String description = intent.getStringExtra("description");

        // Validate email
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Error: Email is missing!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Convert email to Firebase-friendly format
        String safeEmail = userEmail.replace(".", "_");

        // Debug log
        Log.d("PersonalDetails2", "Safe Email Key: " + safeEmail);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference().child("profile_images");


        // Initialize views
        uploadImageView = findViewById(R.id.rnvmyp00rdhr);
        TextView uploadTextView = findViewById(R.id.rhmxhwga1l15);
        LinearLayout doneButton = findViewById(R.id.r2eo8chrg8mq);

        // Open gallery when clicking on image
        uploadImageView.setOnClickListener(v -> openGallery());
        uploadTextView.setOnClickListener(v -> openGallery());

        // Save data to Firebase on button click
        doneButton.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                uploadImageToFirebase(safeEmail, occupation, skill, experience, location, workLink, description, achievements);
            } else {
                Toast.makeText(this, "Please upload a photo first!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Open gallery to pick an image
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            uploadImageView.setImageURI(selectedImageUri);
        }
    }

    private void uploadImageToFirebase(String safeEmail, String occupation, String skill, String experience,
                                       String location, String workLink, String description, String achievements) {
        if (selectedImageUri == null) {
            Toast.makeText(this, "No image selected!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Correct file reference
        StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + safeEmail + ".jpg");

        Toast.makeText(this, "Uploading Image...", Toast.LENGTH_SHORT).show();

        fileRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();  // Get the download URL
                    Log.d("PersonalDetails2", "Image uploaded successfully: " + imageUrl);

                    // Save user details to Firebase
                    saveDetailsToFirebase(safeEmail, occupation, skill, experience, location, workLink, description, achievements, imageUrl);

                }).addOnFailureListener(e -> {
                    Log.e("PersonalDetails2", "Error getting download URL", e);
                    Toast.makeText(PersonalDetails2.this, "Failed to get image URL", Toast.LENGTH_LONG).show();
                }))
                .addOnFailureListener(e -> {
                    Log.e("PersonalDetails2", "Image upload failed", e);
                    Toast.makeText(PersonalDetails2.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }


    // Save User Details to Firebase Realtime Database
    private void saveDetailsToFirebase(String safeEmail, String occupation, String skill, String experience,
                                       String location, String workLink, String description,
                                       String achievements, String imageUrl) {
        // Create UserDetails2 object
        UserDetails2 userDetails = new UserDetails2(userEmail, occupation, skill, experience, location, workLink, description, achievements, imageUrl);

        // Store data in Firebase under "users/safeEmail/personalDetails"
        databaseReference.child(safeEmail).child("personalDetails")
                .setValue(userDetails)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(PersonalDetails2.this, "Profile Saved!", Toast.LENGTH_SHORT).show();
                        // Redirect to profile page
                        Intent intent = new Intent(PersonalDetails2.this, profilepage.class);
                        intent.putExtra("email", userEmail);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(PersonalDetails2.this, "Failed to save details!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(PersonalDetails2.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
