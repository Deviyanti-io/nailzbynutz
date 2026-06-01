package com.example.nailzbynutz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// Import Firebase
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainNavigationActivity extends AppCompatActivity {

    private ScrollView layoutHomePage;
    private TextView tvWelcomeUser, tvUserPoints; // Dideklarasikan saja di sini
    private CardView menuGelNail, menuPressOnNail, menuManicure;
    private BottomNavigationView bottomNav;
    private String currentUsername = "Guest";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);

        // Ambil data session nama user yang masuk
        if (getIntent().hasExtra("USER_NAME")) {
            currentUsername = getIntent().getStringExtra("USER_NAME");
        } else {
            SharedPreferences session = getSharedPreferences("UserSession", MODE_PRIVATE);
            currentUsername = session.getString("USER_NAME", "Guest");
        }

        // Inisialisasi (findViewById) HARUS dilakukan di sini, di dalam onCreate
        layoutHomePage = findViewById(R.id.layout_home_page);
        tvWelcomeUser = findViewById(R.id.tv_welcome_user);
        tvUserPoints = findViewById(R.id.tv_user_points);
        menuGelNail = findViewById(R.id.menu_gel_nail);
        menuPressOnNail = findViewById(R.id.menu_press_on_nail);
        menuManicure = findViewById(R.id.menu_manicure);
        bottomNav = findViewById(R.id.bottom_navigation);

        // Menarik Data Nama & Poin secara Real-Time dari Firebase
        DatabaseReference usersRef = FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users");
        usersRef.orderByChild("username").equalTo(currentUsername).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnap : snapshot.getChildren()) {
                        String name = userSnap.child("username").getValue(String.class);
                        Integer dbPoints = userSnap.child("points").getValue(Integer.class);
                        int points = (dbPoints != null) ? dbPoints : 0;

                        if (tvWelcomeUser != null) tvWelcomeUser.setText("Hi, " + name + "! 👋");
                        if (tvUserPoints != null) tvUserPoints.setText(points + " Pts");
                    }
                } else {
                    if (tvWelcomeUser != null) tvWelcomeUser.setText("Hi, " + currentUsername + "! 👋");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Biarkan tampilan default jika gagal memuat
            }
        });

        menuPressOnNail.setOnClickListener(v -> startActivity(new Intent(this, CustomNailShapeActivity.class)));
        menuGelNail.setOnClickListener(v -> startActivity(new Intent(this, GelPolishActivity.class)));
        menuManicure.setOnClickListener(v -> startActivity(new Intent(this, ManicureActivity.class)));

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                layoutHomePage.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.nav_explore) {
                startActivity(new Intent(this, ExploreActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(this, HistoryActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });

        bottomNav.setSelectedItemId(R.id.nav_home);
    }
}