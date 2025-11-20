package com.bocchipet.models;

public class PlayerDataDTO {
    
    private int sanity;
    private int food;
    private int money;
    private String currentGuitarImage;

    public PlayerDataDTO() {}

    // Getters dan Setters
    // Diperluin ama Gson buat ngisi data pas loading

    public int getSanity() {
        return sanity;
    }

    public void setSanity(int sanity) {
        this.sanity = sanity;
    }

    public int getFood() {
        return food;
    }

    public void setFood(int food) {
        this.food = food;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getCurrentGuitarImage() {
        return currentGuitarImage;
    }

    public void setCurrentGuitarImage(String currentGuitarImage) {
        this.currentGuitarImage = currentGuitarImage;
    }
}