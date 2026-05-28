package com.example.nailzbynutz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainNavigationActivity extends AppCompatActivity {

    private ScrollView layoutHomePage;
    private TextView tvWelcomeUser;
    private CardView menuGelNail, menuPressOnNail, menuManicure;
    private BottomNavigationView bottomNav;
    private String currentUsername = "Guest";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);

        // Ambil data user
        if (getIntent().hasExtra("USER_NAME")) {
            currentUsername = getIntent().getStringExtra("USER_NAME");
        } else {
            SharedPreferences session = getSharedPreferences("UserSession", MODE_PRIVATE);
            currentUsername = session.getString("USER_NAME", "Guest");
        }

        // Inisialisasi view
        layoutHomePage = findViewById(R.id.layout_home_page);
        tvWelcomeUser = findViewById(R.id.tv_welcome_user);
        menuGelNail = findViewById(R.id.menu_gel_nail);
        menuPressOnNail = findViewById(R.id.menu_press_on_nail);
        menuManicure = findViewById(R.id.menu_manicure);
        bottomNav = findViewById(R.id.bottom_navigation);

        // Set greeting
        tvWelcomeUser.setText("Hi, " + currentUsername + "! 👋");

        // Klik menu layanan
        menuPressOnNail.setOnClickListener(v -> {
            startActivity(new Intent(this, CustomNailShapeActivity.class));
        });
        menuGelNail.setOnClickListener(v -> {
            startActivity(new Intent(this, GelPolishActivity.class));
        });
        menuManicure.setOnClickListener(v -> {
            startActivity(new Intent(this, ManicureActivity.class));
        });

        // Bottom navigation
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                layoutHomePage.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.nav_explore) {
                startActivity(new Intent(this, ExploreActivity.class));
                return true;
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(this, HistoryActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        // Set default menu home aktif
        bottomNav.setSelectedItemId(R.id.nav_home);
    }
}