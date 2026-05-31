package com.example.nailzbynutz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences session = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userName = session.getString("USER_NAME", "Nailz Lover");
        String userEmail = session.getString("USER_EMAIL", "hello@nailz.com");

        SharedPreferences pointsPref = getSharedPreferences("UserPoints", MODE_PRIVATE);
        int points = pointsPref.getInt("total_points", 0);

        TextView tvName = findViewById(R.id.tv_profile_name);
        TextView tvEmail = findViewById(R.id.tv_profile_email);
        TextView tvPoints = findViewById(R.id.tv_member_points);
        TextView tvMemberStatus = findViewById(R.id.tv_member_status);
        CardView memberCard = findViewById(R.id.member_card);
        Button btnEditProfileTop = findViewById(R.id.btn_edit_profile);
        ImageView btnBack = findViewById(R.id.btn_back_profile);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (tvName != null) tvName.setText(userName);
        if (tvEmail != null) tvEmail.setText(userEmail);
        if (tvPoints != null) tvPoints.setText(points + " pts");

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

        if (btnEditProfileTop != null) {
            btnEditProfileTop.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class)));
        }

        LinearLayout menuPaymentInfo = findViewById(R.id.menu_payment_info);
        LinearLayout menuWishlist = findViewById(R.id.menu_wishlist);
        LinearLayout menuBookings = findViewById(R.id.menu_bookings);
        LinearLayout menuSettings = findViewById(R.id.menu_settings);
        LinearLayout menuLogout = findViewById(R.id.menu_logout);

        if (menuPaymentInfo != null) {
            menuPaymentInfo.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, PaymentInfoActivity.class)));
        }
        if (menuWishlist != null) {
            menuWishlist.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, WishlistActivity.class)));
        }
        if (menuBookings != null) {
            menuBookings.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, HistoryActivity.class)));
        }
        if (menuSettings != null) {
            menuSettings.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, SettingActivity.class)));
        }

        if (menuLogout != null) {
            menuLogout.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Apakah Anda yakin ingin keluar dari akun?")
                        .setPositiveButton("Ya, Keluar", (dialog, which) -> {
                            session.edit().clear().apply();
                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }).setNegativeButton("Batal", null).show();
            });
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_profile);
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(this, MainNavigationActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.nav_explore) {
                    startActivity(new Intent(this, ExploreActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.nav_history) {
                    startActivity(new Intent(this, HistoryActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.nav_profile) {
                    return true;
                }
                return false;
            });
        }
    }
}