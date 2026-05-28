package com.example.nailzbynutz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import java.util.ArrayList;

public class CustomNailReviewActivity extends AppCompatActivity {

    private TextView tvShape, tvLength, tvColor, tvFinish, tvAddons, tvSize, tvNotes;
    private View viewColorPreview;
    private AppCompatButton btnToBooking;
    private TextView tvImageNote;

    private String shape, length, colorType, colorHex, finish, sizeReport, notes, uploadedImage;
    private ArrayList<String> addonsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_nail_review);

        // Get data from previous activity (CustomNailDetailsActivity)
        Intent incoming = getIntent();
        shape = incoming.getStringExtra("SHAPE_DATA");
        length = incoming.getStringExtra("LENGTH_DATA");
        colorType = incoming.getStringExtra("COLOR_TYPE_DATA");
        colorHex = incoming.getStringExtra("COLOR_HEX_DATA");
        finish = incoming.getStringExtra("FINISH_DATA");
        sizeReport = incoming.getStringExtra("SIZE_REPORT");
        notes = incoming.getStringExtra("SPECIAL_NOTES");
        uploadedImage = incoming.getStringExtra("UPLOADED_IMAGE");
        addonsList = incoming.getStringArrayListExtra("ADDONS_DATA");

        // Default values if null
        if (shape == null) shape = "Almond";
        if (length == null) length = "Medium";
        if (colorType == null) colorType = "Solid";
        if (colorHex == null) colorHex = "#D6001C";
        if (finish == null) finish = "Glossy";
        if (sizeReport == null) sizeReport = "M (16-12-13-12-10mm)";
        if (notes == null) notes = "";
        if (addonsList == null) addonsList = new ArrayList<>();

        // Initialize views
        tvShape = findViewById(R.id.tv_review_shape);
        tvLength = findViewById(R.id.tv_review_length);
        tvColor = findViewById(R.id.tv_review_color);
        tvFinish = findViewById(R.id.tv_review_finish);
        tvAddons = findViewById(R.id.tv_review_addons);
        tvSize = findViewById(R.id.tv_review_size);
        tvNotes = findViewById(R.id.tv_review_notes);
        viewColorPreview = findViewById(R.id.view_review_color_preview);
        btnToBooking = findViewById(R.id.btn_to_booking);
        tvImageNote = findViewById(R.id.tv_image_note);

        // Set values
        tvShape.setText(shape);
        tvLength.setText(length);
        tvFinish.setText(finish);
        tvSize.setText(sizeReport);
        tvNotes.setText(notes.isEmpty() ? "Tidak ada catatan tambahan" : notes);
        tvColor.setText(colorType + " (" + colorHex + ")");

        try {
            viewColorPreview.setBackgroundColor(Color.parseColor(colorHex));
        } catch (Exception e) {
            viewColorPreview.setBackgroundColor(getColor(R.color.lavender_dark));
        }

        // Add-ons
        if (!addonsList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String addon : addonsList) {
                sb.append(addon).append(", ");
            }
            String addonsText = sb.substring(0, sb.length() - 2);
            tvAddons.setText(addonsText);
        } else {
            tvAddons.setText("Tanpa aksesoris tambahan");
        }

        // Uploaded image note
        if (uploadedImage != null && !uploadedImage.isEmpty()) {
            tvImageNote.setVisibility(View.VISIBLE);
        }

        // Back button (from top bar)
        findViewById(R.id.btn_back_review).setOnClickListener(v -> finish());

        // Proceed to booking
        btnToBooking.setOnClickListener(v -> {
            Intent intent = new Intent(CustomNailReviewActivity.this, BookingAppointmentActivity.class);
            intent.putExtra("FINAL_SHAPE", shape);
            intent.putExtra("FINAL_LENGTH", length);
            intent.putExtra("FINAL_COLOR_TYPE", colorType);
            intent.putExtra("FINAL_COLOR", colorHex);
            intent.putExtra("FINAL_FINISH", finish);
            intent.putExtra("FINAL_SIZE", sizeReport);
            intent.putExtra("FINAL_NOTES", notes);
            intent.putExtra("FINAL_IMAGE", uploadedImage);
            intent.putStringArrayListExtra("FINAL_ADDONS", addonsList);
            startActivity(intent);
        });
    }
}