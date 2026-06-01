package com.example.nailzbynutz;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class HistoryActivity extends AppCompatActivity {

    private LinearLayout bookingContainer;
    private BottomNavigationView bottomNav;
    private Typeface poppinsMedium, poppinsBold, fredoka;
    private TextView tabUpcoming, tabComplete;
    private boolean isUpcomingTab = true;

    private DatabaseReference bookingsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        bookingContainer = findViewById(R.id.bookingContainer);
        bottomNav = findViewById(R.id.bottom_navigation);
        tabUpcoming = findViewById(R.id.tab_upcoming);
        tabComplete = findViewById(R.id.tab_complete);

        // SABUK PENGAMAN FONT: Mencegah crash jika font belum ter-load sempurna
        try {
            poppinsMedium = ResourcesCompat.getFont(this, R.font.poppins_medium);
            poppinsBold = ResourcesCompat.getFont(this, R.font.poppins_bold);
            fredoka = ResourcesCompat.getFont(this, R.font.fredoka_bold);
        } catch (Exception e) {
            poppinsMedium = Typeface.DEFAULT;
            poppinsBold = Typeface.DEFAULT_BOLD;
            fredoka = Typeface.DEFAULT_BOLD;
        }

        bookingsRef = FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("bookings");

        View btnBack = findViewById(R.id.btn_back_history);
        if(btnBack != null) btnBack.setOnClickListener(v -> finish());

        if (tabUpcoming != null) {
            tabUpcoming.setOnClickListener(v -> {
                isUpcomingTab = true;
                updateTabUI();
                loadBookingsFromFirebase();
            });
        }

        if (tabComplete != null) {
            tabComplete.setOnClickListener(v -> {
                isUpcomingTab = false;
                updateTabUI();
                loadBookingsFromFirebase();
            });
        }

        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(this, MainNavigationActivity.class));
                    overridePendingTransition(0, 0); finish(); return true;
                } else if (id == R.id.nav_explore) {
                    startActivity(new Intent(this, ExploreActivity.class));
                    overridePendingTransition(0, 0); finish(); return true;
                } else if (id == R.id.nav_history) {
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(this, ProfileActivity.class));
                    overridePendingTransition(0, 0); finish(); return true;
                }
                return false;
            });
            bottomNav.setSelectedItemId(R.id.nav_history);
        }

        updateTabUI();
        loadBookingsFromFirebase();
    }

    private void updateTabUI() {
        try {
            if (isUpcomingTab) {
                tabUpcoming.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.lavender_dark)));
                tabUpcoming.setTextColor(Color.WHITE);
                tabComplete.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                tabComplete.setTextColor(getColor(R.color.text_secondary));
            } else {
                tabComplete.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.lavender_dark)));
                tabComplete.setTextColor(Color.WHITE);
                tabUpcoming.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                tabUpcoming.setTextColor(getColor(R.color.text_secondary));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadBookingsFromFirebase() {
        if (bookingContainer == null) return;

        SharedPreferences session = getSharedPreferences("UserSession", MODE_PRIVATE);
        String currentUsername = session.getString("USER_NAME", "Pelanggan");

        bookingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    bookingContainer.removeAllViews();
                    boolean hasItem = false;

                    for (DataSnapshot categoryFolder : snapshot.getChildren()) {
                        if (categoryFolder.child("customerName").exists()) continue;

                        for (DataSnapshot booking : categoryFolder.getChildren()) {
                            try {
                                // Membaca dengan String.valueOf untuk menghindari DatabaseException (Crash Fatal)
                                String dbCustomerName = String.valueOf(booking.child("customerName").getValue());
                                if (dbCustomerName.equals("null") || !dbCustomerName.equals(currentUsername)) {
                                    continue;
                                }

                                String status = String.valueOf(booking.child("status").getValue());
                                if (status.equals("null")) status = "Pending";

                                boolean shouldShow = false;
                                if (isUpcomingTab && (status.equals("Confirmed") || status.equals("Pending") || status.equals("Upcoming"))) {
                                    shouldShow = true;
                                } else if (!isUpcomingTab && (status.equals("Completed") || status.equals("Canceled"))) {
                                    shouldShow = true;
                                }

                                if (shouldShow) {
                                    addBookingCard(booking, status, categoryFolder.getKey());
                                    hasItem = true;
                                }
                            } catch (Exception e) {
                                e.printStackTrace(); // Jika 1 pesanan rusak, abaikan dan lanjut ke pesanan berikutnya
                            }
                        }
                    }

                    if (!hasItem) showEmptyMessage();
                } catch (Exception e) {
                    showEmptyMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showEmptyMessage();
            }
        });
    }

    private void showEmptyMessage() {
        try {
            bookingContainer.removeAllViews();
            TextView empty = new TextView(this);
            empty.setText(isUpcomingTab ? "Belum ada booking mendatang" : "Belum ada riwayat booking");
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(0, 100, 0, 0);
            empty.setTextColor(getColor(R.color.text_secondary));
            empty.setTypeface(poppinsMedium);
            bookingContainer.addView(empty);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void addBookingCard(DataSnapshot booking, String status, String categoryKey) {
        // SABUK PENGAMAN KARTU: Membungkus seluruh proses menggambar desain di dalam Try-Catch
        try {
            String bookingId = booking.getKey();

            String dateStr = String.valueOf(booking.child("date").getValue());
            if (dateStr.equals("null")) dateStr = "";

            String day = "0";
            String month = "-";
            if (!dateStr.isEmpty() && dateStr.contains("/")) {
                String[] parts = dateStr.split("/");
                day = parts[0];
                try {
                    int m = Integer.parseInt(parts[1]);
                    String[] months = {"JAN", "FEB", "MAR", "APR", "MEI", "JUN", "JUL", "AGU", "SEP", "OKT", "NOV", "DES"};
                    month = months[m - 1];
                } catch (Exception ignored) {}
            } else if (!dateStr.isEmpty()) {
                day = dateStr;
            }

            String title = String.valueOf(booking.child("serviceType").getValue());
            if (title.equals("null") || title.isEmpty()) title = categoryKey.replace("_", " ");

            String shape = String.valueOf(booking.child("shape").getValue());
            String length = String.valueOf(booking.child("length").getValue());
            String colorType = String.valueOf(booking.child("colorType").getValue());

            String subtitle = "";
            if (!shape.equals("null") && !shape.isEmpty()) subtitle += shape;
            if (!length.equals("null") && !length.isEmpty()) subtitle += (subtitle.isEmpty() ? "" : " • ") + length;
            if (!colorType.equals("null") && !colorType.isEmpty()) subtitle += (subtitle.isEmpty() ? "" : " • ") + colorType;
            if (subtitle.isEmpty()) subtitle = "Layanan Reguler";

            String time = String.valueOf(booking.child("time").getValue());
            if (time.equals("null") || time.isEmpty()) time = "Menunggu Waktu";

            String colorHex = String.valueOf(booking.child("colorHex").getValue());
            if (colorHex.equals("null") || colorHex.isEmpty()) colorHex = "#D6001C";

            String paymentStatus = String.valueOf(booking.child("paymentStatus").getValue());
            if (paymentStatus.equals("null") || paymentStatus.isEmpty()) paymentStatus = "Menunggu Konfirmasi";

            CardView card = new CardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.setMargins(0, 0, 0, 24);
            card.setLayoutParams(cardParams);
            card.setRadius(32);
            card.setCardElevation(2);
            try { card.setCardBackgroundColor(getColor(R.color.background_card)); }
            catch (Exception e) { card.setCardBackgroundColor(Color.WHITE); }

            LinearLayout mainContent = new LinearLayout(this);
            mainContent.setOrientation(LinearLayout.VERTICAL);
            mainContent.setPadding(24, 24, 24, 24);

            LinearLayout topRow = new LinearLayout(this);
            topRow.setOrientation(LinearLayout.HORIZONTAL);
            topRow.setGravity(Gravity.CENTER_VERTICAL);

            LinearLayout dateBox = new LinearLayout(this);
            dateBox.setOrientation(LinearLayout.VERTICAL);
            dateBox.setGravity(Gravity.CENTER);
            dateBox.setPadding(16, 12, 16, 12);
            dateBox.setBackground(getDateBoxBackground());

            TextView tvDate = new TextView(this);
            tvDate.setText(day); tvDate.setTextSize(22); tvDate.setTypeface(fredoka);
            try { tvDate.setTextColor(getColor(R.color.lavender_dark)); } catch (Exception e) { tvDate.setTextColor(Color.parseColor("#5E35B1")); }

            TextView tvMonth = new TextView(this);
            tvMonth.setText(month); tvMonth.setTextSize(11); tvMonth.setTypeface(poppinsMedium);
            try { tvMonth.setTextColor(getColor(R.color.lavender_dark)); } catch (Exception e) { tvMonth.setTextColor(Color.parseColor("#5E35B1")); }

            dateBox.addView(tvDate); dateBox.addView(tvMonth);

            LinearLayout info = new LinearLayout(this);
            info.setOrientation(LinearLayout.VERTICAL);
            info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            info.setPadding(16, 0, 0, 0);

            TextView tvTitle = new TextView(this);
            tvTitle.setText(title); tvTitle.setTextSize(15); tvTitle.setTypeface(poppinsBold);
            try { tvTitle.setTextColor(getColor(R.color.text_primary)); } catch (Exception e) { tvTitle.setTextColor(Color.BLACK); }

            TextView tvSub = new TextView(this);
            tvSub.setText(subtitle); tvSub.setTextSize(12); tvSub.setTypeface(poppinsMedium);
            try { tvSub.setTextColor(getColor(R.color.text_secondary)); } catch (Exception e) { tvSub.setTextColor(Color.GRAY); }

            View colorIndicator = new View(this);
            LinearLayout.LayoutParams colorParams = new LinearLayout.LayoutParams(20, 20);
            colorParams.setMargins(0, 6, 0, 6);
            colorIndicator.setLayoutParams(colorParams);
            GradientDrawable colorCircle = new GradientDrawable();
            colorCircle.setShape(GradientDrawable.OVAL);
            try { colorCircle.setColor(Color.parseColor(colorHex)); }
            catch (Exception e) { colorCircle.setColor(Color.parseColor("#D6001C")); }
            colorIndicator.setBackground(colorCircle);

            TextView tvTime = new TextView(this);
            tvTime.setText("🕒 " + time); tvTime.setTextSize(12); tvTime.setTypeface(poppinsMedium);
            try { tvTime.setTextColor(getColor(R.color.text_secondary)); } catch (Exception e) { tvTime.setTextColor(Color.GRAY); }

            info.addView(tvTitle); info.addView(tvSub); info.addView(colorIndicator); info.addView(tvTime);

            LinearLayout statusCol = new LinearLayout(this);
            statusCol.setOrientation(LinearLayout.VERTICAL);
            statusCol.setGravity(Gravity.END);

            TextView tvStatus = new TextView(this);
            tvStatus.setText(status); tvStatus.setTextSize(12); tvStatus.setTypeface(poppinsBold);
            if (status.equals("Confirmed")) {
                try { tvStatus.setTextColor(getColor(R.color.success)); } catch (Exception e) { tvStatus.setTextColor(Color.parseColor("#4CAF50")); }
            } else if (status.equals("Canceled")) {
                tvStatus.setTextColor(Color.parseColor("#D6001C"));
            } else {
                try { tvStatus.setTextColor(getColor(R.color.lavender_dark)); } catch (Exception e) { tvStatus.setTextColor(Color.parseColor("#5E35B1")); }
            }
            tvStatus.setPadding(0, 0, 0, 4);
            statusCol.addView(tvStatus);

            TextView tvPayStatus = new TextView(this);
            tvPayStatus.setText(paymentStatus); tvPayStatus.setTextSize(10); tvPayStatus.setTypeface(poppinsBold);
            if (paymentStatus.equals("Lunas")) {
                try { tvPayStatus.setTextColor(getColor(R.color.success)); } catch (Exception e) { tvPayStatus.setTextColor(Color.parseColor("#4CAF50")); }
            } else if (paymentStatus.equals("Menunggu Konfirmasi") || paymentStatus.equals("Menunggu Pembayaran")) {
                tvPayStatus.setTextColor(Color.parseColor("#FF9800"));
            } else {
                tvPayStatus.setTextColor(Color.parseColor("#D6001C"));
                tvPayStatus.setOnClickListener(v -> {
                    startActivity(new Intent(HistoryActivity.this, PaymentInfoActivity.class));
                });
            }
            statusCol.addView(tvPayStatus);

            topRow.addView(dateBox); topRow.addView(info); topRow.addView(statusCol);
            mainContent.addView(topRow);

            LinearLayout actionRow = new LinearLayout(this);
            actionRow.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.setMargins(0, 20, 0, 0);
            actionRow.setLayoutParams(rowParams);

            if (isUpcomingTab) {
                actionRow.setWeightSum(2);
                TextView btnEdit = new TextView(this);
                btnEdit.setText("Edit"); btnEdit.setGravity(Gravity.CENTER); btnEdit.setTextColor(Color.parseColor("#4A148C"));
                btnEdit.setTypeface(poppinsBold); btnEdit.setTextSize(14);
                LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(0, (int) (42 * getResources().getDisplayMetrics().density), 1);
                editParams.setMargins(0, 0, 8, 0); btnEdit.setLayoutParams(editParams);
                GradientDrawable bgEdit = new GradientDrawable(); bgEdit.setColor(Color.parseColor("#EEF0FA")); bgEdit.setCornerRadius(20);
                btnEdit.setBackground(bgEdit);
                btnEdit.setOnClickListener(v -> openDatePicker(bookingId, categoryKey));

                TextView btnCancel = new TextView(this);
                btnCancel.setText("Cancel"); btnCancel.setGravity(Gravity.CENTER); btnCancel.setTextColor(Color.parseColor("#D6001C"));
                btnCancel.setTypeface(poppinsBold); btnCancel.setTextSize(14);
                LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(0, (int) (42 * getResources().getDisplayMetrics().density), 1);
                cancelParams.setMargins(8, 0, 0, 0); btnCancel.setLayoutParams(cancelParams);
                GradientDrawable bgCancel = new GradientDrawable(); bgCancel.setColor(Color.parseColor("#FCE8E6")); bgCancel.setCornerRadius(20);
                btnCancel.setBackground(bgCancel);
                btnCancel.setOnClickListener(v -> {
                    new AlertDialog.Builder(this).setTitle("Batalkan Booking").setMessage("Yakin ingin membatalkan booking ini?")
                            .setPositiveButton("Ya", (dialog, which) -> cancelBooking(bookingId, categoryKey)).setNegativeButton("Tidak", null).show();
                });

                actionRow.addView(btnEdit); actionRow.addView(btnCancel);
            } else {
                actionRow.setWeightSum(1);
                TextView btnDelete = new TextView(this);
                btnDelete.setText("Hapus Riwayat"); btnDelete.setGravity(Gravity.CENTER); btnDelete.setTextColor(Color.parseColor("#D6001C"));
                btnDelete.setTypeface(poppinsBold); btnDelete.setTextSize(14);
                LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(0, (int) (42 * getResources().getDisplayMetrics().density), 1);
                btnDelete.setLayoutParams(deleteParams);
                GradientDrawable bgDelete = new GradientDrawable(); bgDelete.setColor(Color.parseColor("#FCE8E6")); bgDelete.setCornerRadius(20);
                btnDelete.setBackground(bgDelete);
                btnDelete.setOnClickListener(v -> {
                    new AlertDialog.Builder(this).setTitle("Hapus Riwayat").setMessage("Yakin hapus riwayat ini secara permanen?")
                            .setPositiveButton("Ya", (dialog, which) -> removeBooking(bookingId, categoryKey)).setNegativeButton("Tidak", null).show();
                });
                actionRow.addView(btnDelete);
            }

            mainContent.addView(actionRow); card.addView(mainContent); bookingContainer.addView(card);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openDatePicker(String bookingId, String categoryKey) {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String newDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            bookingsRef.child(categoryKey).child(bookingId).child("date").setValue(newDate).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Tanggal booking diperbarui di Server!", Toast.LENGTH_SHORT).show();
            });
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void cancelBooking(String bookingId, String categoryKey) {
        bookingsRef.child(categoryKey).child(bookingId).child("status").setValue("Canceled").addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Booking berhasil dibatalkan", Toast.LENGTH_SHORT).show();
        });
    }

    private void removeBooking(String bookingId, String categoryKey) {
        bookingsRef.child(categoryKey).child(bookingId).removeValue().addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Riwayat dihapus secara permanen", Toast.LENGTH_SHORT).show();
        });
    }

    private GradientDrawable getDateBoxBackground() {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setCornerRadius(16f);
        try { gd.setColor(getColor(R.color.lavender_light)); }
        catch (Exception e) { gd.setColor(Color.parseColor("#EDE7F6")); }
        return gd;
    }
}