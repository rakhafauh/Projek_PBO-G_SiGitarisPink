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
        this.availableItems.add(new ShopItem("Onigiri", 50, 20, null));
        this.availableItems.add(new ShopItem("Makan Siang Bento", 150, 50, null));
        this.availableItems.add(new ShopItem("Gitar Pink", 5000, 0, "images/guitars/pink_guitar.png"));
        this.availableItems.add(new ShopItem("Gitar Kuning", 5000, 0, "images/guitars/yellow_guitar.png"));
        //Gatau tambah apalagi, scopenya bisa lebih luas soalnya
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

            System.out.println("Pembelian berhasil: " + item.getName());
            return true;
        } else {
            System.out.println("Uang tidak cukup untuk membeli " + item.getName());
            return false;
        }
    }

    public List<ShopItem> getAvailableItems() {

        return new ArrayList<>(availableItems);
    }
}