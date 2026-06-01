package com.example.nailzbynutz;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private ServiceAdapter adapter;
    private ArrayList<ServiceModel> historyList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // PERBAIKAN KRUSIAL: Buat keranjang/list di awal sebelum yang lainnya!
        // Ini yang akan membunuh Error NullPointerException di Logcat Anda.
        historyList = new ArrayList<>();

        rvHistory = findViewById(R.id.rv_history);
        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        if (rvHistory != null) {
            rvHistory.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ServiceAdapter(this, historyList);
            rvHistory.setAdapter(adapter);
        }

        loadHistoryFromFirebase();
    }

    private void loadHistoryFromFirebase() {
        SharedPreferences session = getSharedPreferences("UserSession", MODE_PRIVATE);
        String currentUsername = session.getString("USER_NAME", "");

        DatabaseReference bookingsRef = FirebaseDatabase.getInstance("https://nailzbynutz-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("bookings");

        bookingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Sekarang saat fungsi ini dipanggil, historyList dijamin TIDAK NULL.
                if (historyList != null) {
                    historyList.clear();
                }

                // Dobel Loop untuk membaca struktur data baru
                for (DataSnapshot categoryFolder : snapshot.getChildren()) {
                    for (DataSnapshot data : categoryFolder.getChildren()) {

                        try {
                            String dbCustomerName = data.child("customerName").getValue(String.class);

                            // Lewati jika bukan milik user yang sedang login
                            if (dbCustomerName == null || !dbCustomerName.equals(currentUsername)) {
                                continue;
                            }

                            String serviceType = data.child("serviceType").getValue(String.class);
                            String status = data.child("status").getValue(String.class);
                            String date = data.child("date").getValue(String.class);

                            if (serviceType == null) serviceType = categoryFolder.getKey().replace("_", " ");
                            if (status == null) status = "Pending";
                            if (date == null) date = "-";

                            // Gabung Status & Tanggal ke deskripsi. Harga diset "0".
                            // Icon diset 'android.R.color.transparent'.
                            String deskripsiHistory = "Status: " + status + "\nTanggal: " + date;
                            historyList.add(new ServiceModel(serviceType, deskripsiHistory, "0", android.R.color.transparent));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (adapter != null) adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HistoryActivity.this, "Gagal memuat history", Toast.LENGTH_SHORT).show();
            }
        });
    }
}