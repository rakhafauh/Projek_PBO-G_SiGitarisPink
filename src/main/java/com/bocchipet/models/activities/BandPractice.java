package com.bocchipet.models.activities;

import com.bocchipet.models.Player;

public class BandPractice implements IActivity {

    @Override
    public void perform(Player player) {
        player.addSanity(15);
        player.addFood(-5);
        
        System.out.println("Aksi: Latihan Band. Sanity bertambah!");
    }

    @Override
    public String getAnimationImageKey() {
        return "bandPracticeImage";
    }

    @Override
    public String getSoundEffectKey() {
        return "guitarRiffSound";
    }
}