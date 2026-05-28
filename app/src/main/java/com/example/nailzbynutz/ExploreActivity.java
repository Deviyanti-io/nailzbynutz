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
    private List<NailModel> fullList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        rvExplore = findViewById(R.id.rv_explore);
        bottomNav = findViewById(R.id.bottom_navigation);
        btnHeartTop = findViewById(R.id.btn_heart_top);
        etSearch = findViewById(R.id.et_search);

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

        btnHeartTop.setOnClickListener(v -> startActivity(new Intent(this, WishlistActivity.class)));

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
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
        bottomNav.setSelectedItemId(R.id.nav_explore);
    }

    private void loadNailData() {
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
        fullList.clear();
        for (int i = 1; i <= 24; i++) {
            int resId = getResources().getIdentifier("nail" + i, "drawable", getPackageName());
            if (resId != 0) {
                fullList.add(new NailModel(names[i-1], "Rp " + prices[i-1], resId));
            } else {
                // Fallback jika gambar tidak ada
                fullList.add(new NailModel(names[i-1], "Rp " + prices[i-1], R.drawable.nail1));
            }
        }
        nailList.clear();
        nailList.addAll(fullList);
    }

    private void filterProducts(String query) {
        nailList.clear();
        if (query.isEmpty()) {
            nailList.addAll(fullList);
        } else {
            for (NailModel nail : fullList) {
                if (nail.getName().toLowerCase().contains(query.toLowerCase())) {
                    nailList.add(nail);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}