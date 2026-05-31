package com.example.nailzbynutz;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class CustomNailReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_nail_review);

        findViewById(R.id.btn_back_review).setOnClickListener(v -> finish());
        Button btnToBooking = findViewById(R.id.btn_to_booking);

        Intent intent = getIntent();
        String shape = intent.getStringExtra("SHAPE_DATA");
        String length = intent.getStringExtra("LENGTH_DATA");
        String colorType = intent.getStringExtra("COLOR_TYPE_DATA");
        String colorHex = intent.getStringExtra("COLOR_HEX_DATA");
        String colorName = intent.getStringExtra("COLOR_NAME_DATA");
        String finishCoat = intent.getStringExtra("FINISH_DATA");
        String size = intent.getStringExtra("SIZE_DATA");
        String notes = intent.getStringExtra("NOTES_DATA");
        String uploadedImage = intent.getStringExtra("UPLOADED_IMAGE_DATA");
        ArrayList<String> addons = intent.getStringArrayListExtra("ADDONS_DATA");

        String serviceType = intent.getStringExtra("SERVICE_TYPE");
        if (serviceType == null) serviceType = "Custom Nails";
        HashMap<String, Integer> addonCounts = (HashMap<String, Integer>) intent.getSerializableExtra("ADDON_COUNTS_DATA");

        TextView tvShape = findViewById(R.id.tv_review_shape);
        TextView tvLength = findViewById(R.id.tv_review_length);
        TextView tvColor = findViewById(R.id.tv_review_color);
        View colorPreview = findViewById(R.id.view_review_color_preview);
        TextView tvFinish = findViewById(R.id.tv_review_finish);
        TextView tvAddons = findViewById(R.id.tv_review_addons);
        TextView tvSize = findViewById(R.id.tv_review_size);
        TextView tvNotes = findViewById(R.id.tv_review_notes);
        ImageView ivReviewImage = findViewById(R.id.iv_review_image);
        TextView tvImageTitle = findViewById(R.id.tv_image_title);
        TextView tvGrandTotal = findViewById(R.id.tv_review_grand_total);

        // KALKULASI HARGA
        int basePrice = serviceType.equals("Gel Nails") ? 60000 : (serviceType.equals("Manicure") ? 45000 : 80000);
        int addonPrice = 0;

        if (addons != null && !addons.isEmpty()) {
            StringBuilder addonsText = new StringBuilder();
            for (String addon : addons) {
                int qty = (addonCounts != null && addonCounts.containsKey(addon)) ? addonCounts.get(addon) : 1;
                addonsText.append("- ").append(addon).append(" (x").append(qty).append(")\n");
                if (qty > 0) addonPrice += addon.equals("Stickers") ? (1000 * qty) : (2000 * qty);
            }
            tvAddons.setText(addonsText.toString().trim());
        } else {
            tvAddons.setText("Tidak ada Add-on");
        }

        int grandTotal = basePrice + addonPrice;

        tvShape.setText(shape != null ? shape : "-");
        tvLength.setText(length != null ? length : "-");

        if (colorHex != null && colorName != null) {
            tvColor.setText(colorType + " - " + colorName + " (" + colorHex + ")");
        } else if (colorHex != null) {
            tvColor.setText(colorType + " (" + colorHex + ")");
        } else {
            tvColor.setText("-");
        }

        tvFinish.setText(finishCoat != null ? finishCoat : "-");
        tvSize.setText(size != null ? size : "-");
        tvNotes.setText((notes != null && !notes.isEmpty()) ? notes : "Tidak ada catatan khusus");

        if (colorHex != null) {
            GradientDrawable gd = new GradientDrawable();
            gd.setShape(GradientDrawable.OVAL);
            gd.setColor(Color.parseColor(colorHex));
            colorPreview.setBackground(gd);
        }

        // Tampilkan Gambar
        if (uploadedImage != null && !uploadedImage.isEmpty()) {
            tvImageTitle.setVisibility(View.VISIBLE);
            ivReviewImage.setVisibility(View.VISIBLE);
            ivReviewImage.setImageURI(Uri.parse(uploadedImage));
        }

        Locale localeID = new Locale("in", "ID");
        tvGrandTotal.setText(NumberFormat.getCurrencyInstance(localeID).format(grandTotal));

        int finalAddonPrice = addonPrice;
        btnToBooking.setOnClickListener(v -> {
            Intent nextIntent = new Intent(CustomNailReviewActivity.this, BookingAppointmentActivity.class);
            if (getIntent().getExtras() != null) {
                nextIntent.putExtras(getIntent().getExtras());
            }
            nextIntent.putExtra("BASE_PRICE", basePrice);
            nextIntent.putExtra("ADDON_PRICE", finalAddonPrice);
            nextIntent.putExtra("GRAND_TOTAL", grandTotal);
            startActivity(nextIntent);
        });
    }
}