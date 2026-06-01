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
import com.bumptech.glide.Glide;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private Context context;
    private ArrayList<WishlistItem> list;

    public WishlistAdapter(Context context, ArrayList<WishlistItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Menggunakan layout item_explore yang sama
        View v = LayoutInflater.from(context).inflate(R.layout.item_explore, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WishlistItem item = list.get(position);
        holder.tvName.setText(item.name);

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        formatRupiah.setMaximumFractionDigits(0);
        try {
            holder.tvPrice.setText(formatRupiah.format(Integer.parseInt(item.price)));
        } catch (Exception e) {
            holder.tvPrice.setText("Rp " + item.price);
        }

        // Tampilkan Gambar
        if (item.isLocal || (item.imageUrl != null && item.imageUrl.startsWith("res_"))) {
            try {
                int resId = Integer.parseInt(item.imageUrl.replace("res_", ""));
                holder.imgNail.setImageResource(resId);
            } catch (Exception e) {
                holder.imgNail.setImageResource(android.R.color.darker_gray);
            }
        } else {
            Glide.with(context).load(item.imageUrl).placeholder(android.R.color.darker_gray).into(holder.imgNail);
        }

        // Set ikon hati pink menyala
        holder.btnLike.setImageResource(R.drawable.ic_heart_on);
        holder.btnLike.setColorFilter(android.graphics.Color.parseColor("#FF4081"));

        // Aksi hapus dari Wishlist
        holder.btnLike.setOnClickListener(v -> {
            SharedPreferences localWishlist = context.getSharedPreferences("LocalWishlist", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = localWishlist.edit();

            // Hapus semua data terkait kuku ini dari SharedPreferences
            editor.remove(item.id);
            editor.remove(item.id + "_name");
            editor.remove(item.id + "_price");
            editor.remove(item.id + "_image");
            editor.remove(item.id + "_isLocal");
            editor.apply();

            list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size());

            Toast.makeText(context, "Dihapus dari Wishlist", Toast.LENGTH_SHORT).show();

            // Memaksa refresh activity agar layout "Kosong" muncul jika list benar-benar habis
            if (context instanceof WishlistActivity) {
                ((WishlistActivity) context).recreate();
            }
        });

        // Aksi klik untuk pesan
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CustomNailColorActivity.class);
            intent.putExtra("SHAPE_DATA", item.name);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgNail, btnLike;
        TextView tvName, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgNail = itemView.findViewById(R.id.img_explore);
            btnLike = itemView.findViewById(R.id.btn_like_item);
            tvName = itemView.findViewById(R.id.tv_explore_name);
            tvPrice = itemView.findViewById(R.id.tv_explore_price);
        }
    }
}