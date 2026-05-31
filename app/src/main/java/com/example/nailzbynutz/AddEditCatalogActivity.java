package com.example.nailzbynutz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class AddEditCatalogActivity extends AppCompatActivity {

    private String catalogId = null;
    private String imageUriString = "";
    private ImageView ivPreview;
    private LinearLayout layoutUploadHint;
    private EditText etName, etPrice;

    private ActivityResultLauncher<String> imagePickerLauncher;
    private DatabaseReference catalogRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_catalog);

        catalogRef = FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("catalogs");

        findViewById(R.id.btn_back_add_edit).setOnClickListener(v -> finish());
        TextView tvTitle = findViewById(R.id.tv_title_add_edit);
        CardView cardUpload = findViewById(R.id.card_upload_image);
        ivPreview = findViewById(R.id.iv_catalog_preview);
        layoutUploadHint = findViewById(R.id.layout_upload_hint);
        etName = findViewById(R.id.et_catalog_name);
        etPrice = findViewById(R.id.et_catalog_price);
        Button btnSave = findViewById(R.id.btn_save_catalog);

        Intent intent = getIntent();
        if (intent.hasExtra("CATALOG_ID")) {
            catalogId = intent.getStringExtra("CATALOG_ID");
            tvTitle.setText("Edit Katalog");
            etName.setText(intent.getStringExtra("CATALOG_NAME"));
            etPrice.setText(intent.getStringExtra("CATALOG_PRICE"));

            imageUriString = intent.getStringExtra("CATALOG_IMAGE");
            if (imageUriString != null && !imageUriString.isEmpty()) {
                layoutUploadHint.setVisibility(View.GONE);
                try {
                    if (imageUriString.startsWith("res_")) {
                        int resId = Integer.parseInt(imageUriString.replace("res_", ""));
                        ivPreview.setImageResource(resId);
                    } else {
                        ivPreview.setImageURI(Uri.parse(imageUriString));
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
        }

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                imageUriString = uri.toString();
                ivPreview.setImageURI(uri);
                layoutUploadHint.setVisibility(View.GONE);
                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        });

        cardUpload.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String price = etPrice.getText().toString().trim();

            if (name.isEmpty() || price.isEmpty() || imageUriString.isEmpty()) {
                Toast.makeText(this, "Harap isi Nama, Harga, dan Foto!", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, Object> data = new HashMap<>();
            data.put("name", name);
            data.put("price", price);
            data.put("imageUrl", imageUriString);

            if (catalogId == null) {
                catalogRef.push().setValue(data);
                Toast.makeText(this, "Katalog baru berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
            } else if (catalogId.startsWith("local_")) {
                catalogRef.push().setValue(data);
                // Menandai gambar bawaan sebagai "dihapus" agar tidak muncul dobel
                getSharedPreferences("LocalCatalogPrefs", MODE_PRIVATE).edit().putBoolean("deleted_" + catalogId, true).apply();
                Toast.makeText(this, "Katalog berhasil diperbarui!", Toast.LENGTH_SHORT).show();
            } else {
                catalogRef.child(catalogId).updateChildren(data);
                Toast.makeText(this, "Katalog berhasil diperbarui!", Toast.LENGTH_SHORT).show();
            }
            finish();
        });
    }
}