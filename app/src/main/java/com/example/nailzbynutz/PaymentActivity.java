package com.example.nailzbynutz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.*;

public class PaymentActivity extends AppCompatActivity {

    private Uri proofUri = null;
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
        progressDialog.setMessage("Menyimpan pesanan Anda...");
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
                proofUri = uri;
                tvProofStatus.setText("Gambar dipilih ✅");
                tvProofStatus.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.btn_upload_proof).setOnClickListener(v -> proofPickerLauncher.launch("image/*"));

        rgPayment.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isCash = (checkedId == R.id.rb_cash);
            layoutTransferInfo.setVisibility(isCash ? View.GONE : View.VISIBLE);
            btnPay.setText(isCash ? "Selesaikan Booking" : "Upload Bukti & Konfirmasi");
        });

        btnPay.setOnClickListener(v -> {
            boolean isCash = (rgPayment.getCheckedRadioButtonId() == R.id.rb_cash);
            String payStatus = isCash ? "Menunggu Pembayaran" : "Menunggu Konfirmasi";

            if (!isCash && proofUri == null) {
                Toast.makeText(this, "Upload bukti transfer dulu!", Toast.LENGTH_SHORT).show();
                return;
            }

            progressDialog.show();
            String base64Proof = null;

            if (!isCash && proofUri != null) {
                base64Proof = encodeImageToBase64(proofUri);
            }

            saveToFirebaseAndFinish(intent, serviceType, payStatus, base64Proof);
        });
    }

    private String encodeImageToBase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveToFirebaseAndFinish(Intent intent, String serviceType, String payStatus, String base64ProofUrl) {
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

        HashMap<String, Object> dataUtama = new HashMap<>();
        dataUtama.put("customerName", getSharedPreferences("UserSession", MODE_PRIVATE).getString("USER_NAME", "Pelanggan"));
        dataUtama.put("date", date);
        dataUtama.put("time", timeRange);
        dataUtama.put("status", "Confirmed");
        dataUtama.put("paymentStatus", payStatus);
        dataUtama.put("serviceType", serviceType);
        dataUtama.put("grandTotal", intent.getIntExtra("GRAND_TOTAL", 0));
        dataUtama.put("shape", intent.getStringExtra("SHAPE_DATA") != null ? intent.getStringExtra("SHAPE_DATA") : "-");
        dataUtama.put("length", intent.getStringExtra("LENGTH_DATA") != null ? intent.getStringExtra("LENGTH_DATA") : "-");
        dataUtama.put("colorType", intent.getStringExtra("COLOR_TYPE_DATA") != null ? intent.getStringExtra("COLOR_TYPE_DATA") : "-");
        dataUtama.put("colorHex", intent.getStringExtra("COLOR_HEX_DATA") != null ? intent.getStringExtra("COLOR_HEX_DATA") : "0");
        dataUtama.put("colorName", intent.getStringExtra("COLOR_NAME_DATA") != null ? intent.getStringExtra("COLOR_NAME_DATA") : "");
        dataUtama.put("finish", intent.getStringExtra("FINISH_DATA") != null ? intent.getStringExtra("FINISH_DATA") : "-");
        dataUtama.put("sizeReport", intent.getStringExtra("SIZE_DATA") != null ? intent.getStringExtra("SIZE_DATA") : "-");
        dataUtama.put("notes", intent.getStringExtra("NOTES_DATA") != null ? intent.getStringExtra("NOTES_DATA") : "-");

        HashMap<String, Integer> addonCounts = (HashMap<String, Integer>) intent.getSerializableExtra("ADDON_COUNTS_DATA");
        ArrayList<String> addons = intent.getStringArrayListExtra("ADDONS_DATA");
        ArrayList<String> formattedAddons = new ArrayList<>();
        if (addons != null && !addons.isEmpty()) {
            for (String addon : addons) {
                int qty = (addonCounts != null && addonCounts.containsKey(addon)) ? addonCounts.get(addon) : 1;
                formattedAddons.add(addon + " (x" + qty + ")");
            }
            dataUtama.put("addons", formattedAddons);
        } else {
            dataUtama.put("addons", new ArrayList<String>());
        }

        String uploadedRefImage = intent.getStringExtra("UPLOADED_IMAGE_DATA");
        if (uploadedRefImage != null) dataUtama.put("referenceImage", uploadedRefImage);
        if (base64ProofUrl != null) dataUtama.put("paymentProof", base64ProofUrl);

        // STRUKTUR NESTED: Laci Kategori -> ID Rapi
        String folderService = (serviceType != null) ? serviceType.replace(" ", "_") : "Lainnya";
        String username = dataUtama.get("customerName").toString().replaceAll("\\s+", "");
        String waktuSekarang = new java.text.SimpleDateFormat("ddMMyy_HHmmss", Locale.getDefault()).format(new Date());
        String customOrderId = username + "_" + waktuSekarang;

        FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("bookings")
                .child(folderService)
                .child(customOrderId)
                .setValue(dataUtama)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Pesanan Berhasil!", Toast.LENGTH_LONG).show();
                    Intent home = new Intent(this, MainNavigationActivity.class);
                    home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(home);
                    finish();
                });
    }
}