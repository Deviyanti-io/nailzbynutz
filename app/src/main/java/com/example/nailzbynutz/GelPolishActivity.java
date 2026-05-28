package com.example.nailzbynutz;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class GelPolishActivity extends AppCompatActivity {

    private LinearLayout typeRegular, typeGel, typeMatte, typeChrome, typeCateye, typeGlitter, typeJelly, typeMagnetic;
    private LinearLayout finishMatte, finishGlossy;
    private Button btnNextGel;
    private ImageView btnBackGel;

    private String selectedPolishType = "Gel Polish";
    private String selectedTopCoat = "Glossy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gel_polish);

        btnBackGel = findViewById(R.id.btn_back_gel);
        typeRegular = findViewById(R.id.type_regular);
        typeGel = findViewById(R.id.type_gel);
        typeMatte = findViewById(R.id.type_matte);
        typeChrome = findViewById(R.id.type_chrome);
        typeCateye = findViewById(R.id.type_cateye);
        typeGlitter = findViewById(R.id.type_glitter);
        typeJelly = findViewById(R.id.type_jelly);
        typeMagnetic = findViewById(R.id.type_magnetic);
        finishMatte = findViewById(R.id.finish_matte);
        finishGlossy = findViewById(R.id.finish_glossy);
        btnNextGel = findViewById(R.id.btn_next_gel);

        btnBackGel.setOnClickListener(v -> finish());

        setPolishTypeListener(typeRegular, "Regular");
        setPolishTypeListener(typeGel, "Gel Polish");
        setPolishTypeListener(typeMatte, "Matte");
        setPolishTypeListener(typeChrome, "Chrome");
        setPolishTypeListener(typeCateye, "Cat Eye");
        setPolishTypeListener(typeGlitter, "Glitter");
        setPolishTypeListener(typeJelly, "Jelly");
        setPolishTypeListener(typeMagnetic, "Magnetic");

        finishMatte.setOnClickListener(v -> {
            selectedTopCoat = "Matte";
            updateTopCoatUI(finishMatte, finishGlossy);
        });
        finishGlossy.setOnClickListener(v -> {
            selectedTopCoat = "Glossy";
            updateTopCoatUI(finishGlossy, finishMatte);
        });

        setActivePolishType(typeGel);
        updateTopCoatUI(finishGlossy, finishMatte);

        btnNextGel.setOnClickListener(v -> {
            Intent intent = new Intent(GelPolishActivity.this, CustomNailColorActivity.class);
            intent.putExtra("SHAPE_DATA", "Almond");
            intent.putExtra("LENGTH_DATA", "Medium");
            intent.putExtra("COLOR_TYPE_DATA", selectedPolishType);
            intent.putExtra("FINISH_DATA", selectedTopCoat);
            intent.putExtra("FROM_GEL_POLISH", true);
            startActivity(intent);
        });
    }

    private void setPolishTypeListener(LinearLayout layout, String type) {
        layout.setOnClickListener(v -> {
            selectedPolishType = type;
            resetPolishTypeBackground();
            setActivePolishType(layout);
            Toast.makeText(this, "Pilih: " + type, Toast.LENGTH_SHORT).show();
        });
    }

    private void resetPolishTypeBackground() {
        LinearLayout[] types = {typeRegular, typeGel, typeMatte, typeChrome, typeCateye, typeGlitter, typeJelly, typeMagnetic};
        for (LinearLayout t : types) {
            t.setBackgroundResource(R.drawable.bg_card_rounded);
        }
    }

    private void setActivePolishType(LinearLayout activeLayout) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setCornerRadius(20f);
        gd.setColor(getColor(R.color.lavender_dark));
        activeLayout.setBackground(gd);
    }

    private void updateTopCoatUI(LinearLayout active, LinearLayout inactive) {
        GradientDrawable gdActive = new GradientDrawable();
        gdActive.setShape(GradientDrawable.RECTANGLE);
        gdActive.setCornerRadius(20f);
        gdActive.setColor(getColor(R.color.lavender_dark));
        active.setBackground(gdActive);

        GradientDrawable gdInactive = new GradientDrawable();
        gdInactive.setShape(GradientDrawable.RECTANGLE);
        gdInactive.setCornerRadius(20f);
        gdInactive.setColor(getColor(R.color.background_card));
        gdInactive.setStroke(2, getColor(R.color.divider));
        inactive.setBackground(gdInactive);
    }
}