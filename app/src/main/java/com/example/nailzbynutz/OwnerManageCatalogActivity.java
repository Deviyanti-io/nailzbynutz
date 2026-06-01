package com.example.nailzbynutz;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class OwnerManageCatalogActivity extends AppCompatActivity {

    private DatabaseReference catalogRef;
    private RecyclerView rvCatalog;
    private CatalogAdapter adapter;
    private ArrayList<CatalogItem> catalogList;
    private SharedPreferences localPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_manage_catalog);

        localPrefs = getSharedPreferences("LocalCatalogPrefs", MODE_PRIVATE);
        findViewById(R.id.btn_back_catalog).setOnClickListener(v -> finish());
        catalogRef = FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("catalogs");

        rvCatalog = findViewById(R.id.rv_owner_catalog);
        rvCatalog.setLayoutManager(new GridLayoutManager(this, 2));

        catalogList = new ArrayList<>();
        adapter = new CatalogAdapter(this, catalogList, catalogRef);
        rvCatalog.setAdapter(adapter);

        FloatingActionButton fabAdd = findViewById(R.id.fab_add_catalog);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(OwnerManageCatalogActivity.this, AddEditCatalogActivity.class);
            startActivity(intent);
        });

        loadCatalogData();
    }

    private void loadCatalogData() {
        catalogRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                catalogList.clear();

                int[] localImages = {
                        R.drawable.nail1, R.drawable.nail2, R.drawable.nail3, R.drawable.nail4, R.drawable.nail5, R.drawable.nail6,
                        R.drawable.nail7, R.drawable.nail8, R.drawable.nail9, R.drawable.nail10, R.drawable.nail11, R.drawable.nail12,
                        R.drawable.nail13, R.drawable.nail14, R.drawable.nail15, R.drawable.nail16, R.drawable.nail17, R.drawable.nail18,
                        R.drawable.nail19, R.drawable.nail20, R.drawable.nail21, R.drawable.nail22, R.drawable.nail23, R.drawable.nail24
                };
                String[] localNames = {
                        "Midnight Blue", "Blue Petal", "Pink Aurora", "Classic French", "Pink Ombre", "Neon Star",
                        "Dark Gothic", "Glitter Glam", "Soft Pastel", "Red Velvet", "Ocean Blue", "Matte Black",
                        "Rose Gold", "Chrome Silver", "Milky White", "Lilac Dream", "Emerald Green", "Sunset Orange",
                        "Cherry Bomb", "Nude Beige", "Galaxy Swirl", "Pearl White", "Tortoise Shell", "Marble Stone"
                };
                String[] localPrices = {
                        "80000", "85000", "90000", "95000", "85000", "80000", "90000", "85000", "80000", "95000", "100000", "75000",
                        "80000", "85000", "80000", "90000", "75000", "110000", "95000", "100000", "95000", "105000", "120000", "115000"
                };

                for (int i = 0; i < localImages.length; i++) {
                    String id = "local_" + i;
                    if (!localPrefs.getBoolean("deleted_" + id, false)) {
                        catalogList.add(new CatalogItem(id, localNames[i], localPrices[i], "res_" + localImages[i], true));
                    }
                }

                for (DataSnapshot item : snapshot.getChildren()) {
                    String id = item.getKey();
                    String name = item.child("name").getValue(String.class);
                    String price = item.child("price").getValue(String.class);
                    String url = item.child("imageUrl").getValue(String.class);
                    if (price == null) price = "0";
                    catalogList.add(new CatalogItem(id, name, price, url, false));
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public static class CatalogItem {
        public String id, name, price, imageUrl;
        public boolean isLocal;
        public CatalogItem(String id, String name, String price, String imageUrl, boolean isLocal) {
            this.id = id; this.name = name; this.price = price; this.imageUrl = imageUrl; this.isLocal = isLocal;
        }
    }

    public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.ViewHolder> {
        private Activity activity;
        private ArrayList<CatalogItem> list;
        private DatabaseReference ref;

        public CatalogAdapter(Activity activity, ArrayList<CatalogItem> list, DatabaseReference ref) {
            this.activity = activity; this.list = list; this.ref = ref;
        }

        @NonNull @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(activity).inflate(R.layout.item_manage_catalog, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CatalogItem item = list.get(position);
            holder.tvName.setText(item.name);

            Locale localeID = new Locale("in", "ID");
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
            formatRupiah.setMaximumFractionDigits(0);
            try {
                holder.tvPrice.setText(formatRupiah.format(Integer.parseInt(item.price)));
            } catch (Exception e) { holder.tvPrice.setText("Rp " + item.price); }

            // FIX: Logika Cerdas Membaca Gambar
            if (item.isLocal || (item.imageUrl != null && item.imageUrl.startsWith("res_"))) {
                try {
                    int resId = Integer.parseInt(item.imageUrl.replace("res_", ""));
                    holder.imgNail.setImageResource(resId);
                } catch (Exception e) {
                    holder.imgNail.setImageResource(android.R.color.darker_gray);
                }
            } else {
                try {
                    holder.imgNail.setImageURI(Uri.parse(item.imageUrl));
                } catch (Exception e) {
                    holder.imgNail.setImageResource(android.R.color.darker_gray);
                }
            }

            // KEMBALIKAN ESTETIKA TOMBOL (HILANGKAN BIRU)
            holder.btnEdit.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            holder.btnEdit.setTextColor(android.graphics.Color.parseColor("#333333"));
            holder.btnDelete.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            holder.btnDelete.setTextColor(android.graphics.Color.parseColor("#D6001C"));

            holder.btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(activity, AddEditCatalogActivity.class);
                intent.putExtra("CATALOG_ID", item.id);
                intent.putExtra("CATALOG_NAME", item.name);
                intent.putExtra("CATALOG_PRICE", item.price);
                intent.putExtra("CATALOG_IMAGE", item.imageUrl);
                activity.startActivity(intent);
            });

            holder.btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(activity)
                        .setTitle("Hapus Katalog")
                        .setMessage("Yakin ingin menghapus desain ini?")
                        .setPositiveButton("Hapus", (dialog, which) -> {
                            if (item.id.startsWith("local_")) {
                                localPrefs.edit().putBoolean("deleted_" + item.id, true).apply();
                                list.remove(position);
                                notifyItemRemoved(position);
                            } else {
                                ref.child(item.id).removeValue();
                            }
                            Toast.makeText(activity, "Berhasil dihapus!", Toast.LENGTH_SHORT).show();
                        }).setNegativeButton("Batal", null).show();
            });
        }

        @Override public int getItemCount() { return list.size(); }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgNail; TextView tvName, tvPrice, btnEdit, btnDelete;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imgNail = itemView.findViewById(R.id.img_catalog);
                tvName = itemView.findViewById(R.id.tv_catalog_name);
                tvPrice = itemView.findViewById(R.id.tv_catalog_price);
                btnEdit = itemView.findViewById(R.id.btn_edit_catalog);
                btnDelete = itemView.findViewById(R.id.btn_delete_catalog);
            }
        }
    }
}