package com.example.nailzbynutz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.NumberFormat;
import java.util.*;

public class PaymentActivity extends AppCompatActivity {

    private Uri proofUri = null; // Diubah menjadi tipe Uri untuk di-upload
    private TextView tvProofStatus;
    private ActivityResultLauncher<String> proofPickerLauncher;
    private ProgressDialog progressDialog;

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
        tvProofStatus = findViewById(R.id.tv_proof_status);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memproses pesanan & Mengunggah gambar...");
        progressDialog.setCancelable(false);

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        formatRupiah.setMaximumFractionDigits(0);

        tvServiceName.setText(serviceType != null ? serviceType : "Custom Nails");
        tvServicePrice.setText(formatRupiah.format(basePrice));
        tvAddonPrice.setText(formatRupiah.format(addonPrice));
        tvGrandTotal.setText(formatRupiah.format(grandTotal));

        proofPickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                proofUri = uri; // Simpan Uri asli untuk Firebase Storage
                tvProofStatus.setText("Gambar dipilih ✅");
                tvProofStatus.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.btn_upload_proof).setOnClickListener(v -> proofPickerLauncher.launch("image/*"));

        rgPayment.setOnCheckedChangeListener((group, checkedId) -> {
            layoutTransferInfo.setVisibility(checkedId == R.id.rb_cash ? View.GONE : View.VISIBLE);
            btnPay.setText(checkedId == R.id.rb_cash ? "Selesaikan Booking" : "Upload Bukti & Konfirmasi");
        });

        btnPay.setOnClickListener(v -> {
            String payStatus = (rgPayment.getCheckedRadioButtonId() == R.id.rb_cash) ? "Menunggu Pembayaran" : "Menunggu Konfirmasi";

            if (payStatus.equals("Menunggu Konfirmasi") && proofUri == null) {
                Toast.makeText(this, "Upload bukti transfer dulu!", Toast.LENGTH_SHORT).show();
                return;
            }

            progressDialog.show(); // Tampilkan loading

            if (proofUri != null) {
                // JIKA ADA BUKTI TRANSFER, UPLOAD KE STORAGE DULU
                uploadImageToStorage(intent, serviceType, payStatus);
            } else {
                // JIKA CASH, LANGSUNG SIMPAN KE DATABASE
                saveToFirebaseAndFinish(intent, serviceType, payStatus, null);
            }
        });
    }

    // FUNGSI MENGEMBALIKAN UPLOAD KE FIREBASE STORAGE
    private void uploadImageToStorage(Intent intent, String serviceType, String payStatus) {
        String fileName = "proof_" + System.currentTimeMillis() + ".jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("payment_proofs").child(fileName);

        storageRef.putFile(proofUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Jika sukses upload, ambil URL permanennya
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        saveToFirebaseAndFinish(intent, serviceType, payStatus, downloadUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Gagal mengunggah bukti: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void saveToFirebaseAndFinish(Intent intent, String serviceType, String payStatus, String uploadedProofUrl) {
        String date = intent.getStringExtra("BOOKING_DATE");
        String time = intent.getStringExtra("BOOKING_TIME");
        if (time == null) time = "09.00";
        if (date == null) date = "01/01/2026";

        String timeRange;
        try {
            String[] parts = time.split("\\.");
            int h = Integer.parseInt(parts[0]);
            timeRange = String.format("%02d.%02d - %02d.%02d", h, Integer.parseInt(parts[1]), h + 1, Integer.parseInt(parts[1]) + 30);
        } catch (Exception e) { timeRange = time; }

        HashMap<String, Object> data = new HashMap<>();
        data.put("customerName", getSharedPreferences("UserSession", MODE_PRIVATE).getString("USER_NAME", "Pelanggan"));
        data.put("date", date);
        data.put("time", timeRange);
        data.put("status", "Confirmed");
        data.put("paymentStatus", payStatus);
        data.put("serviceType", serviceType);
        data.put("grandTotal", intent.getIntExtra("GRAND_TOTAL", 0));

        data.put("shape", intent.getStringExtra("SHAPE_DATA") != null ? intent.getStringExtra("SHAPE_DATA") : "-");
        data.put("length", intent.getStringExtra("LENGTH_DATA") != null ? intent.getStringExtra("LENGTH_DATA") : "-");
        data.put("colorType", intent.getStringExtra("COLOR_TYPE_DATA") != null ? intent.getStringExtra("COLOR_TYPE_DATA") : "-");
        data.put("colorHex", intent.getStringExtra("COLOR_HEX_DATA") != null ? intent.getStringExtra("COLOR_HEX_DATA") : "0");
        data.put("finish", intent.getStringExtra("FINISH_DATA") != null ? intent.getStringExtra("FINISH_DATA") : "-");
        data.put("sizeReport", intent.getStringExtra("SIZE_DATA") != null ? intent.getStringExtra("SIZE_DATA") : "-");
        data.put("notes", intent.getStringExtra("NOTES_DATA") != null ? intent.getStringExtra("NOTES_DATA") : "-");

        ArrayList<String> addons = intent.getStringArrayListExtra("ADDONS_DATA");
        if (addons != null && !addons.isEmpty()) {
            data.put("addons", addons);
        } else {
            data.put("addons", new ArrayList<String>());
        }

        String uploadedRefImage = intent.getStringExtra("UPLOADED_IMAGE_DATA");
        if (uploadedRefImage != null) data.put("referenceImage", uploadedRefImage);

        // Simpan Link URL gambar yang sudah di-upload tadi
        if (uploadedProofUrl != null) data.put("paymentProof", uploadedProofUrl);

        FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("bookings")
                .push().setValue(data).addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Pesanan Berhasil!", Toast.LENGTH_LONG).show();
                    Intent home = new Intent(this, MainNavigationActivity.class);
                    home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(home);
                    finish();
                });
    }
}