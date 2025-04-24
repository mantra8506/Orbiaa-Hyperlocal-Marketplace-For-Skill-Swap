package com.example.skillswap;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateGroupActivity extends AppCompatActivity {

    private EditText editGroupName;
    private Button btnCreate;
    private DatabaseReference groupsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        editGroupName = findViewById(R.id.editGroupName);
        btnCreate = findViewById(R.id.btnCreate);
        groupsRef = FirebaseDatabase.getInstance().getReference("Groups");

        btnCreate.setOnClickListener(v -> {
            String groupName = editGroupName.getText().toString().trim();

            if (TextUtils.isEmpty(groupName)) {
                Toast.makeText(CreateGroupActivity.this, "Enter group name", Toast.LENGTH_SHORT).show();
                return;
            }

            String groupId = groupsRef.push().getKey();
            Group newGroup = new Group(groupName);
            groupsRef.child(groupId).setValue(newGroup)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(CreateGroupActivity.this, "Group Created", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(CreateGroupActivity.this, "Failed to create group", Toast.LENGTH_SHORT).show());
        });
    }
}
