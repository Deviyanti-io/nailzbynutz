package com.example.nailzbynutz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Tambahan Import untuk Firebase dan HashMap
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private EditText etEmail, etUsername, etPassword;
    private Button btnRegister;
    private TextView tvGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etEmail = findViewById(R.id.et_reg_email);
        etUsername = findViewById(R.id.et_reg_username);
        etPassword = findViewById(R.id.et_reg_password);
        btnRegister = findViewById(R.id.btn_register);
        tvGoToLogin = findViewById(R.id.tv_go_to_login);

        // 1. Inisialisasi Firebase Database dengan URL spesifik server Asia Tenggara
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app");
        // 2. Buat "tabel" atau "folder" bernama "users" di dalam database
        DatabaseReference usersRef = database.getReference("users");

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show();
                return; // Hentikan proses jika ada yang kosong
            }

            // 3. Siapkan data yang akan dikirim menggunakan HashMap
            HashMap<String, String> userData = new HashMap<>();
            userData.put("email", email);
            userData.put("username", username);
            userData.put("password", password);

            // 4. Kirim data ke Firebase!
            // push() digunakan agar Firebase membuatkan ID unik secara otomatis untuk setiap user baru
            usersRef.push().setValue(userData)
                    .addOnSuccessListener(aVoid -> {
                        // --- KODE INI BERJALAN JIKA DATA SUKSES MASUK KE SERVER ---

                        // Tetap simpan di SharedPreferences untuk fitur "Ingat Saya" atau Auto-Login
                        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        prefs.edit()
                                .putString("SAVED_EMAIL", email)
                                .putString("SAVED_USER", username)
                                .putString("SAVED_PASS", password)
                                .apply();

                        Toast.makeText(SignUpActivity.this, "Akun berhasil didaftarkan ke Firebase!", Toast.LENGTH_SHORT).show();
                        finish(); // Tutup halaman sign up dan kembali ke login
                    })
                    .addOnFailureListener(e -> {
                        // --- KODE INI BERJALAN JIKA GAGAL (MISAL TIDAK ADA INTERNET) ---
                        Toast.makeText(SignUpActivity.this, "Gagal mendaftar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        tvGoToLogin.setOnClickListener(v -> finish());
    }
}