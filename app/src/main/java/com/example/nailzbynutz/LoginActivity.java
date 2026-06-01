package com.example.nailzbynutz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

// Tambahan Import Firebase
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvGoToSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.edt_password);
        btnLogin = findViewById(R.id.btn_login);
        tvGoToSignUp = findViewById(R.id.tv_go_to_signup);

        // KEMBALIKAN ESTETIKA PUTIH MINIMALIS
        etUsername.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE));
        etUsername.setTextColor(android.graphics.Color.BLACK);
        etPassword.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE));
        etPassword.setTextColor(android.graphics.Color.BLACK);

        // Inisialisasi Database Firebase
        DatabaseReference usersRef = FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users");

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Isi username dan password!", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- LOGIKA KHUSUS AKUN OWNER ---
            if (username.equals("owner") && password.equals("admin123")) {
                Toast.makeText(this, "Selamat datang, Owner!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, OwnerDashboardActivity.class));
                finish();
                return;
            }

            // --- LOGIKA UNTUK USER BIASA (CARI DI FIREBASE) ---
            btnLogin.setEnabled(false); // Matikan tombol sementara
            btnLogin.setText("Memeriksa...");

            usersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        boolean isLoginSuccess = false;
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String dbPassword = userSnapshot.child("password").getValue(String.class);
                            String dbEmail = userSnapshot.child("email").getValue(String.class);

                            if (dbPassword != null && dbPassword.equals(password)) {
                                isLoginSuccess = true;

                                SharedPreferences session = getSharedPreferences("UserSession", MODE_PRIVATE);
                                session.edit()
                                        .putString("USER_NAME", username)
                                        .putString("USER_EMAIL", dbEmail)
                                        .apply();

                                Toast.makeText(LoginActivity.this, "Login Berhasil!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainNavigationActivity.class));
                                finish();
                                break;
                            }
                        }

                        if (!isLoginSuccess) {
                            Toast.makeText(LoginActivity.this, "Password salah!", Toast.LENGTH_LONG).show();
                            resetLoginButton();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Username tidak ditemukan!", Toast.LENGTH_LONG).show();
                        resetLoginButton();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(LoginActivity.this, "Error: Gagal terhubung ke database.", Toast.LENGTH_SHORT).show();
                    resetLoginButton();
                }
            });
        });

        tvGoToSignUp.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
    }

    private void resetLoginButton() {
        btnLogin.setEnabled(true);
        btnLogin.setText("LOGIN");
    }
}