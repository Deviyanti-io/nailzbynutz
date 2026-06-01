package com.example.nailzbynutz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etPhone;
    private Button btnSignUp;
    private TextView tvGoToLogin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etEmail = findViewById(R.id.et_reg_email);
        etUsername = findViewById(R.id.et_reg_username);
        etPhone = findViewById(R.id.et_phone_signup);
        etPassword = findViewById(R.id.et_reg_password);

        btnSignUp = findViewById(R.id.btn_register);
        tvGoToLogin = findViewById(R.id.tv_go_to_login);

        DatabaseReference usersRef =
                FirebaseDatabase.getInstance(
                        "https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app"
                ).getReference("users");

        btnSignUp.setOnClickListener(v -> {

            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (username.isEmpty()
                    || email.isEmpty()
                    || password.isEmpty()
                    || phone.isEmpty()) {

                Toast.makeText(
                        this,
                        "Harap isi semua data termasuk Nomor HP!",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            btnSignUp.setEnabled(false);
            btnSignUp.setText("Mendaftar...");

            usersRef.child(username).get()
                    .addOnSuccessListener(snapshot -> {

                        if (snapshot.exists()) {

                            Toast.makeText(
                                    this,
                                    "Username sudah digunakan!",
                                    Toast.LENGTH_SHORT
                            ).show();

                            btnSignUp.setEnabled(true);
                            btnSignUp.setText("Sign Up");
                            return;
                        }

                        HashMap<String, Object> userData = new HashMap<>();

                        userData.put("username", username);
                        userData.put("email", email);
                        userData.put("password", password);
                        userData.put("phone", phone);

                        userData.put("points", 0);
                        userData.put("memberTier", "Bronze");
                        userData.put("role", "user");

                        usersRef.child(username)
                                .setValue(userData)
                                .addOnSuccessListener(aVoid -> {

                                    Toast.makeText(
                                            this,
                                            "Pendaftaran Berhasil! Silakan Login.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    startActivity(
                                            new Intent(
                                                    this,
                                                    LoginActivity.class
                                            )
                                    );

                                    finish();

                                })
                                .addOnFailureListener(e -> {

                                    Toast.makeText(
                                            this,
                                            "Pendaftaran Gagal: " + e.getMessage(),
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    btnSignUp.setEnabled(true);
                                    btnSignUp.setText("Sign Up");
                                });
                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(
                                this,
                                "Terjadi kesalahan: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();

                        btnSignUp.setEnabled(true);
                        btnSignUp.setText("Sign Up");
                    });
        });

        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}