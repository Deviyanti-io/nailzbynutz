package com.example.nailzbynutz;

public class WishlistItem {
    public String id;
    public String name;
    public String price;
    public String imageUrl;
    public boolean isLocal;

    public WishlistItem() {
        // Constructor kosong wajib untuk Firebase
    }

    public WishlistItem(String id, String name, String price, String imageUrl, boolean isLocal) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isLocal = isLocal;
    }
}