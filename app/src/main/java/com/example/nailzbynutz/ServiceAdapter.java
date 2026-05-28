package com.example.nailzbynutz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {

    private List<ServiceModel> serviceList;

    public ServiceAdapter(List<ServiceModel> serviceList) {
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Menghubungkan layout satu baris item jasa (item_service.xml) ke dalam daftar
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceModel service = serviceList.get(position);

        holder.tvTitle.setText(service.getTitle());
        holder.tvDescription.setText(service.getDescription());
        holder.ivIcon.setImageResource(service.getIconResId());

        // Mengubah angka nominal biasa menjadi format mata uang Rupiah secara otomatis (Contoh: Rp45.000)
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        formatter.setMaximumFractionDigits(0); // Menghilangkan nilai desimal ,00 di belakang harga
        String formattedPrice = formatter.format(service.getPrice());
        holder.tvPrice.setText(formattedPrice);
    }

    @Override
    public int getItemCount() {
        return serviceList.size(); // Total list layanan salon yang dimuat
    }

    // Menghubungkan id komponen XML item_service ke variabel Java
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvDescription, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.service_icon);
            tvTitle = itemView.findViewById(R.id.service_title);
            tvDescription = itemView.findViewById(R.id.service_description);
            tvPrice = itemView.findViewById(R.id.service_price);
        }
    }
}