package com.example.nailzbynutz;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import com.google.firebase.database.*;

public class PaymentInfoActivity extends AppCompatActivity {

    private LinearLayout paymentHistoryContainer;
    private DatabaseReference bookingsRef;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_info);

        findViewById(R.id.btn_back_payment_info).setOnClickListener(v -> finish());
        paymentHistoryContainer = findViewById(R.id.payment_history_container);

        // Ambil nama user yang sedang login
        SharedPreferences session = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentUsername = session.getString("USER_NAME", "Pelanggan");

        bookingsRef = FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("bookings");

        loadUnpaidBookings();
    }

    private void loadUnpaidBookings() {
        // Hanya ambil data milik user ini
        bookingsRef.orderByChild("customerName").equalTo(currentUsername).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                paymentHistoryContainer.removeAllViews();
                boolean hasItem = false;
                Typeface poppinsBold = ResourcesCompat.getFont(PaymentInfoActivity.this, R.font.poppins_bold);
                Typeface poppinsMedium = ResourcesCompat.getFont(PaymentInfoActivity.this, R.font.poppins_medium);

                for (DataSnapshot data : snapshot.getChildren()) {
                    String paymentStatus = data.child("paymentStatus").getValue(String.class);
                    if (paymentStatus == null) paymentStatus = "Menunggu Pembayaran";

                    // HANYA TAMPILKAN YANG BELUM LUNAS
                    if (!paymentStatus.equals("Lunas")) {
                        hasItem = true;
                        String serviceType = data.child("serviceType").getValue(String.class);
                        String date = data.child("date").getValue(String.class);
                        String time = data.child("time").getValue(String.class);

                        if (serviceType == null) serviceType = "Custom Nails";

                        CardView card = new CardView(PaymentInfoActivity.this);
                        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        cardParams.setMargins(0, 0, 0, 20);
                        card.setLayoutParams(cardParams);
                        card.setRadius(24);
                        card.setCardElevation(4);
                        card.setCardBackgroundColor(Color.WHITE);

                        LinearLayout mainContent = new LinearLayout(PaymentInfoActivity.this);
                        mainContent.setOrientation(LinearLayout.VERTICAL);
                        mainContent.setPadding(24, 24, 24, 24);

                        TextView tvTitle = new TextView(PaymentInfoActivity.this);
                        tvTitle.setText(serviceType);
                        tvTitle.setTextSize(15);
                        tvTitle.setTypeface(poppinsBold);
                        tvTitle.setTextColor(Color.parseColor("#333333"));
                        mainContent.addView(tvTitle);

                        TextView tvDateTime = new TextView(PaymentInfoActivity.this);
                        tvDateTime.setText("📅 " + date + "  |  🕒 " + time);
                        tvDateTime.setTextSize(12);
                        tvDateTime.setTypeface(poppinsMedium);
                        tvDateTime.setTextColor(Color.parseColor("#808080"));
                        tvDateTime.setPadding(0, 4, 0, 16);
                        mainContent.addView(tvDateTime);

                        TextView tvPayStatus = new TextView(PaymentInfoActivity.this);
                        tvPayStatus.setText("Status: " + paymentStatus);
                        tvPayStatus.setTextSize(14);
                        tvPayStatus.setTypeface(poppinsBold);
                        tvPayStatus.setTextColor(Color.parseColor("#D6001C")); // Warna Merah untuk menarik perhatian

                        mainContent.addView(tvPayStatus);
                        card.addView(mainContent);

                        // Menambahkan di urutan paling atas
                        paymentHistoryContainer.addView(card, 0);
                    }
                }

                if (!hasItem) {
                    TextView empty = new TextView(PaymentInfoActivity.this);
                    empty.setText("Hore! Tidak ada tagihan yang belum dibayar 🎉");
                    empty.setGravity(Gravity.CENTER);
                    empty.setPadding(0, 80, 0, 0);
                    empty.setTextColor(Color.parseColor("#808080"));
                    empty.setTypeface(poppinsMedium);
                    paymentHistoryContainer.addView(empty);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}