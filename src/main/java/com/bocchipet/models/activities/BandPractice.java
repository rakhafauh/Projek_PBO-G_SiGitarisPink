package com.bocchipet.models.activities;

import com.bocchipet.models.Player;

public class BandPractice implements IActivity {

    private int variant;

    public BandPractice(int variant) {
        this.variant = variant;
    }

    @Override
    public void perform(Player player) {
        player.addSanity(40); 
        player.addFood(-30);   
    }

    @Override
    public String getAnimationImageKey() {
        return "panggung" + variant;
    }

    @Override
    public String getSoundEffectKey() {
        return "music" + variant;
    }
}