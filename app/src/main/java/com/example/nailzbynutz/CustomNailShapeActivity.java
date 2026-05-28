package com.example.nailzbynutz;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class CustomNailShapeActivity extends AppCompatActivity {

    // Deklarasi view
    private ImageView btnBack;
    private LinearLayout btnAlmond, btnCoffin, btnSquare, btnOval, btnStiletto, btnRound, btnSquoval, btnLipstick;
    private TextView tvAlmond, tvCoffin, tvSquare, tvOval, tvStiletto, tvRound, tvSquoval, tvLipstick;
    private AppCompatButton btnNext, btnShort, btnMedium, btnLong;

    // Data yang dipilih user
    private String selectedShape = "Almond";
    private String selectedLength = "Medium";

    // Warna tema
    private final String COLOR_ACTIVE_BG = "#7E8DBB";   // lavender_dark
    private final String COLOR_ACTIVE_TEXT = "#FFFFFF";
    private final String COLOR_INACTIVE_BG = "#FFFFFF";
    private final String COLOR_INACTIVE_TEXT = "#2D2A32";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_nail_shape);

        // Inisialisasi view
        btnBack = findViewById(R.id.btn_back);
        btnAlmond = findViewById(R.id.btn_shape_almond);
        btnCoffin = findViewById(R.id.btn_shape_coffin);
        btnSquare = findViewById(R.id.btn_shape_square);
        btnOval = findViewById(R.id.btn_shape_oval);
        btnStiletto = findViewById(R.id.btn_shape_stiletto);
        btnRound = findViewById(R.id.btn_shape_round);
        btnSquoval = findViewById(R.id.btn_shape_squoval);
        btnLipstick = findViewById(R.id.btn_shape_lipstick);
        tvAlmond = findViewById(R.id.tv_almond);
        tvCoffin = findViewById(R.id.tv_coffin);
        tvSquare = findViewById(R.id.tv_square);
        tvOval = findViewById(R.id.tv_oval);
        tvStiletto = findViewById(R.id.tv_stiletto);
        tvRound = findViewById(R.id.tv_round);
        tvSquoval = findViewById(R.id.tv_squoval);
        tvLipstick = findViewById(R.id.tv_lipstick);
        btnShort = findViewById(R.id.btn_len_short);
        btnMedium = findViewById(R.id.btn_len_medium);
        btnLong = findViewById(R.id.btn_len_long);
        btnNext = findViewById(R.id.btn_next_step);

        // Tombol back: kembali ke activity sebelumnya
        btnBack.setOnClickListener(v -> finish());

        // Listener untuk shape
        setShapeClickListener(btnAlmond, tvAlmond, "Almond");
        setShapeClickListener(btnCoffin, tvCoffin, "Coffin");
        setShapeClickListener(btnSquare, tvSquare, "Square");
        setShapeClickListener(btnOval, tvOval, "Oval");
        setShapeClickListener(btnStiletto, tvStiletto, "Stiletto");
        setShapeClickListener(btnRound, tvRound, "Round");
        setShapeClickListener(btnSquoval, tvSquoval, "Squoval");
        setShapeClickListener(btnLipstick, tvLipstick, "Lipstick");

        // Listener untuk length
        btnShort.setOnClickListener(v -> {
            selectedLength = "Short";
            updateLengthUI();
        });
        btnMedium.setOnClickListener(v -> {
            selectedLength = "Medium";
            updateLengthUI();
        });
        btnLong.setOnClickListener(v -> {
            selectedLength = "Long";
            updateLengthUI();
        });

        // Set tampilan awal
        updateShapeUI(selectedShape);
        updateLengthUI();

        // Tombol next: pindah ke CustomNailColorActivity
        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(CustomNailShapeActivity.this, CustomNailColorActivity.class);
            intent.putExtra("SHAPE_DATA", selectedShape);
            intent.putExtra("LENGTH_DATA", selectedLength);
            startActivity(intent);
        });
    }

    // Helper: set listener untuk shape
    private void setShapeClickListener(LinearLayout layout, TextView textView, String shapeName) {
        layout.setOnClickListener(v -> {
            selectedShape = shapeName;
            updateShapeUI(shapeName);
            Toast.makeText(this, "Selected: " + shapeName, Toast.LENGTH_SHORT).show();
        });
    }

    // Update UI shape: ubah background dan teks
    private void updateShapeUI(String activeShape) {
        // Reset semua ke inactive
        applyShapeStyle(btnAlmond, tvAlmond, COLOR_INACTIVE_BG, COLOR_INACTIVE_TEXT);
        applyShapeStyle(btnCoffin, tvCoffin, COLOR_INACTIVE_BG, COLOR_INACTIVE_TEXT);
        applyShapeStyle(btnSquare, tvSquare, COLOR_INACTIVE_BG, COLOR_INACTIVE_TEXT);
        applyShapeStyle(btnOval, tvOval, COLOR_INACTIVE_BG, COLOR_INACTIVE_TEXT);
        applyShapeStyle(btnStiletto, tvStiletto, COLOR_INACTIVE_BG, COLOR_INACTIVE_TEXT);
        applyShapeStyle(btnRound, tvRound, COLOR_INACTIVE_BG, COLOR_INACTIVE_TEXT);
        applyShapeStyle(btnSquoval, tvSquoval, COLOR_INACTIVE_BG, COLOR_INACTIVE_TEXT);
        applyShapeStyle(btnLipstick, tvLipstick, COLOR_INACTIVE_BG, COLOR_INACTIVE_TEXT);

        // Aktifkan yang dipilih
        switch (activeShape) {
            case "Almond": applyShapeStyle(btnAlmond, tvAlmond, COLOR_ACTIVE_BG, COLOR_ACTIVE_TEXT); break;
            case "Coffin": applyShapeStyle(btnCoffin, tvCoffin, COLOR_ACTIVE_BG, COLOR_ACTIVE_TEXT); break;
            case "Square": applyShapeStyle(btnSquare, tvSquare, COLOR_ACTIVE_BG, COLOR_ACTIVE_TEXT); break;
            case "Oval": applyShapeStyle(btnOval, tvOval, COLOR_ACTIVE_BG, COLOR_ACTIVE_TEXT); break;
            case "Stiletto": applyShapeStyle(btnStiletto, tvStiletto, COLOR_ACTIVE_BG, COLOR_ACTIVE_TEXT); break;
            case "Round": applyShapeStyle(btnRound, tvRound, COLOR_ACTIVE_BG, COLOR_ACTIVE_TEXT); break;
            case "Squoval": applyShapeStyle(btnSquoval, tvSquoval, COLOR_ACTIVE_BG, COLOR_ACTIVE_TEXT); break;
            case "Lipstick": applyShapeStyle(btnLipstick, tvLipstick, COLOR_ACTIVE_BG, COLOR_ACTIVE_TEXT); break;
        }
    }

    // Menerapkan style pada satu tombol shape
    private void applyShapeStyle(LinearLayout layout, TextView textView, String bgColorHex, String textColorHex) {
        if (layout == null || textView == null) return;
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setCornerRadius(32f);
        gd.setColor(Color.parseColor(bgColorHex));
        layout.setBackground(gd);
        textView.setTextColor(Color.parseColor(textColorHex));
    }

    // Update UI tombol length
    private void updateLengthUI() {
        resetLengthButtonStyle(btnShort);
        resetLengthButtonStyle(btnMedium);
        resetLengthButtonStyle(btnLong);

        if (selectedLength.equals("Short")) {
            setActiveLengthButtonStyle(btnShort);
        } else if (selectedLength.equals("Medium")) {
            setActiveLengthButtonStyle(btnMedium);
        } else if (selectedLength.equals("Long")) {
            setActiveLengthButtonStyle(btnLong);
        }
    }

    private void resetLengthButtonStyle(AppCompatButton button) {
        button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(COLOR_INACTIVE_BG)));
        button.setTextColor(Color.parseColor(COLOR_INACTIVE_TEXT));
        // Tambahkan stroke
        button.setTextColor(Color.parseColor("#E8E3F5"));
        button.setWidth(1);
    }

    private void setActiveLengthButtonStyle(AppCompatButton button) {
        button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(COLOR_ACTIVE_BG)));
        button.setTextColor(Color.parseColor(COLOR_ACTIVE_TEXT));
        button.setWidth(0); // hilangkan stroke
    }
}