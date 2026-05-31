package com.example.nailzbynutz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ExploreActivity extends AppCompatActivity {

    private RecyclerView rvExplore;
    private ExploreAdapter adapter;
    private ArrayList<OwnerManageCatalogActivity.CatalogItem> exploreList;
    private ArrayList<OwnerManageCatalogActivity.CatalogItem> filteredList;
    private DatabaseReference catalogRef;
    private EditText etSearch;
    private SharedPreferences localPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        localPrefs = getSharedPreferences("LocalCatalogPrefs", MODE_PRIVATE);
        catalogRef = FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("catalogs");

        rvExplore = findViewById(R.id.rv_explore);
        rvExplore.setLayoutManager(new GridLayoutManager(this, 2));

        exploreList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new ExploreAdapter(filteredList);
        rvExplore.setAdapter(adapter);

        etSearch = findViewById(R.id.et_search);

        findViewById(R.id.btn_heart_top).setOnClickListener(v -> {
            startActivity(new Intent(ExploreActivity.this, WishlistActivity.class));
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSearch(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        loadCatalogData();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_explore);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainNavigationActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(this, HistoryActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            } else if (id == R.id.nav_explore) {
                return true;
            }
            return false;
        });
    }

    private void filterSearch(String query) {
        filteredList.clear();
        if (query.trim().isEmpty()) {
            filteredList.addAll(exploreList);
        } else {
            for (OwnerManageCatalogActivity.CatalogItem item : exploreList) {
                if (item.name.toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void loadCatalogData() {
        catalogRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                exploreList.clear();

                int[] localImages = {
                        R.drawable.nail1, R.drawable.nail2, R.drawable.nail3, R.drawable.nail4, R.drawable.nail5, R.drawable.nail6,
                        R.drawable.nail7, R.drawable.nail8, R.drawable.nail9, R.drawable.nail10, R.drawable.nail11, R.drawable.nail12,
                        R.drawable.nail13, R.drawable.nail14, R.drawable.nail15, R.drawable.nail16, R.drawable.nail17, R.drawable.nail18,
                        R.drawable.nail19, R.drawable.nail20, R.drawable.nail21, R.drawable.nail22, R.drawable.nail23, R.drawable.nail24
                };
                String[] localNames = {
                        "Midnight Blue", "Blue Petal", "Pink Aurora", "Classic French", "Pink Ombre", "Neon Star",
                        "Dark Gothic", "Glitter Glam", "Soft Pastel", "Red Velvet", "Ocean Blue", "Matte Black",
                        "Rose Gold", "Chrome Silver", "Milky White", "Lilac Dream", "Emerald Green", "Sunset Orange",
                        "Cherry Bomb", "Nude Beige", "Galaxy Swirl", "Pearl White", "Tortoise Shell", "Marble Stone"
                };
                String[] localPrices = {
                        "80000", "85000", "90000", "95000", "85000", "80000", "90000", "85000", "80000", "95000", "100000", "75000",
                        "80000", "85000", "80000", "90000", "75000", "110000", "95000", "100000", "95000", "105000", "120000", "115000"
                };

                for (int i = 0; i < localImages.length; i++) {
                    String id = "local_" + i;
                    if (!localPrefs.getBoolean("deleted_" + id, false)) {
                        exploreList.add(new OwnerManageCatalogActivity.CatalogItem(id, localNames[i], localPrices[i], "res_" + localImages[i], true));
                    }
                }

                for (DataSnapshot item : snapshot.getChildren()) {
                    String id = item.getKey();
                    String name = item.child("name").getValue(String.class);
                    String price = item.child("price").getValue(String.class);
                    String url = item.child("imageUrl").getValue(String.class);
                    if (price == null) price = "0";
                    exploreList.add(new OwnerManageCatalogActivity.CatalogItem(id, name, price, url, false));
                }

                filterSearch(etSearch.getText().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private boolean isLiked(String name) {
        SharedPreferences prefs = getSharedPreferences("WishlistPrefs", MODE_PRIVATE);
        return prefs.getBoolean(name, false);
    }

    private void toggleWishlistStatus(OwnerManageCatalogActivity.CatalogItem item, boolean newState) {
        SharedPreferences prefs = getSharedPreferences("WishlistPrefs", MODE_PRIVATE);
        prefs.edit().putBoolean(item.name, newState).apply();
    }

    public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ViewHolder> {
        private ArrayList<OwnerManageCatalogActivity.CatalogItem> list;

        public ExploreAdapter(ArrayList<OwnerManageCatalogActivity.CatalogItem> list) {
            this.list = list;
        }

        @NonNull @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_explore, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            OwnerManageCatalogActivity.CatalogItem item = list.get(position);
            holder.tvName.setText(item.name);

            Locale localeID = new Locale("in", "ID");
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
            formatRupiah.setMaximumFractionDigits(0);
            try {
                holder.tvPrice.setText(formatRupiah.format(Integer.parseInt(item.price)));
            } catch (Exception e) { holder.tvPrice.setText("Rp " + item.price); }

            // FIX: Logika Cerdas Membaca Gambar
            if (item.isLocal || (item.imageUrl != null && item.imageUrl.startsWith("res_"))) {
                try {
                    int resId = Integer.parseInt(item.imageUrl.replace("res_", ""));
                    holder.imgNail.setImageResource(resId);
                } catch (Exception e) {
                    holder.imgNail.setImageResource(android.R.color.darker_gray);
                }
            } else {
                try {
                    holder.imgNail.setImageURI(Uri.parse(item.imageUrl));
                } catch (Exception e) {
                    holder.imgNail.setImageResource(android.R.color.darker_gray);
                }
            }

            boolean liked = isLiked(item.name);
            if (liked) {
                holder.btnLike.setImageResource(R.drawable.ic_heart_off);
                holder.btnLike.setColorFilter(getColor(R.color.success));
            } else {
                holder.btnLike.setImageResource(R.drawable.ic_heart_off);
                holder.btnLike.setColorFilter(getColor(R.color.lavender_dark));
            }

            holder.btnLike.setOnClickListener(v -> {
                boolean isCurrentlyLiked = isLiked(item.name);
                toggleWishlistStatus(item, !isCurrentlyLiked);
                notifyItemChanged(position);

                if (!isCurrentlyLiked) Toast.makeText(ExploreActivity.this, "Ditambahkan ke Wishlist ❤️", Toast.LENGTH_SHORT).show();
                else Toast.makeText(ExploreActivity.this, "Dihapus dari Wishlist", Toast.LENGTH_SHORT).show();
            });

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(ExploreActivity.this, CustomNailColorActivity.class);
                intent.putExtra("SHAPE_DATA", item.name);
                startActivity(intent);
            });
        }

        @Override public int getItemCount() { return list.size(); }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgNail, btnLike;
            TextView tvName, tvPrice;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imgNail = itemView.findViewById(R.id.img_explore);
                btnLike = itemView.findViewById(R.id.btn_like_item);
                tvName = itemView.findViewById(R.id.tv_explore_name);
                tvPrice = itemView.findViewById(R.id.tv_explore_price);
            }
        }
    }
}