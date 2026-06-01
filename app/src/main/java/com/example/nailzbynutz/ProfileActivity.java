package com.example.nailzbynutz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// Import Firebase
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvPoints, tvMemberStatus;
    private CardView memberCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.tv_profile_name);
        tvEmail = findViewById(R.id.tv_profile_email);
        tvPoints = findViewById(R.id.tv_member_points);
        tvMemberStatus = findViewById(R.id.tv_member_status);
        memberCard = findViewById(R.id.member_card);
        Button btnEditProfileTop = findViewById(R.id.btn_edit_profile);
        ImageView btnBack = findViewById(R.id.btn_back_profile);

        // 1. Ambil Username yang sedang login dari sesi lokal
        SharedPreferences session = getSharedPreferences("UserSession", MODE_PRIVATE);
        String currentUsername = session.getString("USER_NAME", "Nailz Lover");

        // 2. Tarik data profil langsung dari Firebase secara Real-Time
        DatabaseReference usersRef = FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users");

        usersRef.orderByChild("username").equalTo(currentUsername).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnap : snapshot.getChildren()) {
                        String name = userSnap.child("username").getValue(String.class);
                        String email = userSnap.child("email").getValue(String.class);

                        // Menarik poin dari database, default 0 jika belum ada
                        Integer dbPoints = userSnap.child("points").getValue(Integer.class);
                        int points = (dbPoints != null) ? dbPoints : 0;

                        if (tvName != null) tvName.setText(name);
                        if (tvEmail != null) tvEmail.setText(email);
                        if (tvPoints != null) tvPoints.setText(points + " pts");

                        // Logika Member Tier
                        if (points >= 1000) {
                            tvMemberStatus.setText("Gold Member");
                            memberCard.setCardBackgroundColor(Color.parseColor("#FFD700")); // Hex warna gold
                        } else if (points >= 500) {
                            tvMemberStatus.setText("Silver Member");
                            memberCard.setCardBackgroundColor(Color.parseColor("#C0C0C0")); // Hex warna silver
                        } else {
                            tvMemberStatus.setText("Bronze Member");
                            memberCard.setCardBackgroundColor(Color.parseColor("#CD7F32")); // Hex warna bronze
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Gagal memuat profil", Toast.LENGTH_SHORT).show();
            }
        });

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnEditProfileTop != null) {
            btnEditProfileTop.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class)));
        }

        LinearLayout menuPaymentInfo = findViewById(R.id.menu_payment_info);
        LinearLayout menuWishlist = findViewById(R.id.menu_wishlist);
        LinearLayout menuBookings = findViewById(R.id.menu_bookings);
        LinearLayout menuSettings = findViewById(R.id.menu_settings);
        LinearLayout menuLogout = findViewById(R.id.menu_logout);

        if (menuPaymentInfo != null) menuPaymentInfo.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, PaymentInfoActivity.class)));
        if (menuWishlist != null) menuWishlist.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, WishlistActivity.class)));
        if (menuBookings != null) menuBookings.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, HistoryActivity.class)));
        if (menuSettings != null) menuSettings.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, SettingActivity.class)));

        if (menuLogout != null) {
            menuLogout.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Apakah Anda yakin ingin keluar dari akun?")
                        .setPositiveButton("Ya, Keluar", (dialog, which) -> {
                            session.edit().clear().apply(); // Hapus sesi
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