package com.example.nailzbynutz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProfileName, tvProfileEmail, tvMemberPoints, tvMemberStatus;
    private LinearLayout menuLogout, menuEdit, menuWishlist, menuBookings, menuSettings;
    private BottomNavigationView bottomNav;
    private CardView memberCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvProfileName = findViewById(R.id.tv_profile_name);
        tvProfileEmail = findViewById(R.id.tv_profile_email);
        tvMemberPoints = findViewById(R.id.tv_member_points);
        tvMemberStatus = findViewById(R.id.tv_member_status);
        memberCard = findViewById(R.id.member_card);
        menuLogout = findViewById(R.id.menu_logout);
        menuEdit = findViewById(R.id.menu_edit);
        menuWishlist = findViewById(R.id.menu_wishlist);
        menuBookings = findViewById(R.id.menu_bookings);
        menuSettings = findViewById(R.id.menu_settings);
        bottomNav = findViewById(R.id.bottom_navigation);

        SharedPreferences session = getSharedPreferences("UserSession", MODE_PRIVATE);
        String savedName = session.getString("USER_NAME", "Nailz Lover");
        String savedEmail = session.getString("USER_EMAIL", "hello@nailz.com");
        tvProfileName.setText(savedName);
        tvProfileEmail.setText(savedEmail);

        SharedPreferences pointsPref = getSharedPreferences("UserPoints", MODE_PRIVATE);
        int points = pointsPref.getInt("total_points", 50);
        tvMemberPoints.setText(points + " pts");

        if (points >= 1000) {
            tvMemberStatus.setText("Gold Member");
            memberCard.setCardBackgroundColor(getColor(R.color.gold));
        } else if (points >= 500) {
            tvMemberStatus.setText("Silver Member");
            memberCard.setCardBackgroundColor(getColor(R.color.silver));
        } else {
            tvMemberStatus.setText("Bronze Member");
            memberCard.setCardBackgroundColor(getColor(R.color.bronze));
        }

        // Logout
        menuLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = session.edit();
            editor.clear();
            editor.apply();
            Toast.makeText(this, "Berhasil keluar akun", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Edit Profil -> buka activity baru
        menuEdit.setOnClickListener(v -> startActivity(new Intent(this, EditProfileActivity.class)));

        // Wishlist
        menuWishlist.setOnClickListener(v -> startActivity(new Intent(this, WishlistActivity.class)));

        // My Bookings (History)
        menuBookings.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));

        // Settings -> buka activity baru
        menuSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingActivity.class)));

        // Bottom navigation
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainNavigationActivity.class));
                overridePendingTransition(0,0);
                finish();
                return true;
            } else if (id == R.id.nav_explore) {
                startActivity(new Intent(this, ExploreActivity.class));
                overridePendingTransition(0,0);
                finish();
                return true;
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(this, HistoryActivity.class));
                overridePendingTransition(0,0);
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });
        bottomNav.setSelectedItemId(R.id.nav_profile);

        findViewById(R.id.btn_back_profile).setOnClickListener(v -> finish());
    }
}