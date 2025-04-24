package com.example.skillswap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.identity.Identity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView txtForgotPassword, SignUp;
    private CheckBox cbRememberPassword;
    private ImageView passwordIcon, logoGoogle, logoFacebook, logoApple;

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        SignUp = findViewById(R.id.btnSignUp);
        cbRememberPassword = findViewById(R.id.cbRememberPassword);
        passwordIcon = findViewById(R.id.passwordIcon);
        logoGoogle = findViewById(R.id.logoGoogle);
        logoFacebook = findViewById(R.id.logoFacebook);
        logoApple = findViewById(R.id.logoApple);

        btnLogin.setOnClickListener(view -> loginUser());

        SignUp.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });


        logoGoogle.setOnClickListener(view -> signInWithGoogle());

        // Handle password visibility toggle
        passwordIcon.setOnClickListener(v -> togglePasswordVisibility());

    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide Password
            etPassword.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
            passwordIcon.setImageResource(R.drawable.passwordhidden); // Set to hidden icon
        } else {
            // Show Password
            etPassword.setTransformationMethod(android.text.method.HideReturnsTransformationMethod.getInstance());
            passwordIcon.setImageResource(R.drawable.passwordshow); // Set to visible icon
        }
        isPasswordVisible = !isPasswordVisible;
    }


    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return;
        }

        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String safeEmail = email.replace(".", "_");

                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(safeEmail);
                    databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String firstName = snapshot.child("firstName").getValue(String.class);
                                String lastName = snapshot.child("lastName").getValue(String.class);
                                String mobile = snapshot.child("mobile").getValue(String.class);

                                // Store email in SharedPreferences for later use
                                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("userEmail", email);
                                editor.apply();

                                // Navigate to Dashboard
                                Intent intent = new Intent(LoginActivity.this, Dashboard.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "User data not found!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



    private void signInWithGoogle() {
        Toast.makeText(LoginActivity.this, "Google Sign-In not implemented yet", Toast.LENGTH_SHORT).show();
    }
}
