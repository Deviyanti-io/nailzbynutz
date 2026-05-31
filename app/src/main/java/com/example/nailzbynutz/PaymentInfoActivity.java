package com.example.nailzbynutz;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import org.json.JSONArray;
import org.json.JSONObject;

public class PaymentInfoActivity extends AppCompatActivity {

    private LinearLayout paymentHistoryContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_info);

        findViewById(R.id.btn_back_payment_info).setOnClickListener(v -> finish());

        paymentHistoryContainer = findViewById(R.id.payment_history_container);

        loadPaymentHistory();
    }

    private void loadPaymentHistory() {
        paymentHistoryContainer.removeAllViews();
        SharedPreferences prefs = getSharedPreferences("BookingData", MODE_PRIVATE);
        String json = prefs.getString("bookings_list", "[]");
        boolean hasItem = false;

        Typeface poppinsBold = ResourcesCompat.getFont(this, R.font.poppins_bold);
        Typeface poppinsMedium = ResourcesCompat.getFont(this, R.font.poppins_medium);

        try {
            JSONArray bookings = new JSONArray(json);
            // Looping mundur agar pesanan terbaru ada di paling atas
            for (int i = bookings.length() - 1; i >= 0; i--) {
                JSONObject booking = bookings.getJSONObject(i);

                String title = booking.optString("title", "Pesanan: Layanan");
                String date = booking.optString("date", "") + " " + booking.optString("month", "");
                String time = booking.optString("time", "");
                String paymentStatus = booking.optString("paymentStatus", "Menunggu Pembayaran");

                // Membuat CardView untuk setiap tagihan
                CardView card = new CardView(this);
                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                cardParams.setMargins(0, 0, 0, 20);
                card.setLayoutParams(cardParams);
                card.setRadius(24);
                card.setCardElevation(2);
                card.setCardBackgroundColor(getColor(R.color.background_card));

                LinearLayout mainContent = new LinearLayout(this);
                mainContent.setOrientation(LinearLayout.VERTICAL);
                mainContent.setPadding(24, 24, 24, 24);

                // Baris atas: Nama Layanan & Tanggal
                TextView tvTitle = new TextView(this);
                tvTitle.setText(title);
                tvTitle.setTextSize(15);
                tvTitle.setTypeface(poppinsBold);
                tvTitle.setTextColor(getColor(R.color.text_primary));
                mainContent.addView(tvTitle);

                TextView tvDateTime = new TextView(this);
                tvDateTime.setText("📅 " + date + "  |  🕒 " + time);
                tvDateTime.setTextSize(12);
                tvDateTime.setTypeface(poppinsMedium);
                tvDateTime.setTextColor(getColor(R.color.text_secondary));
                tvDateTime.setPadding(0, 4, 0, 16);
                mainContent.addView(tvDateTime);

                // Status Pembayaran
                TextView tvPayStatus = new TextView(this);
                tvPayStatus.setText("Status: " + paymentStatus);
                tvPayStatus.setTextSize(14);
                tvPayStatus.setTypeface(poppinsBold);

                if (paymentStatus.equals("Lunas")) {
                    tvPayStatus.setTextColor(getColor(R.color.success)); // Hijau
                } else {
                    tvPayStatus.setTextColor(Color.parseColor("#D6001C")); // Merah
                }

                mainContent.addView(tvPayStatus);
                card.addView(mainContent);
                paymentHistoryContainer.addView(card);

                hasItem = true;
            }

            // Jika belum ada pesanan sama sekali
            if (!hasItem) {
                TextView empty = new TextView(this);
                empty.setText("Belum ada riwayat tagihan.");
                empty.setGravity(Gravity.CENTER);
                empty.setPadding(0, 40, 0, 0);
                empty.setTextColor(getColor(R.color.text_secondary));
                empty.setTypeface(poppinsMedium);
                paymentHistoryContainer.addView(empty);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}