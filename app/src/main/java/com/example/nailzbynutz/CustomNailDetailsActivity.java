package com.example.nailzbynutz;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class CustomNailDetailsActivity extends AppCompatActivity {

    // View components
    private ImageView btnBack;
    private Button btnNext;
    private Button btnSizeS, btnSizeM, btnSizeL;
    private Button btnMinusThumb, btnPlusThumb, btnMinusIndex, btnPlusIndex;
    private Button btnMinusMiddle, btnPlusMiddle, btnMinusRing, btnPlusRing, btnMinusPinky, btnPlusPinky;
    private TextView tvThumb, tvIndex, tvMiddle, tvRing, tvPinky;
    private TextView btnShowGuide;
    private EditText etSpecialNotes;

    // Size values (default M)
    private int thumbVal = 16, indexVal = 12, middleVal = 13, ringVal = 12, pinkyVal = 10;
    private String selectedPreset = "M";

    // Data from previous activities
    private String dataShape, dataLength, dataColorType, dataColorHex, dataFinish, uploadedImage;
    private ArrayList<String> addonsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_nail_details);

        // Get data from intent
        Intent incoming = getIntent();
        dataShape = incoming.getStringExtra("SHAPE_DATA");
        dataLength = incoming.getStringExtra("LENGTH_DATA");
        dataColorType = incoming.getStringExtra("COLOR_TYPE_DATA");
        dataColorHex = incoming.getStringExtra("COLOR_HEX_DATA");
        dataFinish = incoming.getStringExtra("FINISH_DATA");
        uploadedImage = incoming.getStringExtra("UPLOADED_IMAGE_DATA");
        if (incoming.getStringArrayListExtra("ADDONS_DATA") != null) {
            addonsList = incoming.getStringArrayListExtra("ADDONS_DATA");
        }

        // Init views
        btnBack = findViewById(R.id.btn_back_details);
        btnNext = findViewById(R.id.btn_next_details);
        btnSizeS = findViewById(R.id.btn_size_s);
        btnSizeM = findViewById(R.id.btn_size_m);
        btnSizeL = findViewById(R.id.btn_size_l);
        btnMinusThumb = findViewById(R.id.btn_minus_thumb);
        btnPlusThumb = findViewById(R.id.btn_plus_thumb);
        btnMinusIndex = findViewById(R.id.btn_minus_index);
        btnPlusIndex = findViewById(R.id.btn_plus_index);
        btnMinusMiddle = findViewById(R.id.btn_minus_middle);
        btnPlusMiddle = findViewById(R.id.btn_plus_middle);
        btnMinusRing = findViewById(R.id.btn_minus_ring);
        btnPlusRing = findViewById(R.id.btn_plus_ring);
        btnMinusPinky = findViewById(R.id.btn_minus_pinky);
        btnPlusPinky = findViewById(R.id.btn_plus_pinky);
        tvThumb = findViewById(R.id.tv_val_thumb);
        tvIndex = findViewById(R.id.tv_val_index);
        tvMiddle = findViewById(R.id.tv_val_middle);
        tvRing = findViewById(R.id.tv_val_ring);
        tvPinky = findViewById(R.id.tv_val_pinky);
        btnShowGuide = findViewById(R.id.btn_show_guide);
        etSpecialNotes = findViewById(R.id.et_special_notes);

        // Set default size M
        applyPresetSize("M", 16, 12, 13, 12, 10);

        // Preset size listeners
        btnSizeS.setOnClickListener(v -> applyPresetSize("S", 15, 11, 12, 11, 9));
        btnSizeM.setOnClickListener(v -> applyPresetSize("M", 16, 12, 13, 12, 10));
        btnSizeL.setOnClickListener(v -> applyPresetSize("L", 17, 13, 14, 13, 11));

        // +/- listeners
        setupCounter(btnMinusThumb, btnPlusThumb, tvThumb, "thumb");
        setupCounter(btnMinusIndex, btnPlusIndex, tvIndex, "index");
        setupCounter(btnMinusMiddle, btnPlusMiddle, tvMiddle, "middle");
        setupCounter(btnMinusRing, btnPlusRing, tvRing, "ring");
        setupCounter(btnMinusPinky, btnPlusPinky, tvPinky, "pinky");

        // Guide dialog
        btnShowGuide.setOnClickListener(v -> showSizeGuide());

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Next button
        btnNext.setOnClickListener(v -> {
            String sizeReport = selectedPreset + " (" + thumbVal + "-" + indexVal + "-" + middleVal + "-" + ringVal + "-" + pinkyVal + "mm)";
            String notes = etSpecialNotes.getText().toString().trim();
            Intent intent = new Intent(CustomNailDetailsActivity.this, CustomNailReviewActivity.class);
            intent.putExtra("SHAPE_DATA", dataShape);
            intent.putExtra("LENGTH_DATA", dataLength);
            intent.putExtra("COLOR_TYPE_DATA", dataColorType);
            intent.putExtra("COLOR_HEX_DATA", dataColorHex);
            intent.putExtra("FINISH_DATA", dataFinish);
            intent.putExtra("SIZE_REPORT", sizeReport);
            intent.putExtra("SPECIAL_NOTES", notes);
            intent.putExtra("UPLOADED_IMAGE", uploadedImage);
            intent.putStringArrayListExtra("ADDONS_DATA", addonsList);
            startActivity(intent);
        });
    }

    private void setupCounter(Button minus, Button plus, TextView tv, String finger) {
        minus.setOnClickListener(v -> changeValue(finger, -1, tv));
        plus.setOnClickListener(v -> changeValue(finger, 1, tv));
    }

    private void changeValue(String finger, int delta, TextView tv) {
        selectedPreset = "Custom";
        resetPresetButtons();
        int newVal = Integer.parseInt(tv.getText().toString()) + delta;
        if (newVal < 5) newVal = 5;
        tv.setText(String.valueOf(newVal));
        updateFingerValue(finger, newVal);
    }

    private void updateFingerValue(String finger, int val) {
        switch (finger) {
            case "thumb": thumbVal = val; break;
            case "index": indexVal = val; break;
            case "middle": middleVal = val; break;
            case "ring": ringVal = val; break;
            case "pinky": pinkyVal = val; break;
        }
    }

    private void applyPresetSize(String label, int t, int i, int m, int r, int p) {
        selectedPreset = label;
        thumbVal = t; indexVal = i; middleVal = m; ringVal = r; pinkyVal = p;
        tvThumb.setText(String.valueOf(thumbVal));
        tvIndex.setText(String.valueOf(indexVal));
        tvMiddle.setText(String.valueOf(middleVal));
        tvRing.setText(String.valueOf(ringVal));
        tvPinky.setText(String.valueOf(pinkyVal));
        resetPresetButtons();
        if (label.equals("S")) setActivePresetButton(btnSizeS);
        else if (label.equals("M")) setActivePresetButton(btnSizeM);
        else if (label.equals("L")) setActivePresetButton(btnSizeL);
    }

    private void resetPresetButtons() {
        setPresetButtonStyle(btnSizeS, false);
        setPresetButtonStyle(btnSizeM, false);
        setPresetButtonStyle(btnSizeL, false);
    }

    private void setActivePresetButton(Button btn) {
        setPresetButtonStyle(btn, true);
    }

    private void setPresetButtonStyle(Button btn, boolean active) {
        if (btn == null) return;
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setCornerRadius(20f);
        if (active) {
            gd.setColor(getColor(R.color.lavender_dark));
            btn.setTextColor(getColor(R.color.white));
            btn.setWidth(0);
        } else {
            gd.setColor(getColor(R.color.background_card));
            btn.setTextColor(getColor(R.color.text_primary));
            btn.setWidth(1);
            btn.setTextColor(getColor(R.color.divider));
        }
        btn.setBackground(gd);
    }

    private void showSizeGuide() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.img_measure_guide);
        imageView.setAdjustViewBounds(true);
        dialog.setContentView(imageView);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        imageView.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}