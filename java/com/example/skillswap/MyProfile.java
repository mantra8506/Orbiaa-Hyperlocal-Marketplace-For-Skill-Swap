package com.example.skillswap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

public class MyProfile extends AppCompatActivity {

    private TextView receivedRequestsText, sentRequestsText;
    private DatabaseReference databaseReference;
    private String currentUserEmail;
    private ImageView btnBack;
    private LinearLayout aboutpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        btnBack=findViewById(R.id.btnBack);
        receivedRequestsText = findViewById(R.id.receivedRequestsText);
        sentRequestsText = findViewById(R.id.sentRequestsText);
        aboutpage=findViewById(R.id.Aboutpage);

        btnBack.setOnClickListener(v -> finish());
        currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (currentUserEmail != null) {
            countRequests();
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
        }

        aboutpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MyProfile.this, AboutUsActivity.class);
                startActivity(intent);
            }
        });

    }

    private void countRequests() {
        String formattedEmail = currentUserEmail.replace(".", "_");
        databaseReference = FirebaseDatabase.getInstance().getReference("swapRequests");

        // Count received requests
        databaseReference.child(formattedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long receivedCount = snapshot.getChildrenCount();
                receivedRequestsText.setText("Requests Received: " + receivedCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyProfile.this, "Failed to load received requests.", Toast.LENGTH_SHORT).show();
            }
        });

        // Count sent requests
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long sentCount = 0;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    if (userSnapshot.child(formattedEmail).exists()) {
                        sentCount++;
                    }
                }
                sentRequestsText.setText("Requests Sent: " + sentCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyProfile.this, "Failed to load sent requests.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
