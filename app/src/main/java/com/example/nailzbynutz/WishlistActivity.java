package com.example.nailzbynutz;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WishlistActivity extends AppCompatActivity {

    private RecyclerView rvWishlist;
    private WishlistAdapter adapter;
    private LinearLayout emptyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        rvWishlist = findViewById(R.id.rv_wishlist);
        emptyLayout = findViewById(R.id.layout_empty_state);

        rvWishlist.setLayoutManager(new LinearLayoutManager(this));

        setupWishlist();

        findViewById(R.id.btn_back_wishlist).setOnClickListener(v -> finish());
    }

    private void setupWishlist() {
        if (NailModel.globalWishlist == null || NailModel.globalWishlist.isEmpty()) {
            emptyLayout.setVisibility(View.VISIBLE);
            rvWishlist.setVisibility(View.GONE);
        } else {
            emptyLayout.setVisibility(View.GONE);
            rvWishlist.setVisibility(View.VISIBLE);
            adapter = new WishlistAdapter(this, NailModel.globalWishlist);
            adapter.setOnItemRemovedListener(newSize -> {
                if (newSize == 0) {
                    emptyLayout.setVisibility(View.VISIBLE);
                    rvWishlist.setVisibility(View.GONE);
                }
            });
            rvWishlist.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh adapter jika ada perubahan data dari luar (misal setelah order)
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        // Update empty state jika wishlist berubah
        setupWishlist();
    }
}