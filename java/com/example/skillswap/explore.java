package com.example.skillswap;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class explore extends AppCompatActivity {

    private EditText searchBar;
    private ImageView searchBtn;
    private RecyclerView searchResultsRecycler;
    private DatabaseReference databaseReference;
    private List<User> userList;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        searchBar = findViewById(R.id.searchBar);
        searchBtn = findViewById(R.id.searchBtn);
        searchResultsRecycler = findViewById(R.id.searchResultsRecycler);

        // Initialize RecyclerView
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList);
        searchResultsRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchResultsRecycler.setAdapter(userAdapter);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Real-time search as user types
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fetchUsersBasedOnSkill(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchUsersBasedOnSkill(String skillQuery) {
        userList.clear();

        if (!skillQuery.isEmpty()) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userList.clear();
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Retrieve firstName and lastName correctly
                        String firstName = userSnapshot.child("firstName").getValue(String.class);
                        String lastName = userSnapshot.child("lastName").getValue(String.class);
                        if (firstName == null) firstName = "";
                        if (lastName == null) lastName = "";
                        String fullName = firstName + " " + lastName;

                        // Extract email and personal details
                        String email = userSnapshot.child("email").getValue(String.class); // Fetch email
                        DataSnapshot personalDetails = userSnapshot.child("personalDetails");
                        String skill = personalDetails.child("skill").getValue(String.class);
                        String experience = personalDetails.child("experience").getValue(String.class);
                        String location = personalDetails.child("location").getValue(String.class);

                        // Check if skill matches the search query
                        if (skill != null && skill.toLowerCase().contains(skillQuery.toLowerCase())) {
                            userList.add(new User(firstName, lastName, email, skill, experience, location));
                        }
                    }

                    if (userList.isEmpty()) {
                        Toast.makeText(explore.this, "No users found", Toast.LENGTH_SHORT).show();
                    }
                    userAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(explore.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            userList.clear();
            userAdapter.notifyDataSetChanged();
        }
    }


}
