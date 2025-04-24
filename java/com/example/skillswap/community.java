package com.example.skillswap;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class community extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GroupAdapter groupAdapter;
    private List<Group> groupList, filteredList;
    private DatabaseReference groupsRef;
    private Button btnCreateGroup, btnSearch;
    private EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        recyclerView = findViewById(R.id.recycler_explore_groups);
        btnCreateGroup = findViewById(R.id.btnCreateGroup);
        btnSearch = findViewById(R.id.btnSearch);
        searchBar = findViewById(R.id.searchBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        groupList = new ArrayList<>();
        filteredList = new ArrayList<>();
        groupAdapter = new GroupAdapter(this, groupList);
        recyclerView.setAdapter(groupAdapter);

        groupsRef = FirebaseDatabase.getInstance().getReference("Groups");

        btnCreateGroup.setOnClickListener(v -> {
            Intent intent = new Intent(community.this, CreateGroupActivity.class);
            startActivity(intent);
        });

        loadGroups();

        // Search feature
        btnSearch.setOnClickListener(v -> filterGroups(searchBar.getText().toString().trim()));

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterGroups(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadGroups() {
        groupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupList.clear();
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    Group group = groupSnapshot.getValue(Group.class);
                    if (group != null) {
                        groupList.add(group);
                    }
                }
                groupAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(community.this, "Failed to load groups", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterGroups(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(groupList);
        } else {
            for (Group group : groupList) {
                if (group.getGroupName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(group);
                }
            }
        }
        groupAdapter.updateList(filteredList);
    }
}
