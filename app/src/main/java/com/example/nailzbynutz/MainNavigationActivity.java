package com.example.nailzbynutz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainNavigationActivity extends AppCompatActivity {

    // Deklarasi view
    private ScrollView layoutHomePage;
    private LinearLayout layoutProfilePage;
    private TextView tvWelcomeUser, tvProfileName, tvProfileEmail;
    private CardView menuGelNail, menuPressOnNail, menuManicure;
    private BottomNavigationView bottomNav;
    private Button btnLogout;
    private String currentUsername = "Guest";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);

        // Ambil data user dari Intent atau SharedPreferences
        if (getIntent().hasExtra("USER_NAME")) {
            currentUsername = getIntent().getStringExtra("USER_NAME");
        } else {
            SharedPreferences session = getSharedPreferences("UserSession", MODE_PRIVATE);
            currentUsername = session.getString("USER_NAME", "Guest");
        }

        // Inisialisasi view
        layoutHomePage = findViewById(R.id.layout_home_page);
        layoutProfilePage = findViewById(R.id.layout_profile_page);
        tvWelcomeUser = findViewById(R.id.tv_welcome_user);
        tvProfileName = findViewById(R.id.tv_profile_name);
        tvProfileEmail = findViewById(R.id.tv_profile_email);
        menuGelNail = findViewById(R.id.menu_gel_nail);
        menuPressOnNail = findViewById(R.id.menu_press_on_nail);
        menuManicure = findViewById(R.id.menu_manicure);
        bottomNav = findViewById(R.id.bottom_navigation);
        btnLogout = findViewById(R.id.btn_logout);

        // Set data user
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String savedEmail = userPrefs.getString("SAVED_EMAIL", "email@example.com");
        tvWelcomeUser.setText("Hi, " + currentUsername + "! 👋");
        tvProfileName.setText(currentUsername);
        tvProfileEmail.setText(savedEmail);

        // Klik menu layanan
        menuPressOnNail.setOnClickListener(v -> {
            startActivity(new Intent(this, CustomNailShapeActivity.class));
        });
        menuGelNail.setOnClickListener(v -> {
            Toast.makeText(this, "Gel Nail service coming soon!", Toast.LENGTH_SHORT).show();
        });
        menuManicure.setOnClickListener(v -> {
            Toast.makeText(this, "Manicure service coming soon!", Toast.LENGTH_SHORT).show();
        });

        // Bottom navigation
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                layoutHomePage.setVisibility(View.VISIBLE);
                layoutProfilePage.setVisibility(View.GONE);
                return true;
            } else if (id == R.id.nav_explore) {
                startActivity(new Intent(this, ExploreActivity.class));
                return true;
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(this, HistoryActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                layoutHomePage.setVisibility(View.GONE);
                layoutProfilePage.setVisibility(View.VISIBLE);
                return true;
            }
            return false;
        });

        // Logout
        btnLogout.setOnClickListener(v -> {
            getSharedPreferences("UserSession", MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}