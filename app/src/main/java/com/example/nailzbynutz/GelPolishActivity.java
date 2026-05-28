package com.example.nailzbynutz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GelPolishActivity extends AppCompatActivity {

    private LinearLayout typeRegular, typeGel, typeMatte, typeChrome, typeCateye, typeGlitter, typeJelly, typeMagnetic;
    private LinearLayout finishMatte, finishGlossy;
    private BottomNavigationView bottomNav;
    private String selectedType = "";
    private String selectedFinish = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gel_polish);

        // Initialize views
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
        bottomNav = findViewById(R.id.bottom_navigation);

        // Back button
        findViewById(R.id.btn_back_gel).setOnClickListener(v -> finish());

        // Polish type click listeners
        setupTypeClickListener(typeRegular, "Regular");
        setupTypeClickListener(typeGel, "Gel Polish");
        setupTypeClickListener(typeMatte, "Matte");
        setupTypeClickListener(typeChrome, "Chrome");
        setupTypeClickListener(typeCateye, "Cat Eye");
        setupTypeClickListener(typeGlitter, "Glitter");
        setupTypeClickListener(typeJelly, "Jelly");
        setupTypeClickListener(typeMagnetic, "Magnetic");

        // Finish click listeners
        setupFinishClickListener(finishMatte, "Matte Top Coat");
        setupFinishClickListener(finishGlossy, "Glossy Top Coat");

        // Next button
        findViewById(R.id.btn_next_gel).setOnClickListener(v -> {
            if (selectedType.isEmpty()) {
                Toast.makeText(this, "Pilih jenis polish terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedFinish.isEmpty()) {
                Toast.makeText(this, "Pilih top coat finish terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, BookingAppointmentActivity.class);
            intent.putExtra("service_type", "Gel Polish");
            intent.putExtra("polish_type", selectedType);
            intent.putExtra("top_coat", selectedFinish);
            startActivity(intent);
        });

        // Bottom navigation
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainNavigationActivity.class));
                finish();
            } else if (id == R.id.nav_explore) {
                startActivity(new Intent(this, ExploreActivity.class));
                finish();
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(this, HistoryActivity.class));
                finish();
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
            }
            return true;
        });
        bottomNav.setSelectedItemId(R.id.nav_explore);
    }

    private void setupTypeClickListener(LinearLayout layout, String type) {
        layout.setOnClickListener(v -> {
            resetTypeSelection();
            selectedType = type;
            layout.setBackgroundTintList(getColorStateList(R.color.lavender_dark));
            TextView text = (TextView) layout.getChildAt(1);
            if (text != null) text.setTextColor(getColor(R.color.white));
        });
    }

    private void resetTypeSelection() {
        LinearLayout[] types = {typeRegular, typeGel, typeMatte, typeChrome, typeCateye, typeGlitter, typeJelly, typeMagnetic};
        for (LinearLayout type : types) {
            type.setBackgroundTintList(getColorStateList(R.color.background_card));
            TextView text = (TextView) type.getChildAt(1);
            if (text != null) text.setTextColor(getColor(R.color.text_primary));
        }
    }

    private void setupFinishClickListener(LinearLayout layout, String finish) {
        layout.setOnClickListener(v -> {
            resetFinishSelection();
            selectedFinish = finish;
            layout.setBackgroundTintList(getColorStateList(R.color.lavender_dark));
            TextView text = (TextView) layout.getChildAt(1);
            if (text != null) text.setTextColor(getColor(R.color.white));
        });
    }

    private void resetFinishSelection() {
        LinearLayout[] finishes = {finishMatte, finishGlossy};
        for (LinearLayout finish : finishes) {
            finish.setBackgroundTintList(getColorStateList(R.color.background_card));
            TextView text = (TextView) finish.getChildAt(1);
            if (text != null) text.setTextColor(getColor(R.color.text_primary));
        }
    }
}