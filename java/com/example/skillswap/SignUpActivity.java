package com.example.skillswap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etMobile, etEmail, etPassword;
    private Button btnCreateAccount, btnLogin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);

        // Initialize UI elements
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etMobile = findViewById(R.id.etmobile);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnLogin = findViewById(R.id.txtlogin);

        // Create Account Button Click
        btnCreateAccount.setOnClickListener(view -> registerUser());

        // Navigate to Login Page
        btnLogin.setOnClickListener(view -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate input fields
        if (TextUtils.isEmpty(firstName)) {
            etFirstName.setError("First Name is required");
            return;
        }
        if (TextUtils.isEmpty(lastName)) {
            etLastName.setError("Last Name is required");
            return;
        }
        if (TextUtils.isEmpty(mobile)) {
            etMobile.setError("Mobile number is required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }

        // Show progress dialog
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        // Create user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get the registered user
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserDetails(user.getUid(), firstName, lastName, mobile, email);
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SignUpActivity.this, "Registration failed " , Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserDetails(String userId, String firstName, String lastName, String mobile, String email) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");

        // Convert email to Firebase-friendly key
        String safeEmail = email.replace(".", "_");

        // Create user data map
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("firstName", firstName);
        userMap.put("lastName", lastName);
        userMap.put("mobile", mobile);
        userMap.put("email", email);
        userMap.put("userId", userId);

        // Save data to Realtime Database using email as key
        databaseRef.child(safeEmail).setValue(userMap)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();

                        // Navigate to next activity
                        Intent intent = new Intent(SignUpActivity.this, PersonalDetails.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Error saving data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


}
