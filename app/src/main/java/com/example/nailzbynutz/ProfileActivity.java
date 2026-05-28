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
    private CardView menuEdit, menuWishlist, menuBookings, menuSettings, menuLogout, memberCard;
    private SharedPreferences sessionPref, pointsPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

        findViewById(R.id.btn_back_profile).setOnClickListener(v -> finish());

        sessionPref = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        pointsPref = getSharedPreferences("UserPoints", MODE_PRIVATE);

        String name = sessionPref.getString("USER_NAME", userPrefs.getString("SAVED_USER", "Guest"));
        String email = sessionPref.getString("USER_EMAIL", userPrefs.getString("SAVED_EMAIL", "guest@example.com"));
        tvName.setText(name);
        tvEmail.setText(email);

        int points = pointsPref.getInt("total_points", 0);
        tvMemberPoints.setText(points + " pts");
        updateMemberUI(points);

        btnEditProfile.setOnClickListener(v -> Toast.makeText(this, "Edit profile coming soon", Toast.LENGTH_SHORT).show());
        menuEdit.setOnClickListener(v -> Toast.makeText(this, "Edit profile coming soon", Toast.LENGTH_SHORT).show());
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
        if (points < 500) {
            tvMemberStatus.setText("Silver Member");
            memberCard.setCardBackgroundColor(getColor(R.color.silver));
        } else if (points < 1000) {
            tvMemberStatus.setText("Gold Member");
            memberCard.setCardBackgroundColor(getColor(R.color.gold));
        } else {
            tvMemberStatus.setText("Platinum Member");
            memberCard.setCardBackgroundColor(getColor(R.color.platinum));
        }
    }
}