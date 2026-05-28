package com.example.nailzbynutz;

import java.util.ArrayList;
import java.util.List;

public class NailModel {
    private String name;
    private String price;
    private int imageResId;
    private boolean isFavorite;

    // Database wishlist global
    public static List<NailModel> globalWishlist = new ArrayList<>();

    // Constructor tanpa status favorite
    public NailModel(String name, String price, int imageResId) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.isFavorite = false;
    }

    // Constructor dengan status favorite
    public NailModel(String name, String price, int imageResId, boolean isFavorite) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.isFavorite = isFavorite;
    }

    // Getter dan Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        this.isFavorite = favorite;
    }
}