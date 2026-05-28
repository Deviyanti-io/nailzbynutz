package com.example.nailzbynutz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName, etEmail;
    private Button btnSave;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etName = findViewById(R.id.et_edit_name);
        etEmail = findViewById(R.id.et_edit_email);
        btnSave = findViewById(R.id.btn_save_profile);
        btnBack = findViewById(R.id.btn_back_edit);

        // Ambil data lama dari session
        SharedPreferences session = getSharedPreferences("UserSession", MODE_PRIVATE);
        String currentName = session.getString("USER_NAME", "Nailz Lover");
        String currentEmail = session.getString("USER_EMAIL", "hello@nailz.com");
        etName.setText(currentName);
        etEmail.setText(currentEmail);

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            String newEmail = etEmail.getText().toString().trim();
            if (newName.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(this, "Nama dan email tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }
            // Simpan ke session
            SharedPreferences.Editor editor = session.edit();
            editor.putString("USER_NAME", newName);
            editor.putString("USER_EMAIL", newEmail);
            editor.apply();

            Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}