package com.example.skillswap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class Dashboard extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuButton, searchbutton;
    private BottomNavigationView bottomNavigationView;
    private LinearLayout thumnail1, thumnail2, thumnail3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        menuButton = findViewById(R.id.r8mahpbbxfk5);
        searchbutton = findViewById(R.id.searchBtn);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        thumnail1 = findViewById(R.id.thumnail1);
        thumnail2 = findViewById(R.id.thumnail2);
        thumnail3 = findViewById(R.id.thumnail3);


        // Toggle Drawer on Menu Button Click
        menuButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
            } else {
                drawerLayout.openDrawer(navigationView);
            }
        });

        //Thumnail1 action perform
        thumnail1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://youtube.com/playlist?list=PLdvOfoe7PXT0ouChAnR1nHlT8BJIo5hP_&si=M8mg12C-isB-VAaa"));
                intent.setPackage("com.google.android.youtube");
                startActivity(intent);
            }
        });

        //Thumnail2 action perform
        thumnail2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Dashboard.this, community.class);
                startActivity(intent);
            }
        });

        //Thumnail1 action perform
        thumnail3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://youtube.com/playlist?list=PLRDn2RBZOEgG76m_kXuQlmNaUgkYEFXeb&si=TC7RBP87MP7_-uzX"));
                intent.setPackage("com.google.android.youtube");
                startActivity(intent);
            }
        });

        // Search Button Click
        searchbutton.setOnClickListener(view -> {
            Intent intent = new Intent(Dashboard.this, explore.class);
            startActivity(intent);
        });

        // Handle Navigation Drawer Clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Toast.makeText(Dashboard.this, "Already On Home Page ", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_personalDetails) {
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String userEmail = sharedPreferences.getString("userEmail", "");

                if (!userEmail.isEmpty()) {
                    Intent intent = new Intent(Dashboard.this, profilepage.class);
                    intent.putExtra("email", userEmail);
                    startActivity(intent);
                } else {
                    Toast.makeText(Dashboard.this, "Error: User email not found!", Toast.LENGTH_LONG).show();
                }
            } else if (id == R.id.nav_community) {
                startActivity(new Intent(Dashboard.this, community.class));
            } else if (id == R.id.nav_logOut) {
                startActivity(new Intent(Dashboard.this, LoginActivity.class));
            } else if (id == R.id.nav_premium) {
                Toast.makeText(Dashboard.this,"Premium page Can't Available Now !!",Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawers();
            return true;
        });

        // Handle Bottom Navigation Clicks
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Toast.makeText(Dashboard.this,"Already On Home Page",Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_swap) {
                startActivity(new Intent(Dashboard.this, requests.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(Dashboard.this, MyProfile.class));
            }
            return true;
        });
    }
}
