package com.bocchipet.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class PlayerStats {

    private IntegerProperty sanity;
    private IntegerProperty food;
    private IntegerProperty money;

    public PlayerStats(int initialSanity, int initialFood, int initialMoney) {
        this.sanity = new SimpleIntegerProperty(initialSanity);
        this.food = new SimpleIntegerProperty(initialFood);
        this.money = new SimpleIntegerProperty(initialMoney);
    }

    public int getSanity() {
        return sanity.get();
    }
    public void setSanity(int value) {
        if (value < 0) value = 0;
        if (value > 100) value = 100;
        sanity.set(value);
    }
    public IntegerProperty sanityProperty() {
        return sanity;
    }

    public int getFood() {
        return food.get();
    }
    public void setFood(int value) {
        if (value < 0) value = 0;
        if (value > 100) value = 100;
        food.set(value);
    }
    public IntegerProperty foodProperty() {
        return food;
    }

    public int getMoney() {
        return money.get();
    }
    public void setMoney(int value) {
        if (value < 0) value = 0;
        money.set(value);
    }
    public IntegerProperty moneyProperty() {
        return money;
    }
}