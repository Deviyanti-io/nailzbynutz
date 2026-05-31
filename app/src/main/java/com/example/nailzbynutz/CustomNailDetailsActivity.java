package com.example.nailzbynutz;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CustomNailDetailsActivity extends AppCompatActivity {

    private int thumb = 14, index = 10, middle = 11, ring = 10, pinky = 8;
    private TextView tvThumb, tvIndex, tvMiddle, tvRing, tvPinky;
    private Button btnSizeS, btnSizeM, btnSizeL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_nail_details);

        findViewById(R.id.btn_back_details).setOnClickListener(v -> finish());

        tvThumb = findViewById(R.id.tv_val_thumb);
        tvIndex = findViewById(R.id.tv_val_index);
        tvMiddle = findViewById(R.id.tv_val_middle);
        tvRing = findViewById(R.id.tv_val_ring);
        tvPinky = findViewById(R.id.tv_val_pinky);

        btnSizeS = findViewById(R.id.btn_size_s);
        btnSizeM = findViewById(R.id.btn_size_m);
        btnSizeL = findViewById(R.id.btn_size_l);

        btnSizeS.setOnClickListener(v -> { setSizes(13, 9, 10, 9, 7); setActiveSize(btnSizeS); });
        btnSizeM.setOnClickListener(v -> { setSizes(14, 10, 11, 10, 8); setActiveSize(btnSizeM); });
        btnSizeL.setOnClickListener(v -> { setSizes(15, 11, 12, 11, 9); setActiveSize(btnSizeL); });

        setupCounter(R.id.btn_minus_thumb, R.id.btn_plus_thumb, tvThumb, "thumb");
        setupCounter(R.id.btn_minus_index, R.id.btn_plus_index, tvIndex, "index");
        setupCounter(R.id.btn_minus_middle, R.id.btn_plus_middle, tvMiddle, "middle");
        setupCounter(R.id.btn_minus_ring, R.id.btn_plus_ring, tvRing, "ring");
        setupCounter(R.id.btn_minus_pinky, R.id.btn_plus_pinky, tvPinky, "pinky");

        EditText etNotes = findViewById(R.id.et_special_notes);
        Button btnNext = findViewById(R.id.btn_next_details);

        btnNext.setOnClickListener(v -> {
            String sizeStr = "Custom (" + thumb + "-" + index + "-" + middle + "-" + ring + "-" + pinky + "mm)";
            String notes = etNotes.getText().toString();

            Intent intent = new Intent(CustomNailDetailsActivity.this, CustomNailReviewActivity.class);
            if (getIntent().getExtras() != null) {
                intent.putExtras(getIntent().getExtras());
            }
            intent.putExtra("SIZE_DATA", sizeStr);
            intent.putExtra("NOTES_DATA", notes);
            startActivity(intent);
        });
    }

    private void setActiveSize(Button activeBtn) {
        Button[] btns = {btnSizeS, btnSizeM, btnSizeL};
        for (Button b : btns) {
            b.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.background_card)));
            b.setTextColor(getColor(R.color.text_primary));
        }
        activeBtn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.lavender_dark)));
        activeBtn.setTextColor(Color.WHITE);
    }

    private void setSizes(int t, int i, int m, int r, int p) {
        thumb = t; index = i; middle = m; ring = r; pinky = p;
        updateUI();
    }

    private void updateUI() {
        tvThumb.setText(String.valueOf(thumb)); tvIndex.setText(String.valueOf(index));
        tvMiddle.setText(String.valueOf(middle)); tvRing.setText(String.valueOf(ring));
        tvPinky.setText(String.valueOf(pinky));
    }

    private void setupCounter(int btnMinId, int btnPlusId, TextView tv, String finger) {
        findViewById(btnMinId).setOnClickListener(v -> {
            if (finger.equals("thumb") && thumb > 5) thumb--;
            else if (finger.equals("index") && index > 5) index--;
            else if (finger.equals("middle") && middle > 5) middle--;
            else if (finger.equals("ring") && ring > 5) ring--;
            else if (finger.equals("pinky") && pinky > 5) pinky--;
            updateUI();
        });
        findViewById(btnPlusId).setOnClickListener(v -> {
            if (finger.equals("thumb") && thumb < 25) thumb++;
            else if (finger.equals("index") && index < 25) index++;
            else if (finger.equals("middle") && middle < 25) middle++;
            else if (finger.equals("ring") && ring < 25) ring++;
            else if (finger.equals("pinky") && pinky < 25) pinky++;
            updateUI();
        });
    }
}