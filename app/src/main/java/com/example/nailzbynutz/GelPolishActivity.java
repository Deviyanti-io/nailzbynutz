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
    private Button btnNextGel;
    private ImageView btnBackGel;

    private String selectedPolishType = "Gel Polish";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gel_polish);

        // Inisialisasi komponen yang tersisa
        btnBackGel = findViewById(R.id.btn_back_gel);
        typeRegular = findViewById(R.id.type_regular);
        typeGel = findViewById(R.id.type_gel);
        typeMatte = findViewById(R.id.type_matte);
        typeChrome = findViewById(R.id.type_chrome);
        typeCateye = findViewById(R.id.type_cateye);
        typeGlitter = findViewById(R.id.type_glitter);
        typeJelly = findViewById(R.id.type_jelly);
        typeMagnetic = findViewById(R.id.type_magnetic);
        btnNextGel = findViewById(R.id.btn_next_gel);

        btnBackGel.setOnClickListener(v -> finish());

        // Set listener untuk masing-masing tipe polish
        setPolishTypeListener(typeRegular, "Regular");
        setPolishTypeListener(typeGel, "Gel Polish");
        setPolishTypeListener(typeMatte, "Matte");
        setPolishTypeListener(typeChrome, "Chrome");
        setPolishTypeListener(typeCateye, "Cat Eye");
        setPolishTypeListener(typeGlitter, "Glitter");
        setPolishTypeListener(typeJelly, "Jelly");
        setPolishTypeListener(typeMagnetic, "Magnetic");

        // Set default ke Gel Polish
        setActivePolishType(typeGel);

        btnNextGel.setOnClickListener(v -> {
            Intent intent = new Intent(GelPolishActivity.this, CustomNailColorActivity.class);
            intent.putExtra("SHAPE_DATA", "Almond");
            intent.putExtra("LENGTH_DATA", "Medium");
            intent.putExtra("COLOR_TYPE_DATA", selectedPolishType);
            intent.putExtra("FROM_GEL_POLISH", true); // Data Top Coat dihapus karena sudah di-handle di halaman color
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
}