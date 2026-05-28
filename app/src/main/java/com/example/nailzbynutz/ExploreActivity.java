package com.example.nailzbynutz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class ExploreActivity extends AppCompatActivity {

    private RecyclerView rvExplore;
    private BottomNavigationView bottomNav;
    private ImageView btnHeartTop;
    private EditText etSearch;
    private ExploreAdapter adapter;
    private List<NailModel> nailList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        rvExplore = findViewById(R.id.rv_explore);
        bottomNav = findViewById(R.id.bottom_navigation);
        btnHeartTop = findViewById(R.id.btn_heart_top);
        etSearch = findViewById(R.id.et_search);

        // Setup RecyclerView (grid 2 kolom)
        rvExplore.setLayoutManager(new GridLayoutManager(this, 2));
        loadNailData();
        adapter = new ExploreAdapter(this, nailList);
        rvExplore.setAdapter(adapter);

        // Search filter
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        // Tombol heart top (buka WishlistActivity)
        btnHeartTop.setOnClickListener(v -> {
            Intent intent = new Intent(ExploreActivity.this, WishlistActivity.class);
            startActivity(intent);
        });

        // Bottom navigation
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainNavigationActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_explore) {
                return true;
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(this, HistoryActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                // Nanti akan dibuat ProfileActivity terpisah
                // startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
        bottomNav.setSelectedItemId(R.id.nav_explore);
    }

    private void loadNailData() {
        // Data produk sesuai gambar yang ada (nail1.jpg - nail24.jpg)
        // Harga bervariasi antara 75.000 - 150.000
        String[] names = {
                "Dreamy Lavender", "Bluey Pink", "Pink Aurora", "Red Scarlet", "Pink Glaze",
                "Sweet Pink", "Simple White Blue", "Pink Rose", "Blush Petal", "Mocha Cream",
                "Red Cherry", "Midnight Blue", "Blue Haze", "Royale Brown", "Silver Mist",
                "Red Bow", "Golden Muse", "Frosted Blue", "Red Aurora", "Pink Mirage",
                "Milky Red", "Nude Luxe", "Peach Aura", "Flower Chrome"
        };
        String[] prices = {
                "95.000", "85.000", "90.000", "100.000", "85.000",
                "80.000", "80.000", "100.000", "95.000", "95.000",
                "90.000", "90.000", "95.000", "90.000", "85.000",
                "80.000", "80.000", "80.000", "90.000", "90.000",
                "85.000", "85.000", "90.000", "95.000"
        };

        for (int i = 1; i <= 24; i++) {
            int resId = getResources().getIdentifier("nail" + i, "drawable", getPackageName());
            if (resId != 0) {
                nailList.add(new NailModel(names[i-1], "Rp " + prices[i-1], resId));
            } else {
                // fallback jika gambar tidak ada
                nailList.add(new NailModel(names[i-1], "Rp " + prices[i-1], R.drawable.nail1));
            }
        }
    }

    private void filterProducts(String query) {
        List<NailModel> filteredList = new ArrayList<>();
        for (NailModel nail : nailList) {
            if (nail.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(nail);
            }
        }
        adapter = new ExploreAdapter(this, filteredList);
        rvExplore.setAdapter(adapter);
    }
}