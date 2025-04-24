package com.example.skillswap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class profilepage extends AppCompatActivity {

    private TextView profileName, profileOccupation, profileEmail, profilePhone, profileLocation;
    private TextView profileSkill, profileExperience, profileWorkLink, profiledescription, profileachievements;
    private ImageView userPhoto;
    private Button editBtn, doneBtn;
    private DatabaseReference databaseReference;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepage);

        // Initialize UI elements
        profileName = findViewById(R.id.profileName);
        profileOccupation = findViewById(R.id.profileOccupation);
        profileEmail = findViewById(R.id.profileEmail);
        profilePhone = findViewById(R.id.profilePhone);
        profileLocation = findViewById(R.id.profileLocation);
        profileSkill = findViewById(R.id.profileSkill);
        profileExperience = findViewById(R.id.profileExperience);
        profileWorkLink = findViewById(R.id.profileWorkLink);
        profiledescription = findViewById(R.id.profiledescription);
        profileachievements = findViewById(R.id.profileachievements);
        editBtn = findViewById(R.id.editBtn);
        doneBtn = findViewById(R.id.doneBtn);

        // ✅ Retrieve email from Intent or SharedPreferences
        userEmail = getIntent().getStringExtra("email");

        if (userEmail == null || userEmail.isEmpty()) {
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            userEmail = sharedPreferences.getString("userEmail", "");
        }

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Error: Email not found!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Convert email to Firebase-friendly format
        String safeEmail = userEmail.replace(".", "_");

        // Firebase reference to user's main node
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(safeEmail);

        // Fetch user data
        fetchUserData();

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profilepage.this, Dashboard.class);
                startActivity(intent);
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(profilepage.this, PersonalDetails.class);
                intent.putExtra("email",userEmail);
                startActivity(intent);
            }
        });

        //Work Link open in Browser
        profileWorkLink.setOnClickListener(v -> {
            String url = profileWorkLink.getText().toString().trim();
            if (!url.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
    }


    private void fetchUserData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Fetch first name, last name, and mobile
                    String firstName = dataSnapshot.child("firstName").getValue(String.class);
                    String lastName = dataSnapshot.child("lastName").getValue(String.class);
                    String mobile = dataSnapshot.child("mobile").getValue(String.class);

                    // Fetch other details from "personalDetails" node
                    DataSnapshot personalDetails = dataSnapshot.child("personalDetails");
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String occupation = personalDetails.child("occupation").getValue(String.class);
                    String location = personalDetails.child("location").getValue(String.class);
                    String skill = personalDetails.child("skill").getValue(String.class);
                    String experience = personalDetails.child("experience").getValue(String.class);
                    String workLink = personalDetails.child("workLink").getValue(String.class);
                    String description = personalDetails.child("description").getValue(String.class);
                    String achievements = personalDetails.child("achievements").getValue(String.class);

                    // Combine firstName and lastName
                    String fullName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");

                    // Set data to UI elements
                    profileName.setText(!fullName.trim().isEmpty() ? fullName : "No Name");
                    profilePhone.setText(mobile != null ? mobile : "No Phone");
                    profileEmail.setText(email != null ? email : userEmail);
                    profileOccupation.setText(occupation != null ? occupation : "No Occupation");
                    profileLocation.setText(location != null ? location : "No Location");
                    profileSkill.setText(skill != null ? skill : "No Skill");
                    profileExperience.setText(experience != null ? experience : "No Experience");
                    profileWorkLink.setText(workLink != null ? workLink : "No Link");
                    profiledescription.setText(description != null ? description : "No Description");
                    profileachievements.setText(achievements != null ? achievements : "No Achievements");
                } else {
                    Toast.makeText(profilepage.this, "No data found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching data", databaseError.toException());
                Toast.makeText(profilepage.this, "Failed to load data!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
