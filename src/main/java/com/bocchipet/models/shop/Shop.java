package com.bocchipet.models.shop;

import com.bocchipet.models.Player;
import java.util.ArrayList;
import java.util.List;

public class Shop {

    private List<ShopItem> availableItems;

    public Shop() {
        this.availableItems = new ArrayList<>();
        initializeStock();
    }

    private void initializeStock() {
        // Index 0
        this.availableItems.add(new ShopItem("Onigiri", 50, 25, null));
        
        // Index 1
        this.availableItems.add(new ShopItem("Bento", 150, 60, null));
        
        // Index 2
        this.availableItems.add(new ShopItem("Energy Drink", 100, 10, null));

        // Index 3
        this.availableItems.add(new ShopItem("Sanity Pill", 300, 0, null));

        // Index 4
        this.availableItems.add(new ShopItem("Pink Guitar", 18000, 0, "images/guitars/pink_guitar.png"));
        
        // Index 5
        this.availableItems.add(new ShopItem("Yellow Guitar", 20000, 0, "images/guitars/yellow_guitar.png"));
    }

    public boolean purchaseItem(Player player, ShopItem item) {
        if (player.getMoney() >= item.getPrice()) {
            player.addMoney(-item.getPrice());

            if (item.isFood()) {
                player.addFood(item.getFoodBonus());
            }
            if (item.isGuitar()) {
                player.setCurrentGuitarImage(item.getGuitarImageId());
            }

            if (item.getName().equals("Energy Drink")) {
                player.addSanity(15);
            }
            if (item.getName().equals("Sanity Pill")) {
                player.addSanity(50); 
            }

            System.out.println("Pembelian berhasil: " + item.getName());
            return true;
        } else {
            System.out.println("Uang tidak cukup!");
            return false;
        }
    }

    public List<ShopItem> getAvailableItems() {
        return new ArrayList<>(availableItems);
    }
}