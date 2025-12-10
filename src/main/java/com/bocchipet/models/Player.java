package com.bocchipet.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Player {

    private PlayerStats stats;
    private StringProperty currentGuitarImage;
    private String name; 

    public Player() {
        this.stats = new PlayerStats(100, 100, 1000); 
        this.currentGuitarImage = new SimpleStringProperty("images/guitars/default_guitar.png");
        this.name = "Bocchi"; // Default name
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public IntegerProperty sanityProperty() { return this.stats.sanityProperty(); }
    public int getSanity() { return this.stats.getSanity(); }
    public void addSanity(int amount) { this.stats.setSanity(this.stats.getSanity() + amount); }

    public IntegerProperty foodProperty() { return this.stats.foodProperty(); }
    public int getFood() { return this.stats.getFood(); }
    public void addFood(int amount) { this.stats.setFood(this.stats.getFood() + amount); }

    public IntegerProperty moneyProperty() { return this.stats.moneyProperty(); }
    public int getMoney() { return this.stats.getMoney(); }
    public void addMoney(int amount) { this.stats.setMoney(this.stats.getMoney() + amount); }

    public StringProperty currentGuitarImageProperty() { return this.currentGuitarImage; }
    public String getCurrentGuitarImage() { return this.currentGuitarImage.get(); }
    public void setCurrentGuitarImage(String imagePath) { this.currentGuitarImage.set(imagePath); }
}