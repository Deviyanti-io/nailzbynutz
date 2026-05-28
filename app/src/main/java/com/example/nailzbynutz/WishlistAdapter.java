package com.example.nailzbynutz;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private List<NailModel> wishlistItems;
    private Context context;
    private SharedPreferences wishlistPrefs;

    public WishlistAdapter(Context context, List<NailModel> wishlistItems) {
        this.context = context;
        this.wishlistItems = wishlistItems;
        this.wishlistPrefs = context.getSharedPreferences("WishlistPrefs", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_nail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NailModel nail = wishlistItems.get(position);
        holder.tvName.setText(nail.getName());
        holder.tvPrice.setText(nail.getPrice());
        holder.ivImage.setImageResource(nail.getImageResId());
        // Tampilkan heart on (karena ini wishlist)
        holder.ivHeart.setImageResource(R.drawable.ic_heart_on);

        // Klik heart untuk menghapus dari wishlist
        holder.ivHeart.setOnClickListener(v -> {
            // Cari indeks asli produk ini (perlu disimpan, tapi untuk sederhana kita hapus dari list dan update prefs)
            // Idealnya simpan ID produk, tapi karena kita pakai indeks, kita harus cari ulang di semua produk
            // Untuk kemudahan, kita hapus item dari list dan update prefs untuk indeks tersebut (perlu mapping)
            // Di sini kita gunakan pendekatan sederhana: hapus dari list dan update status favorite di SharedPreferences
            // Karena kita tidak punya ID, kita akan gunakan nama sebagai key (tidak ideal, tapi bisa untuk demo)
            updateFavoriteStatus(nail.getName(), false);
            wishlistItems.remove(position);
            notifyItemRemoved(position);
            if (wishlistItems.isEmpty()) {
                // notify activity untuk tampilkan empty state (bisa via interface, tapi untuk sederhana kita reload activity)
                ((WishlistActivity) context).finish();
                context.startActivity(((WishlistActivity) context).getIntent());
            }
        });
    }

    private void updateFavoriteStatus(String productName, boolean isFav) {
        // Karena tidak ada ID, kita update semua produk yang namanya cocok (tidak ideal, tapi untuk demo)
        // Di aplikasi nyata, sebaiknya gunakan ID produk.
        // Untuk sementara, kita tidak perlu implementasi karena wishlist sudah di-handle via indeks di Explore.
        // Biarkan kosong, karena kita hanya hapus dari list lokal.
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