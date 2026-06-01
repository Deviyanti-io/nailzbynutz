package com.example.nailzbynutz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;
import java.util.HashMap;

public class BookingAppointmentActivity extends AppCompatActivity {

    private String selectedDate = "";
    private String selectedTime = "";
    private DatabaseReference bookingsRef;
    private Button btnTime09, btnTime11, btnTime13, btnTime14, btnTime15, btnTime17;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_appointment);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        Button btnNextBooking = findViewById(R.id.btnNextBooking);
        btnNextBooking.setText("Confirm & Proceed to Payment");

        CalendarView calendarView = findViewById(R.id.calendarView);
        btnTime09 = findViewById(R.id.btnTime09);
        btnTime11 = findViewById(R.id.btnTime11);
        btnTime13 = findViewById(R.id.btnTime13);
        btnTime14 = findViewById(R.id.btnTime14);
        btnTime15 = findViewById(R.id.btnTime15);
        btnTime17 = findViewById(R.id.btnTime17);

        bookingsRef = FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("bookings");

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            checkQuotaForDate(selectedDate);
        });

        setTimeSlotListener(btnTime09, "09.00");
        setTimeSlotListener(btnTime11, "11.00");
        setTimeSlotListener(btnTime13, "13.00");
        setTimeSlotListener(btnTime14, "14.00");
        setTimeSlotListener(btnTime15, "15.00");
        setTimeSlotListener(btnTime17, "17.00");

        btnNextBooking.setOnClickListener(v -> {
            if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
                Toast.makeText(this, "Silakan pilih tanggal dan waktu!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(BookingAppointmentActivity.this, PaymentActivity.class);
            if (getIntent().getExtras() != null) {
                intent.putExtras(getIntent().getExtras());
            }
            intent.putExtra("BOOKING_DATE", selectedDate);
            intent.putExtra("BOOKING_TIME", selectedTime);
            startActivity(intent);
        });
    }

    private void checkQuotaForDate(String targetDate) {
        selectedTime = "";
        HashMap<String, Integer> timeSlotCounts = new HashMap<>();
        timeSlotCounts.put("09.00", 0);
        timeSlotCounts.put("11.00", 0);
        timeSlotCounts.put("13.00", 0);
        timeSlotCounts.put("14.00", 0);
        timeSlotCounts.put("15.00", 0);
        timeSlotCounts.put("17.00", 0);

        // PERBAIKAN: Menggunakan addListenerForSingleValueEvent langsung ke root 'bookings'
        // Lalu kita gunakan Dobel Loop untuk mencari data tanggal yang cocok di dalam laci kategori
        bookingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Loop 1: Buka Laci Kategori (Manicure, dsb)
                for (DataSnapshot categoryFolder : snapshot.getChildren()) {

                    // Loop 2: Buka pesanan di dalamnya
                    for (DataSnapshot data : categoryFolder.getChildren()) {
                        try {
                            String dbDate = data.child("date").getValue(String.class);
                            String status = data.child("status").getValue(String.class);

                            // Cek apakah tanggalnya sama dan statusnya Confirmed
                            if (targetDate.equals(dbDate) && "Confirmed".equals(status)) {
                                String timeRange = data.child("time").getValue(String.class);
                                if (timeRange != null) {
                                    String startTime = timeRange.split(" ")[0];
                                    if (timeSlotCounts.containsKey(startTime)) {
                                        timeSlotCounts.put(startTime, timeSlotCounts.get(startTime) + 1);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            // Abaikan jika ada data yang rusak, jangan force close
                            e.printStackTrace();
                        }
                    }
                }

                // Update UI tombol setelah semua folder dicek
                updateButtonQuota(btnTime09, timeSlotCounts.get("09.00"), "09.00");
                updateButtonQuota(btnTime11, timeSlotCounts.get("11.00"), "11.00");
                updateButtonQuota(btnTime13, timeSlotCounts.get("13.00"), "13.00");
                updateButtonQuota(btnTime14, timeSlotCounts.get("14.00"), "14.00");
                updateButtonQuota(btnTime15, timeSlotCounts.get("15.00"), "15.00");
                updateButtonQuota(btnTime17, timeSlotCounts.get("17.00"), "17.00");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void updateButtonQuota(Button btn, int count, String timeStr) {
        if (count >= 5) {
            btn.setEnabled(false);
            btn.setText(timeStr + "\n(Full)");
            // Paksa warna merah pekat dan teks putih
            btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#D6001C")));
            btn.setTextColor(android.graphics.Color.WHITE);
        } else {
            btn.setEnabled(true);
            btn.setText(timeStr);
            // Kembalikan ke warna asli
            btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.background_card)));
            btn.setTextColor(getColor(R.color.text_primary));
        }
    }

    private void setTimeSlotListener(Button btn, String time) {
        btn.setOnClickListener(v -> {
            selectedTime = time;
            Button[] btns = {btnTime09, btnTime11, btnTime13, btnTime14, btnTime15, btnTime17};
            for (Button b : btns) {
                // KUNCI PERBAIKAN: Hanya kembalikan warna tombol jika tombol itu TIDAK FULL (masih aktif)
                if (b.isEnabled()) {
                    b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.background_card)));
                    b.setTextColor(getColor(R.color.text_primary));
                }
            }
            // Warnai ungu tombol yang baru saja dipilih
            btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.lavender_dark)));
            btn.setTextColor(android.graphics.Color.WHITE);
        });
    }
}