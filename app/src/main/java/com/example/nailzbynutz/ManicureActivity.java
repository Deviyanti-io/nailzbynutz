package com.example.nailzbynutz;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class ManicureActivity extends AppCompatActivity {

    private RecyclerView rvServices;
    private BottomNavigationView bottomNav;
    private ServiceAdapter adapter;
    private List<ServiceModel> serviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manicure);

        rvServices = findViewById(R.id.rv_services);
        bottomNav = findViewById(R.id.bottom_navigation);

        rvServices.setLayoutManager(new LinearLayoutManager(this));

        serviceList = new ArrayList<>();
        // Hanya dua layanan
        serviceList.add(new ServiceModel("Basic Manicure",
                "Pembersihan kuku, perapian kutikula, pembentukan kuku, dan pijat tangan relaksasi.",
                "45.000", R.drawable.ic_nail_shape));
        serviceList.add(new ServiceModel("Spa Pedicure",
                "Rendam kaki dengan garam spa, scrub pengangkat sel kulit mati, masker kaki, dan perawatan kuku.",
                "65.000", R.drawable.ic_nail_shape));

        adapter = new ServiceAdapter(serviceList);
        rvServices.setAdapter(adapter);

        findViewById(R.id.btn_back_manicure).setOnClickListener(v -> finish());

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainNavigationActivity.class));
                finish();
            } else if (id == R.id.nav_explore) {
                startActivity(new Intent(this, ExploreActivity.class));
                finish();
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(this, HistoryActivity.class));
                finish();
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
            }
            return true;
        });
        bottomNav.setSelectedItemId(R.id.nav_explore);
    }
}