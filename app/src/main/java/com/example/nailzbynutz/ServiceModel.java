package com.example.nailzbynutz;

public class ServiceModel {
    private String title;
    private String description;
    private int price;
    private int iconResId;

    // --- TAMBAHAN VARIABEL UNTUK HISTORY ---
    private String status;
    private String date;

    // Konstruktor untuk inisialisasi data katalog jasa salon (Asli)
    public ServiceModel(String title, String description, String price, int iconResId) {
        this.title = title;
        this.description = description;
        this.price = Integer.parseInt(price);
        this.iconResId = iconResId;
    }

    public ServiceModel(String nailArt, String desainKukuSesuaiPermintaan, int i, int icTopPolish) {
        this.title = nailArt;
        this.description = desainKukuSesuaiPermintaan;
        this.price = i;
        this.iconResId = icTopPolish;
    }

    // --- KONSTRUKTOR BARU KHUSUS UNTUK HISTORY ---
    public ServiceModel(String title, String status, String date) {
        this.title = title;
        this.status = status;
        this.date = date;
    }

    // --- Getter dan Setter Asli ---

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public int getIconResId() {
        return iconResId;
    }

    public int getImageResId() {
        return 0;
    }

    public CharSequence getName() {
        return null;
    }

    // --- GETTER BARU UNTUK HISTORY ---
    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }
}