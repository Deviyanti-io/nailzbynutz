package com.example.nailzbynutz;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class WishlistActivity extends AppCompatActivity {

    private RecyclerView rvWishlist;
    private LinearLayout layoutEmpty;
    private ImageView btnBack;
    private ExploreAdapter adapter;
    private List<NailModel> wishlistItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        rvWishlist = findViewById(R.id.rv_wishlist);
        layoutEmpty = findViewById(R.id.layout_empty_state);
        btnBack = findViewById(R.id.btn_back_wishlist);

        // Load produk yang difavoritkan dari SharedPreferences
        loadWishlistItems();

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        rvWishlist.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ExploreAdapter(this, wishlistItems);
        rvWishlist.setAdapter(adapter);

        // Tampilkan empty state jika kosong
        if (wishlistItems.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvWishlist.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvWishlist.setVisibility(View.VISIBLE);
        }
    }

    private void loadWishlistItems() {

    }
}