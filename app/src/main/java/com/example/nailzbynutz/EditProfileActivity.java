package com.example.nailzbynutz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

// Tambahan Import Firebase
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        // Ambil data lama dari session untuk ditampilkan pertama kali
        SharedPreferences session = getSharedPreferences("UserSession", MODE_PRIVATE);
        String currentName = session.getString("USER_NAME", "Nailz Lover");
        String currentEmail = session.getString("USER_EMAIL", "hello@nailz.com");

        etName.setText(currentName);
        etEmail.setText(currentEmail);

        btnBack.setOnClickListener(v -> finish());

        // Inisialisasi Database Firebase
        DatabaseReference usersRef = FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users");

        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            String newEmail = etEmail.getText().toString().trim();

            if (newName.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(this, "Nama dan email tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSave.setEnabled(false);
            btnSave.setText("Menyimpan...");

            // Cari user berdasarkan username yang lama, lalu perbarui datanya
            usersRef.orderByChild("username").equalTo(currentName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot userSnap : snapshot.getChildren()) {
                            String userKey = userSnap.getKey(); // Dapatkan ID unik user

                            // Update data di Firebase
                            usersRef.child(userKey).child("username").setValue(newName);
                            usersRef.child(userKey).child("email").setValue(newEmail)
                                    .addOnSuccessListener(aVoid -> {
                                        // Update session lokal agar pencarian query berikutnya tidak error
                                        SharedPreferences.Editor editor = session.edit();
                                        editor.putString("USER_NAME", newName);
                                        editor.putString("USER_EMAIL", newEmail);
                                        editor.apply();

                                        Toast.makeText(EditProfileActivity.this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
                                        finish();
                                    });
                        }
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Gagal menemukan akun di server", Toast.LENGTH_SHORT).show();
                        btnSave.setEnabled(true);
                        btnSave.setText("SIMPAN PERUBAHAN");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(EditProfileActivity.this, "Error koneksi server", Toast.LENGTH_SHORT).show();
                    btnSave.setEnabled(true);
                    btnSave.setText("SIMPAN PERUBAHAN");
                }
            });
        });
    }
}