package com.example.nailzbynutz;

public class ServiceModel {
    private String name;
    private String description;
    private int price;
    private int imageResId;

    public ServiceModel(String name, String description, String price, int imageResId) {
        this.name = name;
        this.description = description;
        this.price = Integer.parseInt(price);
        this.imageResId = imageResId;
    }

    public ServiceModel(String basicPedicure, String description, int i, int icNailShape) {

    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
    public int getImageResId() { return imageResId; }
}