package com.example.nailzbynutz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvMemberStatus, tvMemberPoints;
    private Button btnEditProfile;
    private CardView menuEdit, menuWishlist, menuBookings, menuSettings, menuLogout;
    private CardView memberCard;
    private SharedPreferences userPrefs, sessionPref, pointsPref;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        tvName = findViewById(R.id.tv_profile_name);
        tvEmail = findViewById(R.id.tv_profile_email);
        tvMemberStatus = findViewById(R.id.tv_member_status);
        tvMemberPoints = findViewById(R.id.tv_member_points);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        menuEdit = findViewById(R.id.menu_edit);
        menuWishlist = findViewById(R.id.menu_wishlist);
        menuBookings = findViewById(R.id.menu_bookings);
        menuSettings = findViewById(R.id.menu_settings);
        menuLogout = findViewById(R.id.menu_logout);
        memberCard = findViewById(R.id.member_card);

        // Back button
        findViewById(R.id.btn_back_profile).setOnClickListener(v -> finish());

        // Get user data from SharedPreferences
        sessionPref = getSharedPreferences("UserSession", MODE_PRIVATE);
        userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        pointsPref = getSharedPreferences("UserPoints", MODE_PRIVATE);

        String name = sessionPref.getString("USER_NAME", userPrefs.getString("SAVED_USER", "Guest"));
        String email = sessionPref.getString("USER_EMAIL", userPrefs.getString("SAVED_EMAIL", "guest@example.com"));

        tvName.setText(name);
        tvEmail.setText(email);

        // Get member points (default 0, bisa bertambah dari booking)
        int points = pointsPref.getInt("total_points", 0);
        updateMemberUI(points);

        // Click listeners
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        menuEdit.setOnClickListener(v -> showEditProfileDialog());
        menuWishlist.setOnClickListener(v -> startActivity(new Intent(this, WishlistActivity.class)));
        menuBookings.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
        menuSettings.setOnClickListener(v -> Toast.makeText(this, "Settings coming soon", Toast.LENGTH_SHORT).show());
        menuLogout.setOnClickListener(v -> {
            sessionPref.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void updateMemberUI(int points) {
        tvMemberPoints.setText(points + " pts");
        String level;
        int bgRes;
        if (points < 500) {
            level = "Silver Member";
            bgRes = R.drawable.bg_member_silver;
        } else if (points < 1000) {
            level = "Gold Member";
            bgRes = R.drawable.bg_member_gold;
        } else {
            level = "Platinum Member";
            bgRes = R.drawable.bg_member_platinum;
        }
        tvMemberStatus.setText(level);
        memberCard.setCardBackgroundColor(getColor(android.R.color.transparent));
        memberCard.setBackgroundResource(bgRes);
    }

    private void showEditProfileDialog() {
        // Implement edit profile dialog (optional)
        Toast.makeText(this, "Edit profile feature coming soon", Toast.LENGTH_SHORT).show();
    }
}