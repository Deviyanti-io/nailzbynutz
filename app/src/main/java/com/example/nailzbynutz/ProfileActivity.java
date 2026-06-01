package com.example.nailzbynutz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvEmail;
    private TextView tvPoints;
    private TextView tvMemberStatus;

    private View memberCard;

    private DatabaseReference userRef;

    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.tv_profile_name);
        tvEmail = findViewById(R.id.tv_profile_email);

        tvPoints = findViewById(R.id.tv_member_points);
        tvMemberStatus = findViewById(R.id.tv_member_status);

        memberCard = findViewById(R.id.member_card);

        SharedPreferences session =
                getSharedPreferences("UserSession", MODE_PRIVATE);

        currentUsername =
                session.getString("USER_NAME", "Guest");

        String currentEmail =
                session.getString("USER_EMAIL", "");

        tvName.setText(currentUsername);
        tvEmail.setText(currentEmail);

        userRef = FirebaseDatabase
                .getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users")
                .child(currentUsername);

        setupMenuButtons();

        setupBottomNavigation();

        loadRealtimeProfileData();

        View btnBack = findViewById(R.id.btn_back_profile);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void setupMenuButtons() {

        findViewById(R.id.btn_edit_profile)
                .setOnClickListener(v ->
                        startActivity(
                                new Intent(
                                        ProfileActivity.this,
                                        EditProfileActivity.class)));

        findViewById(R.id.menu_payment_info)
                .setOnClickListener(v ->
                        startActivity(
                                new Intent(
                                        ProfileActivity.this,
                                        PaymentInfoActivity.class)));

        findViewById(R.id.menu_wishlist)
                .setOnClickListener(v ->
                        startActivity(
                                new Intent(
                                        ProfileActivity.this,
                                        WishlistActivity.class)));

        findViewById(R.id.menu_bookings)
                .setOnClickListener(v ->
                        startActivity(
                                new Intent(
                                        ProfileActivity.this,
                                        HistoryActivity.class)));

        findViewById(R.id.menu_settings)
                .setOnClickListener(v ->
                        startActivity(
                                new Intent(
                                        ProfileActivity.this,
                                        SettingActivity.class)));

        findViewById(R.id.menu_logout)
                .setOnClickListener(v -> {

                    SharedPreferences session =
                            getSharedPreferences(
                                    "UserSession",
                                    MODE_PRIVATE);

                    session.edit().clear().apply();

                    Intent intent =
                            new Intent(
                                    ProfileActivity.this,
                                    LoginActivity.class);

                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                    finish();
                });
    }

    private void loadRealtimeProfileData() {

        userRef.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {

                        if (!snapshot.exists()) {
                            return;
                        }

                        String email =
                                snapshot.child("email")
                                        .getValue(String.class);

                        if (email != null) {
                            tvEmail.setText(email);
                        }

                        int points = 0;

                        try {

                            Object value =
                                    snapshot.child("points")
                                            .getValue();

                            if (value != null) {
                                points = Integer.parseInt(
                                        value.toString());
                            }

                        } catch (Exception ignored) {
                        }

                        tvPoints.setText(points + " pts");

                        updateMemberTier(points);
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {
                    }
                });
    }

    private void updateMemberTier(int points) {

        if (points >= 500) {

            tvMemberStatus.setText("Platinum Member");

            memberCard.setBackgroundResource(
                    R.drawable.bg_member_platinum);

        } else if (points >= 300) {

            tvMemberStatus.setText("Gold Member");

            memberCard.setBackgroundResource(
                    R.drawable.bg_member_gold);

        } else if (points >= 150) {

            tvMemberStatus.setText("Silver Member");

            memberCard.setBackgroundResource(
                    R.drawable.bg_member_silver);

        } else {

            tvMemberStatus.setText("Bronze Member");

            memberCard.setBackgroundResource(
                    R.drawable.bg_card_rounded);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRealtimeProfileData();
    }

    private void setupBottomNavigation() {

        BottomNavigationView bottomNav =
                findViewById(R.id.bottom_navigation);

        if (bottomNav == null) return;

        bottomNav.setSelectedItemId(R.id.nav_profile);

        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {

                startActivity(
                        new Intent(
                                this,
                                MainNavigationActivity.class));

                finish();
                return true;
            }

            if (id == R.id.nav_explore) {

                startActivity(
                        new Intent(
                                this,
                                ExploreActivity.class));

                finish();
                return true;
            }

            if (id == R.id.nav_history) {

                startActivity(
                        new Intent(
                                this,
                                HistoryActivity.class));

                finish();
                return true;
            }

            if (id == R.id.nav_profile) {
                return true;
            }

            return false;
        });
    }
}