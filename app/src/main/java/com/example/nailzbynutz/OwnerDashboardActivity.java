package com.example.nailzbynutz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;

public class OwnerDashboardActivity extends AppCompatActivity {

    private TextView tvActiveOrders, tvTotalRevenue;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_dashboard);

        // PERBAIKAN 1: Menggunakan "View" secara umum agar tidak crash jika di XML Anda menggunakan LinearLayout/Button/CardView
        View btnManageBookings = findViewById(R.id.btn_manage_bookings);
        View btnManageCatalog = findViewById(R.id.btn_manage_catalog);
        View btnLogoutOwner = findViewById(R.id.btn_logout_owner);

        tvActiveOrders = findViewById(R.id.tv_active_orders);
        tvTotalRevenue = findViewById(R.id.tv_total_revenue);

        DatabaseReference bookingsRef = FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("bookings");

        bookingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int activeOrders = 0;
                long totalRevenue = 0;

                for (DataSnapshot bookingSnap : snapshot.getChildren()) {
                    String status = bookingSnap.child("status").getValue(String.class);

                    if ("Confirmed".equals(status) || "Pending".equals(status)) {
                        activeOrders++;
                    }

                    if ("Completed".equals(status)) {
                        // PERBAIKAN 2: Anti-crash saat menghitung harga dari Firebase
                        Object priceObj = bookingSnap.child("grandTotal").getValue();
                        if (priceObj != null) {
                            try {
                                totalRevenue += Long.parseLong(priceObj.toString());
                            } catch (Exception e) {}
                        }
                    }
                }

                Locale localeID = new Locale("in", "ID");
                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                formatRupiah.setMaximumFractionDigits(0);

                if (tvActiveOrders != null) tvActiveOrders.setText(activeOrders + " Pesanan Aktif");
                if (tvTotalRevenue != null) tvTotalRevenue.setText(formatRupiah.format(totalRevenue));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        if (btnManageBookings != null) {
            btnManageBookings.setOnClickListener(v -> startActivity(new Intent(OwnerDashboardActivity.this, OwnerManageBookingActivity.class)));
        }

        if (btnManageCatalog != null) {
            btnManageCatalog.setOnClickListener(v -> startActivity(new Intent(OwnerDashboardActivity.this, OwnerManageCatalogActivity.class)));
        }

        if (btnLogoutOwner != null) {
            btnLogoutOwner.setOnClickListener(v -> {
                startActivity(new Intent(OwnerDashboardActivity.this, LoginActivity.class));
                finish();
            });
        }
    }
}