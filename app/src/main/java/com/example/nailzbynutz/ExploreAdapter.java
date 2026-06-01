package com.example.nailzbynutz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ViewHolder> {

    private Context context;
    private List<NailModel> nailList;

    public ExploreAdapter(Context context, List<NailModel> nailList) {
        this.context = context;
        this.nailList = nailList;
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

        // Membaca status Wishlist langsung dari memori lokal (SharedPreferences)
        SharedPreferences localWishlist = context.getSharedPreferences("LocalWishlist", Context.MODE_PRIVATE);
        String safeId = nail.getName().replace(" ", "_");
        boolean isLikedLocally = localWishlist.getBoolean(safeId, false);

        nail.setFavorite(isLikedLocally);

        if (isLikedLocally) {
            holder.ivHeart.setImageResource(R.drawable.ic_heart_on);
        } else {
            holder.ivHeart.setImageResource(R.drawable.ic_heart_off);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CustomNailShapeActivity.class);
            intent.putExtra("PRODUCT_NAME", nail.getName());
            context.startActivity(intent);
        });

        holder.ivHeart.setOnClickListener(v -> {
            try {
                boolean currentStatus = nail.isFavorite();
                boolean newStatus = !currentStatus;
                nail.setFavorite(newStatus);

                SharedPreferences.Editor editor = localWishlist.edit();

                if (newStatus) {
                    editor.putBoolean(safeId, true);
                    editor.putString(safeId + "_name", nail.getName());

                    // Mengekstrak angka saja dari harga untuk menghindari error NumberFormat di WishlistAdapter
                    String cleanPrice = nail.getPrice().replaceAll("[^0-9]", "");
                    if(cleanPrice.isEmpty()) cleanPrice = "0";

                    editor.putString(safeId + "_price", cleanPrice);
                    editor.putString(safeId + "_image", "res_" + nail.getImageResId());
                    editor.putBoolean(safeId + "_isLocal", true);

                    Toast.makeText(context, nail.getName() + " ditambahkan ke Wishlist ❤️", Toast.LENGTH_SHORT).show();
                } else {
                    editor.remove(safeId);
                    editor.remove(safeId + "_name");
                    editor.remove(safeId + "_price");
                    editor.remove(safeId + "_image");
                    editor.remove(safeId + "_isLocal");

                    Toast.makeText(context, nail.getName() + " dihapus dari Wishlist", Toast.LENGTH_SHORT).show();
                }
                editor.apply();
                notifyItemChanged(position);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
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