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
        syncGlobalWishlist();
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

    private void syncGlobalWishlist() {
        NailModel.globalWishlist.clear();
        for (NailModel nail : nailList) {
            if (nail.isFavorite()) {
                NailModel.globalWishlist.add(nail);
            }
        }
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

        if (nail.isFavorite()) {
            holder.ivHeart.setImageResource(R.drawable.ic_heart_on);
        } else {
            holder.ivHeart.setImageResource(R.drawable.ic_heart_off);
        }

        holder.ivHeart.setOnClickListener(v -> {
            boolean newStatus = !nail.isFavorite();
            nail.setFavorite(newStatus);
            saveFavoriteStatus(position, newStatus);
            if (newStatus) {
                if (!NailModel.globalWishlist.contains(nail)) {
                    NailModel.globalWishlist.add(nail);
                }
            } else {
                NailModel.globalWishlist.remove(nail);
            }
            notifyItemChanged(position);
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