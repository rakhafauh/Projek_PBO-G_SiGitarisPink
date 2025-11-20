package com.bocchipet.models.shop;


public class ShopItem {

    private String name;
    private int price;
    private int foodBonus;
    private String guitarImageId;


    public ShopItem(String name, int price, int foodBonus, String guitarImageId) {
        this.name = name;
        this.price = price;
        this.foodBonus = foodBonus;
        this.guitarImageId = guitarImageId;
    }

    // Cuman butuh getter, karena itemnya "read-only"

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getFoodBonus() {
        return foodBonus;
    }

    public String getGuitarImageId() {
        return guitarImageId;
    }

    public boolean isFood() {
        return foodBonus > 0;
    }

    public boolean isGuitar() {
        return guitarImageId != null;
    }
}