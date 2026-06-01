package com.example.nailzbynutz;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.text.HtmlCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;

public class OwnerManageBookingActivity extends AppCompatActivity {

    private ViewGroup bookingContainerAdmin;
    private DatabaseReference bookingsRef;
    private Typeface poppinsBold, poppinsMedium;
    private TextView tabUpcoming, tabComplete, tabCancel, tvActiveFilter;
    private String currentTabStatus = "Confirmed";
    private String currentDateFilter = "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_manage_booking);

        try {
            poppinsBold = ResourcesCompat.getFont(this, R.font.poppins_bold);
            poppinsMedium = ResourcesCompat.getFont(this, R.font.poppins_medium);
        } catch (Exception e) {
            poppinsBold = Typeface.DEFAULT_BOLD;
            poppinsMedium = Typeface.DEFAULT;
        }

        try {
            View btnBack = findViewById(R.id.btn_back_manage);
            if (btnBack != null) btnBack.setOnClickListener(v -> finish());

            bookingContainerAdmin = findViewById(R.id.bookingContainerAdmin);
            tvActiveFilter = findViewById(R.id.tv_active_filter);
            tabUpcoming = findViewById(R.id.tab_upcoming);
            tabComplete = findViewById(R.id.tab_complete);
            tabCancel = findViewById(R.id.tab_cancel);
            View btnFilterDate = findViewById(R.id.btn_filter_date);

            if (tabUpcoming != null) tabUpcoming.setOnClickListener(v -> switchTab(tabUpcoming, "Confirmed"));
            if (tabComplete != null) tabComplete.setOnClickListener(v -> switchTab(tabComplete, "Completed"));
            if (tabCancel != null) tabCancel.setOnClickListener(v -> switchTab(tabCancel, "Canceled"));
            if (btnFilterDate != null) btnFilterDate.setOnClickListener(v -> showDatePicker());

            if (tvActiveFilter != null) {
                tvActiveFilter.setOnClickListener(v -> {
                    currentDateFilter = "";
                    tvActiveFilter.setVisibility(View.GONE);
                    loadAllBookingsFromFirebase();
                });
            }

            bookingsRef = FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("bookings");

            if (tabUpcoming != null) {
                switchTab(tabUpcoming, "Confirmed");
            } else {
                loadAllBookingsFromFirebase();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error in onCreate: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void switchTab(TextView activeTab, String targetStatus) {
        try {
            if (tabUpcoming != null) {
                tabUpcoming.setBackgroundResource(0);
                tabUpcoming.setBackgroundTintList(null);
                tabUpcoming.setTextColor(Color.parseColor("#A0B0C0"));
            }
            if (tabComplete != null) {
                tabComplete.setBackgroundResource(0);
                tabComplete.setBackgroundTintList(null);
                tabComplete.setTextColor(Color.parseColor("#A0B0C0"));
            }
            if (tabCancel != null) {
                tabCancel.setBackgroundResource(0);
                tabCancel.setBackgroundTintList(null);
                tabCancel.setTextColor(Color.parseColor("#A0B0C0"));
            }

            if (activeTab != null) {
                activeTab.setBackgroundResource(R.drawable.bg_card_rounded);
                activeTab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3F51B5")));
                activeTab.setTextColor(Color.WHITE);
            }

            currentTabStatus = targetStatus;
            loadAllBookingsFromFirebase();
        } catch (Exception e) {}
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            currentDateFilter = dayOfMonth + "/" + (month + 1) + "/" + year;
            if (tvActiveFilter != null) {
                tvActiveFilter.setText("📅 Filter: " + currentDateFilter + " (Klik untuk reset)");
                tvActiveFilter.setVisibility(View.VISIBLE);
            }
            loadAllBookingsFromFirebase();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void loadAllBookingsFromFirebase() {
        if (bookingContainerAdmin == null) return;

        bookingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    bookingContainerAdmin.removeAllViews();
                    boolean hasBookings = false;

                    // DOBEL LOOP
                    for (DataSnapshot categoryFolder : snapshot.getChildren()) {
                        String categoryKey = categoryFolder.getKey(); // NAMA LACI PENTING!

                        for (DataSnapshot data : categoryFolder.getChildren()) {
                            try {
                                String status = String.valueOf(data.child("status").getValue());
                                if (status.equals("null")) status = "Confirmed";

                                String date = String.valueOf(data.child("date").getValue());
                                if (date.equals("null")) date = "";

                                if (!status.equals(currentTabStatus)) continue;
                                if (!currentDateFilter.isEmpty() && !date.equals(currentDateFilter)) continue;

                                hasBookings = true;
                                // LEMPAR NAMA LACI KE METHOD PEMBUAT CARD
                                addAdminBookingCard(data, status, date, categoryKey);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
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
                } catch (Exception e) {
                    Toast.makeText(OwnerManageBookingActivity.this, "Error baca data", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadSafeImage(ImageView iv, String base64String) {
        if (base64String != null && !base64String.isEmpty() && !base64String.equals("null")) {
            try {
                byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                iv.setImageBitmap(decodedByte);
            } catch (Exception e) {
                iv.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            iv.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    // PERHATIKAN: Ada tambahan parameter 'categoryKey' di bawah ini
    private void addAdminBookingCard(DataSnapshot data, String status, String date, String categoryKey) {
        try {
            String bookingId = data.getKey();
            String customerName = String.valueOf(data.child("customerName").getValue());
            if (customerName.equals("null") || customerName.isEmpty()) customerName = "Pelanggan";

            String time = String.valueOf(data.child("time").getValue());
            if (time.equals("null")) time = "-";

            String paymentStatus = String.valueOf(data.child("paymentStatus").getValue());
            if (paymentStatus.equals("null")) paymentStatus = "Menunggu Konfirmasi";

            String serviceType = String.valueOf(data.child("serviceType").getValue());
            if (serviceType.equals("null") || serviceType.isEmpty()) serviceType = categoryKey.replace("_", " ");

            String paymentProof = data.hasChild("paymentProof") ? String.valueOf(data.child("paymentProof").getValue()) : "";
            String referenceImage = data.hasChild("referenceImage") ? String.valueOf(data.child("referenceImage").getValue()) : "";
            String metodePembayaran = (!paymentProof.isEmpty() && !paymentProof.equals("null")) ? "Transfer" : "Cash";

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
            if (status.equalsIgnoreCase("Confirmed") || status.equalsIgnoreCase("Upcoming")) tvStatusText.setTextColor(Color.parseColor("#3F51B5"));
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
            tvMethodText.setTextColor(Color.parseColor("#3F51B5"));
            statusRow.addView(tvMethodText);

            mainLayout.addView(statusRow);

            TextView btnToggle = new TextView(this);
            btnToggle.setText("Lihat Detail ▼");
            btnToggle.setTextColor(Color.parseColor("#3F51B5"));
            btnToggle.setTypeface(poppinsBold);
            btnToggle.setPadding(0, 16, 0, 0);
            mainLayout.addView(btnToggle);

            LinearLayout detailLayout = new LinearLayout(this);
            detailLayout.setOrientation(LinearLayout.VERTICAL);
            detailLayout.setPadding(0, 16, 0, 0);
            detailLayout.setVisibility(View.GONE);

            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
            divider.setBackgroundColor(Color.parseColor("#EEEEEE"));
            detailLayout.addView(divider);

            String shape = String.valueOf(data.child("shape").getValue());
            String length = String.valueOf(data.child("length").getValue());
            String colorType = String.valueOf(data.child("colorType").getValue());
            String hex = String.valueOf(data.child("colorHex").getValue());
            String colorName = String.valueOf(data.child("colorName").getValue());
            String finish = String.valueOf(data.child("finish").getValue());
            String sizeReport = String.valueOf(data.child("sizeReport").getValue());
            String notes = String.valueOf(data.child("notes").getValue());

            String colorDisplay = (!colorType.equals("null") ? colorType : "-");
            if (!colorName.equals("null") && !colorName.isEmpty()) colorDisplay += " - " + colorName;
            colorDisplay += " (" + (!hex.equals("null") ? hex : "") + ")";

            StringBuilder addonsStr = new StringBuilder();
            if (data.hasChild("addons")) {
                for (DataSnapshot addon : data.child("addons").getChildren()) {
                    addonsStr.append("- ").append(addon.getValue(String.class)).append("<br>");
                }
            } else {
                addonsStr.append("- Tidak ada<br>");
            }

            String htmlFormat =
                    "<font color='#808080'>Nail Shape</font><br><b>" + (!shape.equals("null") ? shape : "-") + "</b><br><br>" +
                            "<font color='#808080'>Nail Length</font><br><b>" + (!length.equals("null") ? length : "-") + "</b><br><br>" +
                            "<font color='#808080'>Color Type & Hex</font><br><b>" + colorDisplay + "</b><br><br>" +
                            "<font color='#808080'>Finishing Coat</font><br><b>" + (!finish.equals("null") ? finish : "-") + "</b><br><br>" +
                            "<font color='#808080'>Selected Add-ons</font><br><b>" + addonsStr.toString() + "</b><br>" +
                            "<font color='#808080'>Nail Size Specification</font><br><b>" + (!sizeReport.equals("null") ? sizeReport : "-") + "</b><br><br>" +
                            "<font color='#808080'>Special Custom Request Notes</font><br><b>" + (!notes.equals("null") && !notes.isEmpty() ? notes : "-") + "</b><br>";

            TextView tvDetails = new TextView(this);
            tvDetails.setText(HtmlCompat.fromHtml(htmlFormat, HtmlCompat.FROM_HTML_MODE_LEGACY));
            tvDetails.setTextColor(Color.parseColor("#333333"));
            tvDetails.setTextSize(14);
            tvDetails.setPadding(0, 16, 0, 16);
            detailLayout.addView(tvDetails);

            if (!paymentProof.isEmpty() && !paymentProof.equals("null")) {
                TextView titleProof = new TextView(this); titleProof.setText("Bukti Transfer:"); titleProof.setTypeface(poppinsBold); titleProof.setTextColor(Color.parseColor("#333333"));
                detailLayout.addView(titleProof);
                ImageView ivProof = new ImageView(this);
                ivProof.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400));
                ivProof.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ivProof.setPadding(0, 8, 0, 16);
                loadSafeImage(ivProof, paymentProof);
                ivProof.setOnClickListener(v -> showImagePopup(paymentProof, "Bukti Pembayaran"));
                detailLayout.addView(ivProof);
            }

            if (!referenceImage.isEmpty() && !referenceImage.equals("null")) {
                TextView titleRef = new TextView(this); titleRef.setText("Referensi Kuku:"); titleRef.setTypeface(poppinsBold); titleRef.setTextColor(Color.parseColor("#333333"));
                detailLayout.addView(titleRef);
                ImageView ivRef = new ImageView(this);
                ivRef.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400));
                ivRef.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ivRef.setPadding(0, 8, 0, 16);
                loadSafeImage(ivRef, referenceImage);
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
                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                btnParams.setMargins(0, 0, 0, 16);
                btnTerima.setLayoutParams(btnParams);
                btnTerima.setPadding(0, 32, 0, 32);
                btnTerima.setBackgroundResource(R.drawable.bg_button_rounded);

                btnTerima.setOnClickListener(v -> {
                    // PENTING: Update berdasarkan folder kategori
                    bookingsRef.child(categoryKey).child(bookingId).child("paymentStatus").setValue("Lunas");
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
                btnComplete.setTextColor(Color.WHITE);
                btnComplete.setTypeface(poppinsBold);
                btnComplete.setTextSize(14);
                LinearLayout.LayoutParams completeParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                completeParams.setMargins(0, 0, 8, 0);
                btnComplete.setLayoutParams(completeParams);
                btnComplete.setPadding(0, 32, 0, 32);
                btnComplete.setBackgroundResource(R.drawable.bg_button_rounded);

                String finalCustomerName = customerName;
                btnComplete.setOnClickListener(v -> {
                    // PENTING: Panggil method update dengan folder kategori
                    updateBookingStatus(bookingId, categoryKey, "Completed");
                    bookingsRef.child(categoryKey).child(bookingId).child("paymentStatus").setValue("Lunas");

                    SharedPreferences session = getSharedPreferences("UserSession", MODE_PRIVATE);
                    int currentPoints = session.getInt("USER_POINTS", 0);
                    session.edit().putInt("USER_POINTS", currentPoints + 50).apply();

                    DatabaseReference userRef = FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users").child(finalCustomerName);
                    userRef.child("points").get().addOnSuccessListener(snap -> {
                        long pts = 0;
                        if (snap.exists() && snap.getValue() != null) {
                            try { pts = Long.parseLong(String.valueOf(snap.getValue())); } catch (Exception ignored) {}
                        }
                        userRef.child("points").setValue(pts + 50);
                    });

                    Toast.makeText(OwnerManageBookingActivity.this, "Selesai! +50 Poin dikirim ke Pelanggan", Toast.LENGTH_LONG).show();
                });

                TextView btnCancel = new TextView(this);
                btnCancel.setText("Batalkan");
                btnCancel.setGravity(Gravity.CENTER);
                btnCancel.setTextColor(Color.WHITE);
                btnCancel.setTypeface(poppinsBold);
                btnCancel.setTextSize(14);
                LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                cancelParams.setMargins(8, 0, 0, 0);
                btnCancel.setLayoutParams(cancelParams);
                btnCancel.setPadding(0, 32, 0, 32);
                android.graphics.drawable.GradientDrawable bgCancel = new android.graphics.drawable.GradientDrawable();
                bgCancel.setColor(Color.parseColor("#E53935"));
                bgCancel.setCornerRadius(24);
                btnCancel.setBackground(bgCancel);

                btnCancel.setOnClickListener(v -> updateBookingStatus(bookingId, categoryKey, "Canceled"));

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

        } catch (Exception e) {
            Toast.makeText(this, "Gagal membuat card: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showImagePopup(String base64String, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ImageView iv = new ImageView(this);
        iv.setPadding(16, 16, 16, 16);
        iv.setAdjustViewBounds(true);
        loadSafeImage(iv, base64String);
        builder.setTitle(title).setView(iv).setPositiveButton("Tutup", null).show();
    }

    // PENTING: Method ini sekarang butuh categoryKey!
    private void updateBookingStatus(String bookingId, String categoryKey, String newStatus) {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Ubah status pesanan menjadi " + newStatus + "?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    bookingsRef.child(categoryKey).child(bookingId).child("status").setValue(newStatus)
                            .addOnSuccessListener(aVoid -> Toast.makeText(OwnerManageBookingActivity.this, "Status diubah!", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Tidak", null).show();
    }
}