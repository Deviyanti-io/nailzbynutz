package com.example.nailzbynutz;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;

public class OwnerManageBookingActivity extends AppCompatActivity {

    private LinearLayout bookingContainerAdmin;
    private DatabaseReference bookingsRef;
    private Typeface poppinsBold, poppinsMedium;
    private TextView tabUpcoming, tabComplete, tabCancel, tvActiveFilter;
    private String currentTabStatus = "Confirmed";
    private String currentDateFilter = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_manage_booking);

        poppinsBold = ResourcesCompat.getFont(this, R.font.poppins_bold);
        poppinsMedium = ResourcesCompat.getFont(this, R.font.poppins_medium);

        findViewById(R.id.btn_back_manage).setOnClickListener(v -> finish());
        bookingContainerAdmin = findViewById(R.id.bookingContainerAdmin);
        tvActiveFilter = findViewById(R.id.tv_active_filter);

        tabUpcoming = findViewById(R.id.tab_upcoming);
        tabComplete = findViewById(R.id.tab_complete);
        tabCancel = findViewById(R.id.tab_cancel);

        tabUpcoming.setOnClickListener(v -> switchTab(tabUpcoming, "Confirmed"));
        tabComplete.setOnClickListener(v -> switchTab(tabComplete, "Completed"));
        tabCancel.setOnClickListener(v -> switchTab(tabCancel, "Canceled"));

        findViewById(R.id.btn_filter_date).setOnClickListener(v -> showDatePicker());
        tvActiveFilter.setOnClickListener(v -> {
            currentDateFilter = "";
            tvActiveFilter.setVisibility(View.GONE);
            loadAllBookingsFromFirebase();
        });

        bookingsRef = FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("bookings");

        switchTab(tabUpcoming, "Confirmed");
    }

    private void switchTab(TextView activeTab, String targetStatus) {
        tabUpcoming.setBackgroundResource(0); tabUpcoming.setTextColor(Color.parseColor("#9E9CC8"));
        tabComplete.setBackgroundResource(0); tabComplete.setTextColor(Color.parseColor("#9E9CC8"));
        tabCancel.setBackgroundResource(0); tabCancel.setTextColor(Color.parseColor("#9E9CC8"));

        activeTab.setBackgroundResource(R.drawable.bg_card_rounded);
        activeTab.getBackground().setTint(getColor(R.color.lavender_dark));
        activeTab.setTextColor(Color.WHITE);

        currentTabStatus = targetStatus;
        loadAllBookingsFromFirebase();
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            currentDateFilter = dayOfMonth + "/" + (month + 1) + "/" + year;
            tvActiveFilter.setText("📅 Filter: " + currentDateFilter + " (Klik untuk reset)");
            tvActiveFilter.setVisibility(View.VISIBLE);
            loadAllBookingsFromFirebase();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void loadAllBookingsFromFirebase() {
        bookingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingContainerAdmin.removeAllViews();
                boolean hasBookings = false;

                for (DataSnapshot data : snapshot.getChildren()) {
                    String status = data.child("status").getValue(String.class);
                    if (status == null) status = "Confirmed";

                    String date = data.child("date").getValue(String.class);
                    if (date == null) date = "";

                    if (!status.equals(currentTabStatus)) continue;
                    if (!currentDateFilter.isEmpty() && !date.equals(currentDateFilter)) continue;

                    hasBookings = true;
                    addAdminBookingCard(data, status, date);
                }

                if (!hasBookings) {
                    TextView empty = new TextView(OwnerManageBookingActivity.this);
                    empty.setText("Tidak ada pesanan ditemukan.");
                    empty.setGravity(Gravity.CENTER);
                    empty.setPadding(0, 100, 0, 0);
                    empty.setTextColor(Color.parseColor("#808080"));
                    empty.setTypeface(poppinsMedium);
                    bookingContainerAdmin.addView(empty);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void addAdminBookingCard(DataSnapshot data, String status, String date) {
        String bookingId = data.getKey();
        String customerName = data.child("customerName").getValue(String.class);
        if (customerName == null || customerName.isEmpty()) customerName = "Pelanggan";

        String time = data.child("time").getValue(String.class);
        String paymentStatus = data.child("paymentStatus").getValue(String.class);
        if (paymentStatus == null) paymentStatus = "Menunggu Konfirmasi";

        String serviceType = data.child("serviceType").getValue(String.class);
        if (serviceType == null || serviceType.isEmpty()) serviceType = "Custom Nails";

        String paymentProof = data.child("paymentProof").getValue(String.class);
        String referenceImage = data.child("referenceImage").getValue(String.class);
        String metodePembayaran = (paymentProof != null && !paymentProof.isEmpty()) ? "Transfer" : "Cash";

        CardView card = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, 24);
        card.setLayoutParams(cardParams);
        card.setRadius(24);
        card.setCardElevation(4);
        card.setCardBackgroundColor(Color.WHITE);

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(24, 24, 24, 24);

        TextView tvName = new TextView(this);
        tvName.setText(customerName);
        tvName.setTextColor(Color.parseColor("#333333"));
        tvName.setTypeface(poppinsBold);
        tvName.setTextSize(18);
        mainLayout.addView(tvName);

        TextView tvInfo = new TextView(this);
        tvInfo.setText(serviceType + " • " + date + " (" + time + ")");
        tvInfo.setTextColor(Color.parseColor("#808080"));
        tvInfo.setTypeface(poppinsMedium);
        tvInfo.setTextSize(13);
        mainLayout.addView(tvInfo);

        LinearLayout statusRow = new LinearLayout(this);
        statusRow.setOrientation(LinearLayout.HORIZONTAL);
        statusRow.setPadding(0, 4, 0, 8);

        TextView tvStatusText = new TextView(this);
        tvStatusText.setText(status);
        tvStatusText.setTypeface(poppinsBold);
        tvStatusText.setTextSize(12);
        if (status.equalsIgnoreCase("Confirmed") || status.equalsIgnoreCase("Upcoming")) tvStatusText.setTextColor(Color.parseColor("#4A3B69"));
        else if (status.equalsIgnoreCase("Completed")) tvStatusText.setTextColor(Color.parseColor("#4CAF50"));
        else tvStatusText.setTextColor(Color.parseColor("#D6001C"));
        statusRow.addView(tvStatusText);

        TextView tvSep1 = new TextView(this); tvSep1.setText("  |  "); tvSep1.setTypeface(poppinsBold); tvSep1.setTextSize(12); tvSep1.setTextColor(Color.parseColor("#808080"));
        statusRow.addView(tvSep1);

        TextView tvPayText = new TextView(this);
        tvPayText.setText(paymentStatus);
        tvPayText.setTypeface(poppinsBold);
        tvPayText.setTextSize(12);
        if (paymentStatus.toLowerCase().contains("menunggu")) tvPayText.setTextColor(Color.parseColor("#F57F17"));
        else tvPayText.setTextColor(Color.parseColor("#4CAF50"));
        statusRow.addView(tvPayText);

        TextView tvSep2 = new TextView(this); tvSep2.setText("  |  "); tvSep2.setTypeface(poppinsBold); tvSep2.setTextSize(12); tvSep2.setTextColor(Color.parseColor("#808080"));
        statusRow.addView(tvSep2);

        TextView tvMethodText = new TextView(this);
        tvMethodText.setText(metodePembayaran);
        tvMethodText.setTypeface(poppinsBold);
        tvMethodText.setTextSize(12);
        tvMethodText.setTextColor(Color.parseColor("#4A3B69"));
        statusRow.addView(tvMethodText);

        mainLayout.addView(statusRow);

        TextView btnToggle = new TextView(this);
        btnToggle.setText("Tutup Detail ▲");
        btnToggle.setTextColor(getColor(R.color.lavender_dark));
        btnToggle.setTypeface(poppinsBold);
        btnToggle.setPadding(0, 16, 0, 0);
        mainLayout.addView(btnToggle);

        // KOTAK DETAIL (Langsung terbuka seperti di screenshot)
        LinearLayout detailLayout = new LinearLayout(this);
        detailLayout.setOrientation(LinearLayout.VERTICAL);
        detailLayout.setPadding(0, 16, 0, 0);

        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
        divider.setBackgroundColor(Color.parseColor("#EEEEEE"));
        detailLayout.addView(divider);

        // MENGAMBIL DATA KUKU LENGKAP
        String shape = data.child("shape").getValue(String.class);
        String length = data.child("length").getValue(String.class);
        String colorType = data.child("colorType").getValue(String.class);
        String hex = data.child("colorHex").getValue(String.class);
        String finish = data.child("finish").getValue(String.class);
        String sizeReport = data.child("sizeReport").getValue(String.class);
        String notes = data.child("notes").getValue(String.class);

        StringBuilder addonsStr = new StringBuilder();
        if (data.hasChild("addons")) {
            for (DataSnapshot addon : data.child("addons").getChildren()) {
                addonsStr.append("- ").append(addon.getValue(String.class)).append("<br>");
            }
        } else {
            addonsStr.append("- Tidak ada<br>");
        }

        // FORMAT HTML AGAR PERSIS SEPERTI SCREENSHOT REVIEW (Label Abu-abu, Isi Hitam Tebal)
        String htmlFormat =
                "<font color='#808080'>Nail Shape</font><br><b>" + (shape != null ? shape : "-") + "</b><br><br>" +
                        "<font color='#808080'>Nail Length</font><br><b>" + (length != null ? length : "-") + "</b><br><br>" +
                        "<font color='#808080'>Color Type & Hex</font><br><b>" + (colorType != null ? colorType : "-") + " (" + (hex != null ? hex : "") + ")</b><br><br>" +
                        "<font color='#808080'>Finishing Coat</font><br><b>" + (finish != null ? finish : "-") + "</b><br><br>" +
                        "<font color='#808080'>Selected Add-ons</font><br><b>" + addonsStr.toString() + "</b><br>" +
                        "<font color='#808080'>Nail Size Specification</font><br><b>" + (sizeReport != null ? sizeReport : "-") + "</b><br><br>" +
                        "<font color='#808080'>Special Custom Request Notes</font><br><b>" + (notes != null && !notes.isEmpty() ? notes : "-") + "</b><br>";

        TextView tvDetails = new TextView(this);
        tvDetails.setText(Html.fromHtml(htmlFormat));
        tvDetails.setTextColor(Color.parseColor("#333333"));
        tvDetails.setTextSize(14);
        tvDetails.setPadding(0, 16, 0, 16);
        detailLayout.addView(tvDetails);

        if (paymentProof != null && !paymentProof.isEmpty()) {
            TextView titleProof = new TextView(this); titleProof.setText("Bukti Transfer:"); titleProof.setTypeface(poppinsBold); titleProof.setTextColor(Color.parseColor("#333333"));
            detailLayout.addView(titleProof);
            ImageView ivProof = new ImageView(this);
            ivProof.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400));
            ivProof.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ivProof.setPadding(0, 8, 0, 16);
            try { ivProof.setImageURI(Uri.parse(paymentProof)); } catch (Exception e) {}
            ivProof.setOnClickListener(v -> showImagePopup(paymentProof, "Bukti Pembayaran"));
            detailLayout.addView(ivProof);
        }

        if (referenceImage != null && !referenceImage.isEmpty()) {
            TextView titleRef = new TextView(this); titleRef.setText("Referensi Kuku:"); titleRef.setTypeface(poppinsBold); titleRef.setTextColor(Color.parseColor("#333333"));
            detailLayout.addView(titleRef);
            ImageView ivRef = new ImageView(this);
            ivRef.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400));
            ivRef.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ivRef.setPadding(0, 8, 0, 16);
            try { ivRef.setImageURI(Uri.parse(referenceImage)); } catch (Exception e) {}
            ivRef.setOnClickListener(v -> showImagePopup(referenceImage, "Gambar Referensi Kuku"));
            detailLayout.addView(ivRef);
        }

        LinearLayout buttonContainer = new LinearLayout(this);
        buttonContainer.setOrientation(LinearLayout.VERTICAL);
        buttonContainer.setPadding(0, 8, 0, 0);

        if ("Menunggu Konfirmasi".equals(paymentStatus)) {
            TextView btnTerima = new TextView(this);
            btnTerima.setText("Terima Pembayaran");
            btnTerima.setGravity(Gravity.CENTER);
            btnTerima.setTextColor(Color.WHITE);
            btnTerima.setTypeface(poppinsBold);
            btnTerima.setTextSize(14);
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 120);
            btnParams.setMargins(0, 0, 0, 16);
            btnTerima.setLayoutParams(btnParams);
            GradientDrawable bgTerima = new GradientDrawable();
            bgTerima.setColor(Color.parseColor("#F57F17"));
            bgTerima.setCornerRadius(24);
            btnTerima.setBackground(bgTerima);

            btnTerima.setOnClickListener(v -> {
                bookingsRef.child(bookingId).child("paymentStatus").setValue("Lunas");
                Toast.makeText(this, "Pembayaran Lunas!", Toast.LENGTH_SHORT).show();
            });
            buttonContainer.addView(btnTerima);
        }

        if ("Confirmed".equals(status)) {
            LinearLayout actionRow = new LinearLayout(this);
            actionRow.setOrientation(LinearLayout.HORIZONTAL);
            actionRow.setWeightSum(2);

            TextView btnComplete = new TextView(this);
            btnComplete.setText("Selesaikan");
            btnComplete.setGravity(Gravity.CENTER);
            btnComplete.setTextColor(getColor(R.color.lavender_dark));
            btnComplete.setTypeface(poppinsBold);
            btnComplete.setTextSize(14);
            LinearLayout.LayoutParams completeParams = new LinearLayout.LayoutParams(0, 120, 1);
            completeParams.setMargins(0, 0, 8, 0);
            btnComplete.setLayoutParams(completeParams);
            GradientDrawable bgComplete = new GradientDrawable();
            bgComplete.setColor(Color.parseColor("#EEF0FA"));
            bgComplete.setCornerRadius(24);
            btnComplete.setBackground(bgComplete);

            // LOGIKA PENAMBAHAN POIN SAAT SELESAIKAN ORDER DITEKAN
            btnComplete.setOnClickListener(v -> {
                updateBookingStatus(bookingId, "Completed");
                bookingsRef.child(bookingId).child("paymentStatus").setValue("Lunas");

                // 1. Tambah poin di penyimpanan lokal (agar profil langsung update)
                SharedPreferences session = getSharedPreferences("UserSession", MODE_PRIVATE);
                int currentPoints = session.getInt("USER_POINTS", 0);
                session.edit().putInt("USER_POINTS", currentPoints + 50).apply();

                // 2. Tambah poin di Firebase (jika sistem Anda punya tabel users)
                DatabaseReference userRef = FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users").child(customerName);
                userRef.child("points").get().addOnSuccessListener(snap -> {
                    long pts = 0;
                    if (snap.exists() && snap.getValue() != null) {
                        pts = (long) snap.getValue();
                    }
                    userRef.child("points").setValue(pts + 50);
                });

                Toast.makeText(OwnerManageBookingActivity.this, "Selesai! +50 Poin dikirim ke Pelanggan", Toast.LENGTH_LONG).show();
            });

            TextView btnCancel = new TextView(this);
            btnCancel.setText("Batalkan");
            btnCancel.setGravity(Gravity.CENTER);
            btnCancel.setTextColor(Color.parseColor("#D6001C"));
            btnCancel.setTypeface(poppinsBold);
            btnCancel.setTextSize(14);
            LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(0, 120, 1);
            cancelParams.setMargins(8, 0, 0, 0);
            btnCancel.setLayoutParams(cancelParams);
            GradientDrawable bgCancel = new GradientDrawable();
            bgCancel.setColor(Color.parseColor("#FCE8E6"));
            bgCancel.setCornerRadius(24);
            btnCancel.setBackground(bgCancel);
            btnCancel.setOnClickListener(v -> updateBookingStatus(bookingId, "Canceled"));

            actionRow.addView(btnComplete);
            actionRow.addView(btnCancel);
            buttonContainer.addView(actionRow);
        }

        detailLayout.addView(buttonContainer);

        btnToggle.setOnClickListener(v -> {
            if (detailLayout.getVisibility() == View.GONE) {
                detailLayout.setVisibility(View.VISIBLE);
                btnToggle.setText("Tutup Detail ▲");
            } else {
                detailLayout.setVisibility(View.GONE);
                btnToggle.setText("Lihat Detail ▼");
            }
        });

        mainLayout.addView(detailLayout);
        card.addView(mainLayout);
        bookingContainerAdmin.addView(card);
    }

    private void showImagePopup(String uri, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ImageView iv = new ImageView(this);
        iv.setPadding(16, 16, 16, 16);
        iv.setAdjustViewBounds(true);
        try { iv.setImageURI(Uri.parse(uri)); } catch (Exception e) {}
        builder.setTitle(title).setView(iv).setPositiveButton("Tutup", null).show();
    }

    private void updateBookingStatus(String bookingId, String newStatus) {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Ubah status pesanan menjadi " + newStatus + "?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    bookingsRef.child(bookingId).child("status").setValue(newStatus)
                            .addOnSuccessListener(aVoid -> Toast.makeText(OwnerManageBookingActivity.this, "Status diubah!", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Tidak", null).show();
    }
}