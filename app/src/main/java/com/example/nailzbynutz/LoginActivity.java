package com.example.nailzbynutz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvGoToSignUp;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.edt_password);
        btnLogin = findViewById(R.id.btn_login);
        tvGoToSignUp = findViewById(R.id.tv_go_to_signup);
        sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Isi username dan password!", Toast.LENGTH_SHORT).show();
                return;
            }

            String savedUser = sharedPref.getString("SAVED_USER", "");
            String savedPass = sharedPref.getString("SAVED_PASS", "");
            String savedEmail = sharedPref.getString("SAVED_EMAIL", "");

            if (username.equals(savedUser) && password.equals(savedPass)) {
                SharedPreferences session = getSharedPreferences("UserSession", MODE_PRIVATE);
                session.edit().putString("USER_NAME", username).putString("USER_EMAIL", savedEmail).apply();

                Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainNavigationActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Username atau password salah!", Toast.LENGTH_LONG).show();
            }
        });

        tvGoToSignUp.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
    }
}