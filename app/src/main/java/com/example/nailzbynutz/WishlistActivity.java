package com.example.nailzbynutz;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WishlistActivity extends AppCompatActivity {

    private RecyclerView rvWishlist;
    private LinearLayout layoutEmpty;
    private ImageView btnBack;
    private ExploreAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        rvWishlist = findViewById(R.id.rv_wishlist);
        layoutEmpty = findViewById(R.id.layout_empty_state);
        btnBack = findViewById(R.id.btn_back_wishlist);

        btnBack.setOnClickListener(v -> finish());

        rvWishlist.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ExploreAdapter(this, NailModel.globalWishlist);
        rvWishlist.setAdapter(adapter);

        updateEmptyState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (NailModel.globalWishlist.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvWishlist.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvWishlist.setVisibility(View.VISIBLE);
        }
    }
}