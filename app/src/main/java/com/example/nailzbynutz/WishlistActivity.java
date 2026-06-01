package com.example.nailzbynutz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Map;

public class WishlistActivity extends AppCompatActivity {

    private RecyclerView rvWishlist;
    private View layoutEmpty;
    private WishlistAdapter adapter;
    private ArrayList<WishlistItem> wishlistData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        rvWishlist = findViewById(R.id.rv_wishlist);
        layoutEmpty = findViewById(R.id.layout_empty_wishlist);

        View btnBack = findViewById(R.id.btn_back);
        if(btnBack != null) btnBack.setOnClickListener(v -> finish());

        wishlistData = new ArrayList<>();
        rvWishlist.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new WishlistAdapter(this, wishlistData);
        rvWishlist.setAdapter(adapter);

        loadWishlistLocal();
    }

    private void loadWishlistLocal() {
        SharedPreferences localWishlist = getSharedPreferences("LocalWishlist", MODE_PRIVATE);
        wishlistData.clear();

        Map<String, ?> allEntries = localWishlist.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();

            // PERBAIKAN: Abaikan key yang berupa metadata tambahan, biarkan ID utamanya diproses
            if (key.endsWith("_name") || key.endsWith("_price") || key.endsWith("_image") || key.endsWith("_isLocal")) {
                continue;
            }

            if (entry.getValue() instanceof Boolean) {
                Boolean isLiked = (Boolean) entry.getValue();
                if (isLiked) {
                    WishlistItem item = new WishlistItem();
                    item.id = key;
                    item.name = localWishlist.getString(key + "_name", key.replace("_", " "));
                    item.price = localWishlist.getString(key + "_price", "0");
                    item.imageUrl = localWishlist.getString(key + "_image", "");
                    item.isLocal = localWishlist.getBoolean(key + "_isLocal", false);
                    wishlistData.add(item);
                }
            }
        }

        // Atur Tampilan Kosong / Ada Isi
        if (wishlistData.isEmpty()) {
            if (layoutEmpty != null) layoutEmpty.setVisibility(View.VISIBLE);
            if (rvWishlist != null) rvWishlist.setVisibility(View.GONE);
        } else {
            if (layoutEmpty != null) layoutEmpty.setVisibility(View.GONE);
            if (rvWishlist != null) rvWishlist.setVisibility(View.VISIBLE);
        }

        if (adapter != null) adapter.notifyDataSetChanged();
    }
}