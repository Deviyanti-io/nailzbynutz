package com.example.nailzbynutz;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {

    private List<ServiceModel> serviceList;
    private android.content.Context context;

    public ServiceAdapter(List<ServiceModel> serviceList) {
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceModel service = serviceList.get(position);
        holder.tvName.setText(service.getName());
        holder.tvDesc.setText(service.getDescription());
        holder.tvPrice.setText("Mulai dari Rp " + service.getPrice());
        holder.ivImage.setImageResource(service.getImageResId());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookingAppointmentActivity.class);
            intent.putExtra("FINAL_SHAPE", service.getName());
            intent.putExtra("FINAL_LENGTH", "Standard");
            intent.putExtra("FINAL_COLOR_TYPE", "Service");
            intent.putExtra("FINAL_COLOR", "#7E8DBB");
            intent.putExtra("FINAL_FINISH", "Standard");
            intent.putExtra("FINAL_SIZE", "Standard");
            intent.putExtra("FINAL_NOTES", "Layanan: " + service.getName() + " - Rp " + service.getPrice());
            intent.putStringArrayListExtra("FINAL_ADDONS", new ArrayList<>());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvDesc, tvPrice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_service_image);
            tvName = itemView.findViewById(R.id.tv_service_name);
            tvDesc = itemView.findViewById(R.id.tv_service_desc);
            tvPrice = itemView.findViewById(R.id.tv_service_price);
        }
    }
}