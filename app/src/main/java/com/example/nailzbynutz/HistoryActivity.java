package com.example.nailzbynutz;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import org.json.JSONArray;
import org.json.JSONObject;

public class HistoryActivity extends AppCompatActivity {

    private LinearLayout bookingContainer;
    private Typeface poppinsMedium, poppinsBold, fredoka;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        bookingContainer = findViewById(R.id.bookingContainer);
        poppinsMedium = ResourcesCompat.getFont(this, R.font.poppins_medium);
        poppinsBold = ResourcesCompat.getFont(this, R.font.poppins_bold);
        fredoka = ResourcesCompat.getFont(this, R.font.fredoka_bold);

        findViewById(R.id.btn_back_history).setOnClickListener(v -> finish());

        loadBookings();
    }

    private void loadBookings() {
        bookingContainer.removeAllViews();
        SharedPreferences prefs = getSharedPreferences("BookingData", MODE_PRIVATE);
        String json = prefs.getString("bookings_list", "[]");
        try {
            JSONArray bookings = new JSONArray(json);
            if (bookings.length() == 0) {
                TextView empty = new TextView(this);
                empty.setText("Belum ada booking");
                empty.setGravity(Gravity.CENTER);
                empty.setPadding(0, 100, 0, 0);
                empty.setTextColor(getColor(R.color.text_secondary));
                empty.setTypeface(poppinsMedium);
                bookingContainer.addView(empty);
                return;
            }
            for (int i = bookings.length() - 1; i >= 0; i--) {
                JSONObject booking = bookings.getJSONObject(i);
                addBookingCard(booking);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addBookingCard(JSONObject booking) throws Exception {
        String date = booking.getString("date");
        String month = booking.getString("month");
        String title = booking.getString("title");
        String subtitle = booking.getString("subtitle");
        String time = booking.getString("time");
        String artist = booking.getString("artist");
        String status = booking.getString("status");
        String colorHex = booking.optString("colorHex", "#D6001C");

        CardView card = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 24);
        card.setLayoutParams(cardParams);
        card.setRadius(32);
        card.setCardElevation(2);
        card.setCardBackgroundColor(getColor(R.color.background_card));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(24, 24, 24, 24);

        // Top row: date box + info
        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

        // Date box
        LinearLayout dateBox = new LinearLayout(this);
        dateBox.setOrientation(LinearLayout.VERTICAL);
        dateBox.setGravity(Gravity.CENTER);
        dateBox.setPadding(16, 12, 16, 12);
        dateBox.setBackground(getDateBoxBackground());
        TextView tvDate = new TextView(this);
        tvDate.setText(date);
        tvDate.setTextSize(22);
        tvDate.setTextColor(getColor(R.color.lavender_dark));
        tvDate.setTypeface(fredoka);
        TextView tvMonth = new TextView(this);
        tvMonth.setText(month);
        tvMonth.setTextSize(11);
        tvMonth.setTextColor(getColor(R.color.lavender_dark));
        tvMonth.setTypeface(poppinsMedium);
        dateBox.addView(tvDate);
        dateBox.addView(tvMonth);

        // Info
        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        info.setPadding(16, 0, 0, 0);
        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(15);
        tvTitle.setTypeface(poppinsBold);
        tvTitle.setTextColor(getColor(R.color.text_primary));
        TextView tvSub = new TextView(this);
        tvSub.setText(subtitle);
        tvSub.setTextSize(12);
        tvSub.setTextColor(getColor(R.color.text_secondary));
        tvSub.setTypeface(poppinsMedium);
        // Color indicator
        View colorIndicator = new View(this);
        LinearLayout.LayoutParams colorParams = new LinearLayout.LayoutParams(20, 20);
        colorParams.setMargins(0, 6, 0, 6);
        colorIndicator.setLayoutParams(colorParams);
        GradientDrawable colorCircle = new GradientDrawable();
        colorCircle.setShape(GradientDrawable.OVAL);
        colorCircle.setColor(Color.parseColor(colorHex));
        colorIndicator.setBackground(colorCircle);
        TextView tvTime = new TextView(this);
        tvTime.setText("🕒 " + time);
        tvTime.setTextSize(12);
        tvTime.setTextColor(getColor(R.color.text_secondary));
        tvTime.setTypeface(poppinsMedium);
        TextView tvArtist = new TextView(this);
        tvArtist.setText("👤 " + artist);
        tvArtist.setTextSize(12);
        tvArtist.setTextColor(getColor(R.color.text_secondary));
        tvArtist.setTypeface(poppinsMedium);

        info.addView(tvTitle);
        info.addView(tvSub);
        info.addView(colorIndicator);
        info.addView(tvTime);
        info.addView(tvArtist);

        TextView tvStatus = new TextView(this);
        tvStatus.setText(status);
        tvStatus.setTextSize(12);
        tvStatus.setTypeface(poppinsBold);
        tvStatus.setTextColor(status.equals("Confirmed") ? getColor(R.color.success) : getColor(R.color.pending));

        topRow.addView(dateBox);
        topRow.addView(info);
        topRow.addView(tvStatus);
        content.addView(topRow);

        // View Details button (optional, bisa ditambah edit/hapus)
        TextView btnDetails = new TextView(this);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                48
        );
        btnParams.topMargin = 16;
        btnDetails.setLayoutParams(btnParams);
        btnDetails.setText("View Details");
        btnDetails.setTextSize(12);
        btnDetails.setTextColor(getColor(R.color.lavender_dark));
        btnDetails.setTypeface(poppinsBold);
        btnDetails.setGravity(Gravity.CENTER);
        btnDetails.setBackground(getButtonBackground());
        content.addView(btnDetails);

        card.addView(content);
        bookingContainer.addView(card);
    }

    private GradientDrawable getDateBoxBackground() {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setCornerRadius(16f);
        gd.setColor(getColor(R.color.lavender_light));
        return gd;
    }

    private GradientDrawable getButtonBackground() {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setCornerRadius(24f);
        gd.setColor(getColor(R.color.lavender_light));
        return gd;
    }
}