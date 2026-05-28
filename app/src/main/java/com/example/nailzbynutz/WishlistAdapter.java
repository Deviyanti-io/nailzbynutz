package com.example.nailzbynutz;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private Context context;
    private List<NailModel> wishlistItems;
    private OnItemRemovedListener onItemRemovedListener;

    public interface OnItemRemovedListener {
        void onItemRemoved(int newSize);
    }

    public void setOnItemRemovedListener(OnItemRemovedListener listener) {
        this.onItemRemovedListener = listener;
    }

    public WishlistAdapter(Context context, List<NailModel> wishlistItems) {
        this.context = context;
        this.wishlistItems = wishlistItems;
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
        holder.ivHeart.setImageResource(R.drawable.ic_heart_on);

        // Tombol hapus dari wishlist
        holder.ivHeart.setOnClickListener(v -> {
            nail.setFavorite(false);
            wishlistItems.remove(position);
            NailModel.globalWishlist.remove(nail);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, wishlistItems.size());
            Toast.makeText(context, nail.getName() + " dihapus dari Wishlist", Toast.LENGTH_SHORT).show();
            if (onItemRemovedListener != null) {
                onItemRemovedListener.onItemRemoved(wishlistItems.size());
            }
        });

        // Klik item -> pindah ke CustomNailShapeActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CustomNailShapeActivity.class);
            intent.putExtra("PRODUCT_NAME", nail.getName());
            // Tidak ada flag aneh, start activity biasa
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