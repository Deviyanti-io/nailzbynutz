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

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ViewHolder> {

    private List<NailModel> nailList;
    private Context context;
    private SharedPreferences wishlistPrefs;

    public ExploreAdapter(Context context, List<NailModel> nailList) {
        this.context = context;
        this.nailList = nailList;
        this.wishlistPrefs = context.getSharedPreferences("WishlistPrefs", Context.MODE_PRIVATE);
        loadFavoriteStatusFromPrefs();
    }
    public void updateList(List<NailModel> newList) {
        this.nailList = newList;
        notifyDataSetChanged();
    }
    private void loadFavoriteStatusFromPrefs() {
        for (int i = 0; i < nailList.size(); i++) {
            boolean isFav = wishlistPrefs.getBoolean("fav_" + i, false);
            nailList.get(i).setFavorite(isFav);
        }
    }

    private void saveFavoriteStatus(int position, boolean isFav) {
        wishlistPrefs.edit().putBoolean("fav_" + position, isFav).apply();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_nail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NailModel nail = nailList.get(position);
        holder.tvName.setText(nail.getName());
        holder.tvPrice.setText(nail.getPrice());
        holder.ivImage.setImageResource(nail.getImageResId());

        // Set icon heart sesuai status favorit
        if (nail.isFavorite()) {
            holder.ivHeart.setImageResource(R.drawable.ic_heart_on);
        } else {
            holder.ivHeart.setImageResource(R.drawable.ic_heart_off);
        }

        // Klik heart untuk toggle favorit
        holder.ivHeart.setOnClickListener(v -> {
            boolean newStatus = !nail.isFavorite();
            nail.setFavorite(newStatus);
            saveFavoriteStatus(position, newStatus);
            notifyItemChanged(position);
        });

        // Klik item untuk pindah ke halaman detail / custom nail
        holder.itemView.setOnClickListener(v -> {
            // Bisa pindah ke CustomNailShapeActivity dengan membawa data nama & harga
            // Tergantung flow aplikasi
        });
    }

    @Override
    public int getItemCount() {
        return nailList.size();
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