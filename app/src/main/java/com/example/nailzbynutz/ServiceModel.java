package com.example.nailzbynutz;

public class ServiceModel {
    private String title;
    private String description;
    private int price;
    private int iconResId;

    // Konstruktor untuk inisialisasi data katalog jasa salon
    public ServiceModel(String title, String description, String price, int iconResId) {
        this.title = title;
        this.description = description;
        this.price = Integer.parseInt(price);
        this.iconResId = iconResId;
    }

    public ServiceModel(String nailArt, String desainKukuSesuaiPermintaan, int i, int icTopPolish) {

    }

    // --- Getter dan Setter ---

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
}