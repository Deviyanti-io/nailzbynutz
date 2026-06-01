package com.example.nailzbynutz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone;
    private Button btnSave;
    private DatabaseReference userRef;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Deklarasi Komponen XML (Pastikan ID-nya sesuai dengan yang ada di XML Anda)
        etName = findViewById(R.id.et_edit_name);
        etEmail = findViewById(R.id.et_edit_email);
        etPhone = findViewById(R.id.et_edit_phone); // INI TAMBAHAN UNTUK NOMOR HP
        btnSave = findViewById(R.id.btn_save_profile);

        View btnBack = findViewById(R.id.btn_back_edit_profile);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        // Ambil data User dari Sesi Lokal
        SharedPreferences session = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentUsername = session.getString("USER_NAME", "Guest");
        String currentEmail = session.getString("USER_EMAIL", "");
        String currentPhone = session.getString("USER_PHONE", "");

        // Set teks bawaan
        if (etName != null) etName.setText(currentUsername);
        if (etEmail != null) etEmail.setText(currentEmail);
        if (etPhone != null) etPhone.setText(currentPhone);

        // Akses Firebase
        userRef = FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users").child(currentUsername);

        // Tarik data No HP dan Email asli dari internet jika ada
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild("email") && etEmail != null) {
                        etEmail.setText(snapshot.child("email").getValue(String.class));
                    }
                    if (snapshot.hasChild("phone") && etPhone != null) {
                        etPhone.setText(snapshot.child("phone").getValue(String.class));
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Tombol Simpan
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> saveProfileData());
        }
    }

    private void saveProfileData() {
        String newName = etName.getText().toString().trim();
        String newEmail = etEmail.getText().toString().trim();
        String newPhone = etPhone.getText().toString().trim();

        if (newName.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty()) {
            Toast.makeText(this, "Semua data termasuk Nomor HP harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simpan data baru ke Firebase
        userRef.child("name").setValue(newName);
        userRef.child("email").setValue(newEmail);
        userRef.child("phone").setValue(newPhone); // Menyimpan Nomor HP ke Database!

        // Perbarui Sesi Lokal
        SharedPreferences session = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = session.edit();
        editor.putString("USER_NAME", newName);
        editor.putString("USER_EMAIL", newEmail);
        editor.putString("USER_PHONE", newPhone);
        editor.apply();

        Toast.makeText(this, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show();
        finish(); // Kembali ke halaman Profile
    }
}