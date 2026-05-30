package com.example.nailzbynutz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Tambahan import untuk Firebase dan HashMap
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class BookingAppointmentActivity extends AppCompatActivity {

    private ImageView btnBack;
    private Button btnNextBooking;
    private CalendarView calendarView;
    private Button btnTime09, btnTime11, btnTime13, btnTime14, btnTime15, btnTime17;
    private String selectedDate = "";
    private String selectedTime = "";

    private String shape, length, colorType, colorHex, finish, sizeReport, notes, uploadedImage;
    private ArrayList<String> addonsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_appointment);

        Intent intent = getIntent();
        shape = intent.getStringExtra("FINAL_SHAPE");
        length = intent.getStringExtra("FINAL_LENGTH");
        colorType = intent.getStringExtra("FINAL_COLOR_TYPE");
        colorHex = intent.getStringExtra("FINAL_COLOR");
        finish = intent.getStringExtra("FINAL_FINISH");
        sizeReport = intent.getStringExtra("FINAL_SIZE");
        notes = intent.getStringExtra("FINAL_NOTES");
        uploadedImage = intent.getStringExtra("FINAL_IMAGE");
        addonsList = intent.getStringArrayListExtra("FINAL_ADDONS");

        if (shape == null) shape = "Almond";
        if (length == null) length = "Medium";
        if (colorType == null) colorType = "Solid";
        if (colorHex == null) colorHex = "#D6001C";
        if (finish == null) finish = "Glossy";
        if (sizeReport == null) sizeReport = "M (16-12-13-12-10mm)";
        if (notes == null) notes = "";
        if (addonsList == null) addonsList = new ArrayList<>();

        btnBack = findViewById(R.id.btnBack);
        btnNextBooking = findViewById(R.id.btnNextBooking);
        calendarView = findViewById(R.id.calendarView);
        btnTime09 = findViewById(R.id.btnTime09);
        btnTime11 = findViewById(R.id.btnTime11);
        btnTime13 = findViewById(R.id.btnTime13);
        btnTime14 = findViewById(R.id.btnTime14);
        btnTime15 = findViewById(R.id.btnTime15);
        btnTime17 = findViewById(R.id.btnTime17);

        btnBack.setOnClickListener(v -> finish());

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
        });

        // Set time slot listeners
        setTimeSlotListener(btnTime09, "09.00");
        setTimeSlotListener(btnTime11, "11.00");
        setTimeSlotListener(btnTime13, "13.00");
        setTimeSlotListener(btnTime14, "14.00");
        setTimeSlotListener(btnTime15, "15.00");
        setTimeSlotListener(btnTime17, "17.00");

        // 1. Inisialisasi Firebase Database untuk Booking
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference bookingsRef = database.getReference("bookings");

        btnNextBooking.setOnClickListener(v -> {
            if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
                Toast.makeText(this, "Pilih tanggal dan waktu!", Toast.LENGTH_SHORT).show();
                return;
            }

            String[] dateParts = selectedDate.split("/");
            String day = dateParts[0];
            int monthNum = Integer.parseInt(dateParts[1]);
            String monthName = getMonthName(monthNum);

            String[] timeParts = selectedTime.split("\\.");
            int startHour = Integer.parseInt(timeParts[0]);
            int startMin = Integer.parseInt(timeParts[1]);
            int endHour = startHour + 1;
            int endMin = startMin + 30;
            if (endMin >= 60) {
                endHour++;
                endMin -= 60;
            }
            String timeRange = String.format("%02d.%02d - %02d.%02d", startHour, startMin, endHour, endMin);
            String subtitle = shape + " • " + length + " • " + colorType;

            // 2. Siapkan data yang akan dikirim ke Firebase menggunakan HashMap
            HashMap<String, Object> firebaseBookingData = new HashMap<>();
            firebaseBookingData.put("date", selectedDate); // Format lengkap
            firebaseBookingData.put("time", timeRange);
            firebaseBookingData.put("artist", "Nut (Top Artist)");
            firebaseBookingData.put("status", "Confirmed");

            // Masukkan juga detail desain kuku agar admin bisa melihatnya
            firebaseBookingData.put("shape", shape);
            firebaseBookingData.put("length", length);
            firebaseBookingData.put("colorType", colorType);
            firebaseBookingData.put("colorHex", colorHex);
            firebaseBookingData.put("finish", finish);
            firebaseBookingData.put("sizeReport", sizeReport);
            firebaseBookingData.put("notes", notes);
            firebaseBookingData.put("addons", addonsList);

            // 3. Kirim ke Firebase!
            bookingsRef.push().setValue(firebaseBookingData)
                    .addOnSuccessListener(aVoid -> {
                        // JIKA SUKSES MASUK FIREBASE, LANJUT SIMPAN LOKAL
                        try {
                            SharedPreferences prefs = getSharedPreferences("BookingData", MODE_PRIVATE);
                            String existingJson = prefs.getString("bookings_list", "[]");

                            JSONArray bookingsArray = new JSONArray(existingJson);
                            JSONObject newBooking = new JSONObject();
                            newBooking.put("date", day);
                            newBooking.put("month", monthName);
                            newBooking.put("title", "Custom Nail");
                            newBooking.put("subtitle", subtitle);
                            newBooking.put("time", timeRange);
                            newBooking.put("artist", "Nut (Top Artist)");
                            newBooking.put("status", "Confirmed");
                            newBooking.put("colorHex", colorHex);
                            bookingsArray.put(newBooking);

                            prefs.edit().putString("bookings_list", bookingsArray.toString()).apply();

                            SharedPreferences pointsPref = getSharedPreferences("UserPoints", MODE_PRIVATE);
                            int currentPoints = pointsPref.getInt("total_points", 0);
                            pointsPref.edit().putInt("total_points", currentPoints + 50).apply();

                            Toast.makeText(this, "Booking berhasil tersimpan di server! +50 poin", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(this, MainNavigationActivity.class));
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Berhasil masuk server, tapi gagal simpan lokal", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // JIKA GAGAL MASUK FIREBASE (Koneksi putus, dsb)
                        Toast.makeText(this, "Gagal booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void setTimeSlotListener(Button btn, String time) {
        btn.setOnClickListener(v -> {
            selectedTime = time;
            resetTimeButtons();
            setActiveTimeButton(btn);
        });
    }

    private void resetTimeButtons() {
        Button[] btns = {btnTime09, btnTime11, btnTime13, btnTime14, btnTime15, btnTime17};
        for (Button btn : btns) {
            if (btn != null) {
                btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.background_card)));
                btn.setTextColor(getColor(R.color.text_primary));
            }
        }
    }

    private void setActiveTimeButton(Button btn) {
        btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.lavender_dark)));
        btn.setTextColor(getColor(R.color.white));
    }

    private String getMonthName(int month) {
        String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
        return months[month - 1];
    }
}