package com.example.nailzbynutz;

import android.annotation.SuppressLint;
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

    private TextView tvName, tvEmail, tvPoints, tvMemberTier;
    private View cardMember;
    private DatabaseReference userRef;
    private String currentUsername;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Deklarasi Komponen UI
        tvName = findViewById(R.id.tv_profile_name);
        tvEmail = findViewById(R.id.tv_profile_email);
        tvPoints = findViewById(R.id.tv_points);
        tvMemberTier = findViewById(R.id.tv_member_tier);
        cardMember = findViewById(R.id.card_member);

        // Ambil Data Sesi (Session)
        SharedPreferences session = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentUsername = session.getString("USER_NAME", "Guest");
        String currentEmail = session.getString("USER_EMAIL", "email@example.com");

        // Set Nama & Email sementara dari lokal
        if (tvName != null) tvName.setText(currentUsername);
        if (tvEmail != null) tvEmail.setText(currentEmail);

        // Hubungkan ke Firebase
        userRef = FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users").child(currentUsername);

        // Tombol Edit Profil
        View btnEditProfile = findViewById(R.id.btn_edit_profile);
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class)));
        }

        // Tombol Logout
        View btnLogout = findViewById(R.id.btn_logout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                session.edit().clear().apply(); // Hapus sesi login
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }

        // Tombol Navigasi Lain (Menu)
        View btnWishlist = findViewById(R.id.btn_wishlist);
        if (btnWishlist != null) {
            btnWishlist.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, WishlistActivity.class)));
        }

        View btnHistory = findViewById(R.id.btn_my_bookings);
        if (btnHistory != null) {
            btnHistory.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, HistoryActivity.class)));
        }

        setupBottomNavigation();
        loadRealtimeProfileData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Memuat ulang data saat kembali dari halaman Edit Profile
        loadRealtimeProfileData();
    }

    // FUNGSI BARU: Sinkronisasi data asli (Poin, No HP, Email) langsung dari Internet
    private void loadRealtimeProfileData() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Update Email & Phone jika ada perubahan dari Edit Profile
                    if (snapshot.hasChild("email") && tvEmail != null) {
                        tvEmail.setText(snapshot.child("email").getValue(String.class));
                    }

                    // Baca Poin Pelanggan
                    int points = 0;
                    if (snapshot.hasChild("points")) {
                        try {
                            points = Integer.parseInt(String.valueOf(snapshot.child("points").getValue()));
                        } catch (Exception e) { points = 0; }
                    }

                    // Terapkan Poin ke Teks
                    if (tvPoints != null) tvPoints.setText(points + " pts");

                    // Terapkan Warna Tier Member
                    if (tvMemberTier != null && cardMember != null) {
                        if (points >= 500) {
                            tvMemberTier.setText("Platinum Member");
                            cardMember.setBackgroundResource(R.drawable.bg_member_platinum); // Pastikan drawable ini ada
                        } else if (points >= 300) {
                            tvMemberTier.setText("Gold Member");
                            cardMember.setBackgroundResource(R.drawable.bg_member_gold);
                        } else if (points >= 150) {
                            tvMemberTier.setText("Silver Member");
                            cardMember.setBackgroundResource(R.drawable.bg_member_silver);
                        } else {
                            tvMemberTier.setText("Bronze Member");
                            cardMember.setBackgroundResource(R.drawable.bg_card_rounded); // Warna default Bronze
                        }
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_profile);
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(this, MainNavigationActivity.class));
                    overridePendingTransition(0, 0); finish(); return true;
                } else if (id == R.id.nav_explore) {
                    startActivity(new Intent(this, ExploreActivity.class));
                    overridePendingTransition(0, 0); finish(); return true;
                } else if (id == R.id.nav_history) {
                    startActivity(new Intent(this, HistoryActivity.class));
                    overridePendingTransition(0, 0); finish(); return true;
                } else if (id == R.id.nav_profile) {
                    return true;
                }
                return false;
            });
        }
    }
}