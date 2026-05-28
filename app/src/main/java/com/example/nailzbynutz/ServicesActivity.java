package com.example.nailzbynutz;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class ServicesActivity extends AppCompatActivity {

    private RecyclerView rvServices;
    private BottomNavigationView bottomNav;
    private ServiceAdapter adapter;
    private List<ServiceModel> serviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        // SESUAI KODE ASLI KAMU: Menghubungkan ID komponen UI
        rvServices = findViewById(R.id.rv_services);
        bottomNav = findViewById(R.id.bottom_navigation);

        // Setup RecyclerView secara vertikal lurus ke bawah
        rvServices.setLayoutManager(new LinearLayoutManager(this));
        loadServices();
        adapter = new ServiceAdapter(serviceList);
        rvServices.setAdapter(adapter);

        // Fungsi tombol panah kembali di top bar untuk menutup halaman layanan
        findViewById(R.id.btn_back_services).setOnClickListener(v -> finish());

        // PERBAIKAN BOTTOM NAVIGATION: Sinkronisasi menu bar bawah dari halaman internal service
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainNavigationActivity.class));
                overridePendingTransition(0, 0); // Efek transisi instan tanpa jeda kedip
                finish(); // Menutup activity lama agar tumpukan RAM efisien
                return true;
            } else if (id == R.id.nav_explore) {
                startActivity(new Intent(this, ExploreActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(this, HistoryActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_profile) {
                // Membuka halaman ProfileActivity secara lancar tanpa macet klik
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });

        // Menandai secara visual tab Explore sebagai menu aktif saat ini
        bottomNav.setSelectedItemId(R.id.nav_explore);
    }

    // Mengisi data item list katalog jasa salon kuku beserta rincian harganya
    private void loadServices() {
        serviceList = new ArrayList<>();
        serviceList.add(new ServiceModel("Basic Manicure", "Pembersihan kuku, perapian kutikula, dan pijat tangan", 45000, R.drawable.ic_nail_shape));
        serviceList.add(new ServiceModel("Spa Manicure", "Basic manicure + rendam garam, scrub, masker tangan", 75000, R.drawable.ic_nail_shape));
        serviceList.add(new ServiceModel("Basic Pedicure", "Perawatan kaki + pembersihan kuku kaki", 65000, R.drawable.ic_nail_shape));
        serviceList.add(new ServiceModel("Spa Pedicure", "Rendam kaki, scrub, masker, pijat kaki", 95000, R.drawable.ic_nail_shape));
        serviceList.add(new ServiceModel("Gel Polish", "Pewarnaan gel tahan lama", 85000, R.drawable.ic_gel));
        serviceList.add(new ServiceModel("Nail Art", "Desain kuku sesuai permintaan", 50000, R.drawable.ic_top_polish));
        serviceList.add(new ServiceModel("Press On Nail", "Pasang kuku palsu custom", 120000, R.drawable.ic_layers));
    }
}