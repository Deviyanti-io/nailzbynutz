package com.example.nailzbynutz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;

public class ServicesActivity extends AppCompatActivity {

    private TextView tvManicurePrice, tvGelPrice, tvCustomPrice;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        // Inisialisasi TextView harga (Pastikan ID ini sesuai dengan XML Anda)
        tvManicurePrice = findViewById(R.id.tv_price_manicure);
        tvGelPrice = findViewById(R.id.tv_price_gel);
        tvCustomPrice = findViewById(R.id.tv_price_custom);

        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        // Aksi klik ke halaman masing-masing servis
        View cardManicure = findViewById(R.id.card_manicure);
        if (cardManicure != null) {
            cardManicure.setOnClickListener(v -> startActivity(new Intent(this, ManicureActivity.class)));
        }

        View cardGelPolish = findViewById(R.id.card_gel_polish);
        if (cardGelPolish != null) {
            cardGelPolish.setOnClickListener(v -> startActivity(new Intent(this, GelPolishActivity.class)));
        }

        View cardCustomNails = findViewById(R.id.card_custom_nails);
        if (cardCustomNails != null) {
            cardCustomNails.setOnClickListener(v -> startActivity(new Intent(this, CustomNailShapeActivity.class)));
        }

        // Panggil data harga dari Firebase
        loadServicesFromFirebase();
    }

    private void loadServicesFromFirebase() {
        DatabaseReference svcRef = FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("services");

        svcRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Locale localeID = new Locale("in", "ID");
                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                formatRupiah.setMaximumFractionDigits(0);

                if (snapshot.hasChild("Manicure") && tvManicurePrice != null) {
                    int priceMani = snapshot.child("Manicure").child("basePrice").getValue(Integer.class);
                    tvManicurePrice.setText(formatRupiah.format(priceMani));
                }

                if (snapshot.hasChild("Gel_Polish") && tvGelPrice != null) {
                    int priceGel = snapshot.child("Gel_Polish").child("basePrice").getValue(Integer.class);
                    tvGelPrice.setText(formatRupiah.format(priceGel));
                }

                if (snapshot.hasChild("Custom_Nails") && tvCustomPrice != null) {
                    int priceCustom = snapshot.child("Custom_Nails").child("basePrice").getValue(Integer.class);
                    tvCustomPrice.setText(formatRupiah.format(priceCustom));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}