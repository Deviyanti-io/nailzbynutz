package com.example.nailzbynutz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class OwnerDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_dashboard);

        CardView btnManageBookings = findViewById(R.id.btn_manage_bookings);
        CardView btnManageCatalog = findViewById(R.id.btn_manage_catalog);
        Button btnLogoutOwner = findViewById(R.id.btn_logout_owner);

        btnManageBookings.setOnClickListener(v -> {
            // Arahkan ke activity khusus untuk ACC / Cancel booking Firebase
            startActivity(new Intent(OwnerDashboardActivity.this, OwnerManageBookingActivity.class));
        });

        btnManageCatalog.setOnClickListener(v -> {
            // Arahkan ke activity khusus untuk Update Katalog / Wishlist
            startActivity(new Intent(OwnerDashboardActivity.this, OwnerManageCatalogActivity.class));
        });

        btnLogoutOwner.setOnClickListener(v -> {
            // Kembali ke halaman Login
            startActivity(new Intent(OwnerDashboardActivity.this, LoginActivity.class));
            finish();
        });
    }
}