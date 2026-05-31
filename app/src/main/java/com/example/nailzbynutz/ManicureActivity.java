package com.example.nailzbynutz;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ManicureActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, BookingAppointmentActivity.class);
        intent.putExtra("SERVICE_TYPE", "Manicure");
        intent.putExtra("SHAPE_DATA", "Manicure Service");
        intent.putExtra("LENGTH_DATA", "Standar");
        intent.putExtra("COLOR_TYPE_DATA", "Natural");
        intent.putExtra("COLOR_HEX_DATA", "#FFB6C1");
        intent.putExtra("FINISH_DATA", "Clean");
        intent.putExtra("SIZE_DATA", "-");
        intent.putExtra("NOTES_DATA", "Layanan Manicure & Pedicure");

        intent.putExtra("BASE_PRICE", 45000);
        intent.putExtra("GRAND_TOTAL", 45000);

        startActivity(intent);
        finish();
    }
}