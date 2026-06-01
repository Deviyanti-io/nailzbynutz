package com.example.nailzbynutz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WishlistActivity extends AppCompatActivity {

    private RecyclerView rvWishlist;
    private WishlistAdapter adapter;
    private LinearLayout emptyLayout;

    private List<WishlistItem> wishlistItems;
    private DatabaseReference userWishlistRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        rvWishlist = findViewById(R.id.rv_wishlist);
        emptyLayout = findViewById(R.id.layout_empty_state);
        rvWishlist.setLayoutManager(new LinearLayoutManager(this));

        wishlistItems = new ArrayList<>();

        // 1. Ambil username yang sedang login
        SharedPreferences session = getSharedPreferences("UserSession", MODE_PRIVATE);
        String currentUsername = session.getString("USER_NAME", "Guest");

        // 2. Arahkan ke database wishlist milik user tersebut
        userWishlistRef = FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users").child(currentUsername).child("wishlist");

        // 3. Pasang Adapter dan kirim referensi Firebase-nya agar Adapter bisa menghapus data
        adapter = new WishlistAdapter(this, wishlistItems, userWishlistRef);
        rvWishlist.setAdapter(adapter);

        // 4. Tarik data dari server
        loadWishlistFromFirebase();

        findViewById(R.id.btn_back_wishlist).setOnClickListener(v -> finish());
    }

    private void loadWishlistFromFirebase() {
        // addValueEventListener akan membuat daftar ini update secara otomatis (real-time)
        userWishlistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                wishlistItems.clear(); // Bersihkan daftar lama

                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String id = data.getKey();
                        String name = data.child("name").getValue(String.class);
                        String price = data.child("price").getValue(String.class);
                        String imageUrl = data.child("imageUrl").getValue(String.class);

                        // Menangani tipe boolean dengan aman
                        Boolean isLocalObj = data.child("isLocal").getValue(Boolean.class);
                        boolean isLocal = (isLocalObj != null) ? isLocalObj : false;

                        wishlistItems.add(new WishlistItem(id, name, price, imageUrl, isLocal));
                    }
                }

                adapter.notifyDataSetChanged(); // Beritahu adapter ada data baru

                // Atur tampilan kosong
                if (wishlistItems.isEmpty()) {
                    emptyLayout.setVisibility(View.VISIBLE);
                    rvWishlist.setVisibility(View.GONE);
                } else {
                    emptyLayout.setVisibility(View.GONE);
                    rvWishlist.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}