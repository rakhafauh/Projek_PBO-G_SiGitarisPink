package com.bocchipet.models.activities;

import com.bocchipet.models.Player;


public class PartTimeJob implements IActivity {

    @Override
    public void perform(Player player) {
        player.addSanity(-10);
        player.addFood(-5);
        player.addMoney(100);
        
        System.out.println("Aksi: Kerja Part-time. Uang nambah!");
    }

    @Override
    public String getAnimationImageKey() {
        return "partTimeImage";
    }

    @Override
    public String getSoundEffectKey() {
        return "cashRegisterSound";
    }
}