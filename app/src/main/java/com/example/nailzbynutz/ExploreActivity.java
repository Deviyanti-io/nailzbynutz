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
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_explore) {
                return true;
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(this, HistoryActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
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
        int[] images = {
                R.drawable.nail1, R.drawable.nail2, R.drawable.nail3, R.drawable.nail4,
                R.drawable.nail5, R.drawable.nail6, R.drawable.nail7, R.drawable.nail8,
                R.drawable.nail9, R.drawable.nail10, R.drawable.nail11, R.drawable.nail12,
                R.drawable.nail13, R.drawable.nail14, R.drawable.nail15, R.drawable.nail16,
                R.drawable.nail17, R.drawable.nail18, R.drawable.nail19, R.drawable.nail20,
                R.drawable.nail21, R.drawable.nail22, R.drawable.nail23, R.drawable.nail24
        };

        fullList.clear();
        for (int i = 0; i < names.length; i++) {
            fullList.add(new NailModel(names[i], "Rp " + prices[i], images[i], false));
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