package com.example.nailzbynutz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {

    private String proofUri = null;
    private TextView tvProofStatus;
    private ActivityResultLauncher<String> proofPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        findViewById(R.id.btn_back_payment).setOnClickListener(v -> finish());
        Intent intent = getIntent();

        String serviceType = intent.getStringExtra("SERVICE_TYPE");
        int basePrice = intent.getIntExtra("BASE_PRICE", 0);
        int addonPrice = intent.getIntExtra("ADDON_PRICE", 0);
        int grandTotal = intent.getIntExtra("GRAND_TOTAL", 0);

        TextView tvServiceName = findViewById(R.id.tv_service_name);
        TextView tvServicePrice = findViewById(R.id.tv_service_price);
        TextView tvAddonPrice = findViewById(R.id.tv_addon_price);
        TextView tvGrandTotal = findViewById(R.id.tv_grand_total);
        RadioGroup rgPayment = findViewById(R.id.rg_payment_method);
        Button btnPay = findViewById(R.id.btn_proceed_booking);
        LinearLayout layoutTransferInfo = findViewById(R.id.layout_transfer_info);
        Button btnUploadProof = findViewById(R.id.btn_upload_proof);
        tvProofStatus = findViewById(R.id.tv_proof_status);

        // FORMAT RUPIAH TANPA ,00
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        formatRupiah.setMaximumFractionDigits(0);

        tvServiceName.setText(serviceType != null ? serviceType : "Custom Nails");
        tvServicePrice.setText(formatRupiah.format(basePrice));
        tvAddonPrice.setText(formatRupiah.format(addonPrice));
        tvGrandTotal.setText(formatRupiah.format(grandTotal));

        proofPickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                proofUri = uri.toString();
                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                tvProofStatus.setText("Bukti berhasil diunggah ✅");
                tvProofStatus.setVisibility(View.VISIBLE);
            }
        });

        btnUploadProof.setOnClickListener(v -> proofPickerLauncher.launch("image/*"));

        rgPayment.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_cash) {
                layoutTransferInfo.setVisibility(View.GONE);
                btnPay.setText("Selesaikan Booking");
            } else {
                layoutTransferInfo.setVisibility(View.VISIBLE);
                btnPay.setText("Upload Bukti & Konfirmasi");
            }
        });

        btnPay.setOnClickListener(v -> {
            int selectedId = rgPayment.getCheckedRadioButtonId();
            String paymentStatus;

            if (selectedId == R.id.rb_cash) {
                paymentStatus = "Menunggu Pembayaran";
            } else {
                if (proofUri == null) {
                    Toast.makeText(this, "Harap upload bukti pembayaran terlebih dahulu!", Toast.LENGTH_SHORT).show();
                    return;
                }
                paymentStatus = "Menunggu Konfirmasi";
            }
            saveToFirebaseAndFinish(intent, serviceType, paymentStatus);
        });
    }

    private void saveToFirebaseAndFinish(Intent intent, String serviceType, String payStatus) {
        String date = intent.getStringExtra("BOOKING_DATE");
        String time = intent.getStringExtra("BOOKING_TIME");
        if (date == null || time == null) {
            Toast.makeText(this, "Data jadwal hilang. Silakan ulangi booking.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] timeParts = time.split("\\.");
        int startHour = Integer.parseInt(timeParts[0]);
        int startMin = Integer.parseInt(timeParts[1]);
        int endHour = startHour + 1;
        int endMin = startMin + 30;
        if (endMin >= 60) { endHour++; endMin -= 60; }
        String timeRange = String.format("%02d.%02d - %02d.%02d", startHour, startMin, endHour, endMin);

        SharedPreferences session = getSharedPreferences("UserSession", MODE_PRIVATE);
        String customerName = session.getString("USER_NAME", "Pelanggan");

        HashMap<String, Object> firebaseData = new HashMap<>();
        firebaseData.put("customerName", customerName);
        firebaseData.put("date", date);
        firebaseData.put("time", timeRange);
        firebaseData.put("status", "Confirmed");
        firebaseData.put("paymentStatus", payStatus);
        firebaseData.put("serviceType", serviceType);

        firebaseData.put("shape", intent.getStringExtra("SHAPE_DATA"));
        firebaseData.put("length", intent.getStringExtra("LENGTH_DATA"));
        firebaseData.put("colorType", intent.getStringExtra("COLOR_TYPE_DATA"));
        firebaseData.put("colorHex", intent.getStringExtra("COLOR_HEX_DATA"));
        firebaseData.put("finish", intent.getStringExtra("FINISH_DATA"));
        firebaseData.put("sizeReport", intent.getStringExtra("SIZE_DATA"));
        firebaseData.put("notes", intent.getStringExtra("NOTES_DATA"));

        ArrayList<String> addons = intent.getStringArrayListExtra("ADDONS_DATA");
        firebaseData.put("addons", addons != null ? addons : new ArrayList<>());

        String referenceImage = intent.getStringExtra("UPLOADED_IMAGE_DATA");
        if(referenceImage != null) firebaseData.put("referenceImage", referenceImage);
        if(proofUri != null) firebaseData.put("paymentProof", proofUri);

        DatabaseReference ref = FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("bookings");

        ref.push().setValue(firebaseData).addOnSuccessListener(aVoid -> {
            try {
                SharedPreferences prefs = getSharedPreferences("BookingData", MODE_PRIVATE);
                JSONArray array = new JSONArray(prefs.getString("bookings_list", "[]"));

                JSONObject obj = new JSONObject();
                String[] dateParts = date.split("/");
                obj.put("date", dateParts[0]);
                String[] months = {"JAN","FEB","MAR","APR","MEI","JUN","JUL","AGU","SEP","OKT","NOV","DES"};
                obj.put("month", months[Integer.parseInt(dateParts[1]) - 1]);

                obj.put("title", "Pesanan: " + (serviceType != null ? serviceType : "Layanan"));
                obj.put("subtitle", intent.getStringExtra("SHAPE_DATA") + " • " + intent.getStringExtra("LENGTH_DATA"));
                obj.put("time", timeRange);
                obj.put("artist", "Nut (Top Artist)");
                obj.put("status", "Confirmed");
                obj.put("paymentStatus", payStatus);
                obj.put("colorHex", intent.getStringExtra("COLOR_HEX_DATA"));

                array.put(obj);
                prefs.edit().putString("bookings_list", array.toString()).apply();

                Toast.makeText(this, "Pesanan Berhasil! +50 Poin", Toast.LENGTH_LONG).show();
                Intent homeIntent = new Intent(this, MainNavigationActivity.class);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homeIntent);
                finish();
            } catch (Exception e) { e.printStackTrace(); }
        });
    }
}