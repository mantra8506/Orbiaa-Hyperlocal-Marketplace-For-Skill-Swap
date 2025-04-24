package com.example.skillswap;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.skillswap.R;

public class Loading2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading2);

        // Button for "Join Now"
        Button joinNowButton = findViewById(R.id.join_now_button);
        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Loading2.this, "Join Now clicked", Toast.LENGTH_SHORT).show();
                Intent signUpIntent = new Intent(Loading2.this, SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });

// TextView for "Login"
        TextView loginText = findViewById(R.id.login_text);
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Loading2.this, "Login clicked", Toast.LENGTH_SHORT).show();
                Intent loginIntent = new Intent(Loading2.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

    }
}