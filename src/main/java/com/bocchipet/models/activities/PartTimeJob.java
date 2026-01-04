package com.bocchipet.models.activities;

import com.bocchipet.models.Player;


public class PartTimeJob implements IActivity {

    @Override
    public void perform(Player player) {
        player.addSanity(-30);
        player.addFood(-25);
        player.addMoney(250);
        
        System.out.println("Activity: Kerja Part-time. Uangnya nambah");
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