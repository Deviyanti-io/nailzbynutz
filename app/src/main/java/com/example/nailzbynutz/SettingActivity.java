package com.example.nailzbynutz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {

    private Switch swNotification;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        swNotification = findViewById(R.id.sw_notification);
        btnBack = findViewById(R.id.btn_back_settings);

        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean notifEnabled = prefs.getBoolean("notifications", true);
        swNotification.setChecked(notifEnabled);

        swNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("notifications", isChecked);
            editor.apply();
            Toast.makeText(this, isChecked ? "Notifikasi diaktifkan" : "Notifikasi dinonaktifkan", Toast.LENGTH_SHORT).show();
        });

        btnBack.setOnClickListener(v -> finish());
    }
}