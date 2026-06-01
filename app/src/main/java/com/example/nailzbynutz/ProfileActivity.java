package com.example.nailzbynutz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvEmail;
    private TextView tvMemberStatus;
    private TextView tvMemberPoints;

    private CardView memberCard;

    private DatabaseReference userRef;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Header
        findViewById(R.id.btn_back_profile).setOnClickListener(v -> finish());

        // Profile
        tvName = findViewById(R.id.tv_profile_name);
        tvEmail = findViewById(R.id.tv_profile_email);

        // Member Card
        tvMemberStatus = findViewById(R.id.tv_member_status);
        tvMemberPoints = findViewById(R.id.tv_member_points);
        memberCard = findViewById(R.id.member_card);

        SharedPreferences session = getSharedPreferences("UserSession", MODE_PRIVATE);

        currentUsername = session.getString("USER_NAME", "Guest");

        String currentEmail = session.getString("USER_EMAIL", "");

        tvName.setText(currentUsername);
        tvEmail.setText(currentEmail);

        userRef = FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users").child(currentUsername);

        setupMenu();
        setupBottomNavigation();
        loadProfileData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData();
    }

    private void loadProfileData() {

        userRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    return;
                }

                String email = snapshot.child("email").getValue(String.class);

                if (email != null) {tvEmail.setText(email);
                }

                int points = 0;

                try {

                    Object pointObj = snapshot.child("points").getValue();

                    if (pointObj != null) {
                        points = Integer.parseInt(pointObj.toString());
                    }

                } catch (Exception ignored) {
                }

                tvMemberPoints.setText(points + " pts");

                if (points >= 500) {

                    tvMemberStatus.setText("Platinum Member");

                    memberCard.setCardBackgroundColor(
                            getResources().getColor(R.color.platinum)
                    );

                } else if (points >= 300) {

                    tvMemberStatus.setText("Gold Member");

                    memberCard.setCardBackgroundColor(
                            getResources().getColor(R.color.gold_member)
                    );

                } else if (points >= 150) {

                    tvMemberStatus.setText("Silver Member");

                    memberCard.setCardBackgroundColor(
                            getResources().getColor(R.color.silver)
                    );

                } else {

                    tvMemberStatus.setText("Bronze Member");

                    memberCard.setCardBackgroundColor(
                            getResources().getColor(R.color.bronze)
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setupMenu() {

        // Edit Profile
        findViewById(R.id.btn_edit_profile).setOnClickListener(v -> startActivity(new Intent(this, EditProfileActivity.class)));

        // Wishlist
        findViewById(R.id.menu_wishlist).setOnClickListener(v -> startActivity(new Intent(this, WishlistActivity.class)));

        // Booking History
        findViewById(R.id.menu_bookings).setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));

        // Payment Info
        findViewById(R.id.menu_payment_info).setOnClickListener(v -> startActivity(new Intent(this, PaymentInfoActivity.class)));

        // Settings
        findViewById(R.id.menu_settings).setOnClickListener(v -> startActivity(new Intent(this, SettingActivity.class)));

        // Logout
        findViewById(R.id.menu_logout).setOnClickListener(v -> {SharedPreferences session = getSharedPreferences("UserSession", MODE_PRIVATE);
            session.edit().clear().apply();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);finish();
        });
    }

    private void setupBottomNavigation() {

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        if (bottomNav == null) return;

        bottomNav.setSelectedItemId(R.id.nav_profile);

        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {

                startActivity(new Intent(this, MainNavigationActivity.class));

                finish();
                return true;
            }

            if (id == R.id.nav_explore) {

                startActivity(new Intent(this, ExploreActivity.class));

                finish();
                return true;
            }

            if (id == R.id.nav_history) {

                startActivity(new Intent(this, HistoryActivity.class));

                finish();
                return true;
            }

            return id == R.id.nav_profile;
        });
    }
}