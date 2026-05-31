package com.example.nailzbynutz;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Calendar;

public class HistoryActivity extends AppCompatActivity {

    private LinearLayout bookingContainer;
    private BottomNavigationView bottomNav;
    private Typeface poppinsMedium, poppinsBold, fredoka;
    private TextView tabUpcoming, tabComplete;
    private boolean isUpcomingTab = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        bookingContainer = findViewById(R.id.bookingContainer);
        bottomNav = findViewById(R.id.bottom_navigation);
        tabUpcoming = findViewById(R.id.tab_upcoming);
        tabComplete = findViewById(R.id.tab_complete);

        poppinsMedium = ResourcesCompat.getFont(this, R.font.poppins_medium);
        poppinsBold = ResourcesCompat.getFont(this, R.font.poppins_bold);
        fredoka = ResourcesCompat.getFont(this, R.font.fredoka_bold);

        findViewById(R.id.btn_back_history).setOnClickListener(v -> finish());

        tabUpcoming.setOnClickListener(v -> {
            isUpcomingTab = true;
            updateTabUI();
            loadBookings();
        });

        tabComplete.setOnClickListener(v -> {
            isUpcomingTab = false;
            updateTabUI();
            loadBookings();
        });

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
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
        bottomNav.setSelectedItemId(R.id.nav_history);

        updateTabUI();
        loadBookings();
    }

    private void updateTabUI() {
        if (isUpcomingTab) {
            tabUpcoming.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.lavender_dark)));
            tabUpcoming.setTextColor(Color.WHITE);
            tabComplete.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.TRANSPARENT));
            tabComplete.setTextColor(getColor(R.color.text_secondary));
        } else {
            tabComplete.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.lavender_dark)));
            tabComplete.setTextColor(Color.WHITE);
            tabUpcoming.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.TRANSPARENT));
            tabUpcoming.setTextColor(getColor(R.color.text_secondary));
        }
    }

    private void loadBookings() {
        bookingContainer.removeAllViews();
        SharedPreferences prefs = getSharedPreferences("BookingData", MODE_PRIVATE);
        String json = prefs.getString("bookings_list", "[]");
        boolean hasItem = false;

        try {
            JSONArray bookings = new JSONArray(json);
            for (int i = bookings.length() - 1; i >= 0; i--) {
                JSONObject booking = bookings.getJSONObject(i);
                String status = booking.getString("status");

                boolean shouldShow = false;
                if (isUpcomingTab && status.equals("Confirmed")) {
                    shouldShow = true;
                } else if (!isUpcomingTab && (status.equals("Completed") || status.equals("Canceled"))) {
                    shouldShow = true;
                }

                if (shouldShow) {
                    addBookingCard(booking, i, status);
                    hasItem = true;
                }
            }
            if (!hasItem) showEmptyMessage();
        } catch (Exception e) {
            e.printStackTrace();
            showEmptyMessage();
        }
    }

    private void showEmptyMessage() {
        TextView empty = new TextView(this);
        empty.setText(isUpcomingTab ? "Belum ada booking mendatang" : "Belum ada riwayat booking");
        empty.setGravity(Gravity.CENTER);
        empty.setPadding(0, 100, 0, 0);
        empty.setTextColor(getColor(R.color.text_secondary));
        empty.setTypeface(poppinsMedium);
        bookingContainer.addView(empty);
    }

    private void addBookingCard(JSONObject booking, int index, String status) throws Exception {
        String date = booking.getString("date");
        String month = booking.getString("month");
        String title = booking.optString("title", "Pesanan: Layanan");
        String subtitle = booking.getString("subtitle");
        String time = booking.getString("time");
        String colorHex = booking.optString("colorHex", "#D6001C");
        String paymentStatus = booking.optString("paymentStatus", "Menunggu Pembayaran");

        CardView card = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, 24);
        card.setLayoutParams(cardParams);
        card.setRadius(32);
        card.setCardElevation(2);
        card.setCardBackgroundColor(getColor(R.color.background_card));

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
        tvDate.setText(date); tvDate.setTextSize(22); tvDate.setTextColor(getColor(R.color.lavender_dark)); tvDate.setTypeface(fredoka);
        TextView tvMonth = new TextView(this);
        tvMonth.setText(month); tvMonth.setTextSize(11); tvMonth.setTextColor(getColor(R.color.lavender_dark)); tvMonth.setTypeface(poppinsMedium);
        dateBox.addView(tvDate); dateBox.addView(tvMonth);

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        info.setPadding(16, 0, 0, 0);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title); tvTitle.setTextSize(15); tvTitle.setTypeface(poppinsBold); tvTitle.setTextColor(getColor(R.color.text_primary));

        TextView tvSub = new TextView(this);
        tvSub.setText(subtitle); tvSub.setTextSize(12); tvSub.setTextColor(getColor(R.color.text_secondary)); tvSub.setTypeface(poppinsMedium);

        View colorIndicator = new View(this);
        LinearLayout.LayoutParams colorParams = new LinearLayout.LayoutParams(20, 20);
        colorParams.setMargins(0, 6, 0, 6);
        colorIndicator.setLayoutParams(colorParams);
        GradientDrawable colorCircle = new GradientDrawable();
        colorCircle.setShape(GradientDrawable.OVAL);
        colorCircle.setColor(Color.parseColor(colorHex));
        colorIndicator.setBackground(colorCircle);

        TextView tvTime = new TextView(this);
        tvTime.setText("🕒 " + time); tvTime.setTextSize(12); tvTime.setTextColor(getColor(R.color.text_secondary)); tvTime.setTypeface(poppinsMedium);

        info.addView(tvTitle); info.addView(tvSub); info.addView(colorIndicator); info.addView(tvTime);

        LinearLayout statusCol = new LinearLayout(this);
        statusCol.setOrientation(LinearLayout.VERTICAL);
        statusCol.setGravity(Gravity.END);

        TextView tvStatus = new TextView(this);
        tvStatus.setText(status); tvStatus.setTextSize(12); tvStatus.setTypeface(poppinsBold);
        if (status.equals("Confirmed")) tvStatus.setTextColor(getColor(R.color.success));
        else if (status.equals("Canceled")) tvStatus.setTextColor(Color.parseColor("#D6001C"));
        else tvStatus.setTextColor(getColor(R.color.lavender_dark));
        tvStatus.setPadding(0, 0, 0, 4);
        statusCol.addView(tvStatus);

        TextView tvPayStatus = new TextView(this);
        tvPayStatus.setText(paymentStatus); tvPayStatus.setTextSize(10); tvPayStatus.setTypeface(poppinsBold);
        if (paymentStatus.equals("Lunas")) {
            tvPayStatus.setTextColor(getColor(R.color.success));
        } else if (paymentStatus.equals("Menunggu Konfirmasi")) {
            tvPayStatus.setTextColor(Color.parseColor("#FF9800")); // Warna Orange
        } else {
            tvPayStatus.setTextColor(Color.parseColor("#D6001C")); // Merah
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
            btnEdit.setOnClickListener(v -> openDatePicker(index));

            TextView btnCancel = new TextView(this);
            btnCancel.setText("Cancel"); btnCancel.setGravity(Gravity.CENTER); btnCancel.setTextColor(Color.parseColor("#D6001C"));
            btnCancel.setTypeface(poppinsBold); btnCancel.setTextSize(14);
            LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(0, (int) (42 * getResources().getDisplayMetrics().density), 1);
            cancelParams.setMargins(8, 0, 0, 0); btnCancel.setLayoutParams(cancelParams);
            GradientDrawable bgCancel = new GradientDrawable(); bgCancel.setColor(Color.parseColor("#FCE8E6")); bgCancel.setCornerRadius(20);
            btnCancel.setBackground(bgCancel);
            btnCancel.setOnClickListener(v -> {
                new AlertDialog.Builder(this).setTitle("Batalkan Booking").setMessage("Yakin ingin membatalkan booking ini?")
                        .setPositiveButton("Ya", (dialog, which) -> cancelBooking(index)).setNegativeButton("Tidak", null).show();
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
                        .setPositiveButton("Ya", (dialog, which) -> removeBooking(index)).setNegativeButton("Tidak", null).show();
            });
            actionRow.addView(btnDelete);
        }

        mainContent.addView(actionRow); card.addView(mainContent); bookingContainer.addView(card);
    }

    private void openDatePicker(int index) {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String[] months = {"JAN", "FEB", "MAR", "APR", "MEI", "JUN", "JUL", "AGU", "SEP", "OKT", "NOV", "DES"};
            try {
                SharedPreferences prefs = getSharedPreferences("BookingData", MODE_PRIVATE);
                JSONArray array = new JSONArray(prefs.getString("bookings_list", "[]"));
                JSONObject obj = array.getJSONObject(index);
                obj.put("date", String.valueOf(dayOfMonth));
                obj.put("month", months[month]);
                prefs.edit().putString("bookings_list", array.toString()).apply();
                Toast.makeText(this, "Tanggal booking diperbarui!", Toast.LENGTH_SHORT).show();
                loadBookings();
            } catch (Exception e) { e.printStackTrace(); }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void cancelBooking(int index) {
        try {
            SharedPreferences prefs = getSharedPreferences("BookingData", MODE_PRIVATE);
            JSONArray array = new JSONArray(prefs.getString("bookings_list", "[]"));
            JSONObject obj = array.getJSONObject(index);
            obj.put("status", "Canceled");
            prefs.edit().putString("bookings_list", array.toString()).apply();
            Toast.makeText(this, "Booking berhasil dibatalkan", Toast.LENGTH_SHORT).show();
            loadBookings();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void removeBooking(int index) {
        try {
            SharedPreferences prefs = getSharedPreferences("BookingData", MODE_PRIVATE);
            JSONArray array = new JSONArray(prefs.getString("bookings_list", "[]"));
            array.remove(index);
            prefs.edit().putString("bookings_list", array.toString()).apply();
            Toast.makeText(this, "Riwayat dihapus", Toast.LENGTH_SHORT).show();
            loadBookings();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private GradientDrawable getDateBoxBackground() {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setCornerRadius(16f);
        gd.setColor(getColor(R.color.lavender_light));
        return gd;
    }
}