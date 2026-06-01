package com.example.nailzbynutz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.core.content.ContextCompat;
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
    private SharedPreferences localWishlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        localPrefs = getSharedPreferences("LocalCatalogPrefs", MODE_PRIVATE);
        localWishlist = getSharedPreferences("LocalWishlist", MODE_PRIVATE);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app");
        catalogRef = database.getReference("catalogs");

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
        setupBottomNavigation();
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
                        "Night Blue", "Blue Petal", "Pink Aurora", "Classic Red", "Pink Ombre", "Glassy Pink",
                        "Simple White Blue", "Pink Blossom", "Laluna", "Misterious Brown", "Red Flower", "Midnight Blue",
                        "Blue Ocean", "Chrome Brown", "Silk Silver", "Red Scarlet", "Glassy Gold", "Vibrant Blue",
                        "Cherry Bomb", "Nude Beige", "Red White Swirl", "Pearl White", "Nude Flower"
                };
                String[] localPrices = {
                        "80000", "85000", "90000", "95000", "85000", "80000", "90000", "85000", "80000", "95000", "100000", "85000",
                        "80000", "85000", "80000", "90000", "90000", "90000", "95000", "90000", "95000", "105000", "90000"
                };

                for (int i = 0; i < localImages.length; i++) {
                    String id = "local_" + i;
                    if (!localPrefs.getBoolean("deleted_" + id, false)) {
                        String safeName = localNames[i % localNames.length];
                        String safePrice = localPrices[i % localPrices.length];
                        exploreList.add(new OwnerManageCatalogActivity.CatalogItem(id, safeName, safePrice, "res_" + localImages[i], true));
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
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setupBottomNavigation() {
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

            if (item.isLocal || (item.imageUrl != null && item.imageUrl.startsWith("res_"))) {
                try {
                    int resId = Integer.parseInt(item.imageUrl.replace("res_", ""));
                    holder.imgNail.setImageResource(resId);
                } catch (Exception e) {
                    holder.imgNail.setImageResource(android.R.color.darker_gray);
                }
            } else {
                try {
                    com.bumptech.glide.Glide.with(holder.itemView.getContext())
                            .load(item.imageUrl)
                            .placeholder(android.R.color.darker_gray)
                            .into(holder.imgNail);
                } catch (Exception e) {
                    holder.imgNail.setImageResource(android.R.color.darker_gray);
                }
            }

            // MENGGUNAKAN LOCAL WISHLIST YANG SUDAH DIPERBAIKI
            String safeId = item.name.replace(" ", "_");
            boolean isLiked = localWishlist.getBoolean(safeId, false);

            if (isLiked) {
                holder.btnLike.setImageResource(R.drawable.ic_heart_on);
                holder.btnLike.setColorFilter(android.graphics.Color.parseColor("#FF4081"));
            } else {
                holder.btnLike.setImageResource(R.drawable.ic_heart_off);
                holder.btnLike.setColorFilter(ContextCompat.getColor(ExploreActivity.this, R.color.lavender_dark));
            }

            holder.btnLike.setOnClickListener(v -> {
                SharedPreferences.Editor editor = localWishlist.edit();
                if (isLiked) {
                    editor.remove(safeId);
                    editor.remove(safeId + "_name");
                    editor.remove(safeId + "_price");
                    editor.remove(safeId + "_image");
                    editor.remove(safeId + "_isLocal");
                    Toast.makeText(ExploreActivity.this, "Dihapus dari Wishlist", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putBoolean(safeId, true);
                    editor.putString(safeId + "_name", item.name);
                    editor.putString(safeId + "_price", item.price);
                    editor.putString(safeId + "_image", item.imageUrl);
                    editor.putBoolean(safeId + "_isLocal", item.isLocal);
                    Toast.makeText(ExploreActivity.this, "Ditambahkan ke Wishlist ❤️", Toast.LENGTH_SHORT).show();
                }
                editor.apply();
                notifyItemChanged(position);
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