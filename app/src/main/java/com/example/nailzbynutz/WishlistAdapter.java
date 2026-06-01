package com.example.nailzbynutz;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private Context context;
    private List<WishlistItem> wishlistItems;
    private DatabaseReference userWishlistRef; // Digunakan untuk menghapus data di server

    public WishlistAdapter(Context context, List<WishlistItem> wishlistItems, DatabaseReference userWishlistRef) {
        this.context = context;
        this.wishlistItems = wishlistItems;
        this.userWishlistRef = userWishlistRef;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_nail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WishlistItem item = wishlistItems.get(position);

        holder.tvName.setText(item.name);
        holder.ivHeart.setImageResource(R.drawable.ic_heart_on);

        // Format Harga ke Rupiah
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        formatRupiah.setMaximumFractionDigits(0);
        try {
            holder.tvPrice.setText(formatRupiah.format(Integer.parseInt(item.price)));
        } catch (Exception e) {
            holder.tvPrice.setText("Rp " + item.price);
        }

        // Tampilkan Gambar (baik dari drawable lokal maupun URL galeri/Firebase)
        if (item.isLocal || (item.imageUrl != null && item.imageUrl.startsWith("res_"))) {
            try {
                int resId = Integer.parseInt(item.imageUrl.replace("res_", ""));
                holder.ivImage.setImageResource(resId);
            } catch (Exception e) {
                holder.ivImage.setImageResource(android.R.color.darker_gray);
            }
        } else {
            try {
                holder.ivImage.setImageURI(Uri.parse(item.imageUrl));
            } catch (Exception e) {
                holder.ivImage.setImageResource(android.R.color.darker_gray);
            }
        }

        // AKSI: Saat ikon Love diklik untuk menghapus dari Wishlist
        holder.ivHeart.setOnClickListener(v -> {
            // Hapus data langsung dari server Firebase!
            userWishlistRef.child(item.id).removeValue();
            Toast.makeText(context, item.name + " dihapus dari Wishlist", Toast.LENGTH_SHORT).show();
        });

        // AKSI: Saat item diklik, lanjutkan ke pemilihan bentuk kuku
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CustomNailShapeActivity.class);
            intent.putExtra("PRODUCT_NAME", item.name);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return wishlistItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage, ivHeart;
        TextView tvName, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_nail_image);
            ivHeart = itemView.findViewById(R.id.iv_heart);
            tvName = itemView.findViewById(R.id.tv_nail_name);
            tvPrice = itemView.findViewById(R.id.tv_nail_price);
        }
    }
}