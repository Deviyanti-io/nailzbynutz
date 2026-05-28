package com.example.nailzbynutz;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ManicureActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Tidak perlu setContentView karena langsung pindah ke booking
        // Jika tetap ingin menampilkan layout, bisa diarahkan ke BookingAppointmentActivity
        Intent intent = new Intent(this, BookingAppointmentActivity.class);
        // Kirim data default jika perlu
        intent.putExtra("FINAL_SHAPE", "Almond");
        intent.putExtra("FINAL_LENGTH", "Medium");
        intent.putExtra("FINAL_COLOR_TYPE", "Solid");
        intent.putExtra("FINAL_COLOR", "#FFB6C1");
        intent.putExtra("FINAL_FINISH", "Glossy");
        intent.putExtra("FINAL_SIZE", "M (16-12-13-12-10mm)");
        intent.putExtra("FINAL_NOTES", "Manicure service");
        startActivity(intent);
        finish(); // Tutup activity agar tidak kembali
    }
}