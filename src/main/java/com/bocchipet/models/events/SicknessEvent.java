package com.bocchipet.models.events;

import com.bocchipet.models.Player;

public class SicknessEvent extends GameEvent {

    public SicknessEvent() {
        this.eventMessage = "Bocchi sakit! Sanitynya ngurang...";
    }

    @Override
    public void trigger(Player player) {
        player.addSanity(-30);
        System.out.println(this.eventMessage);
    }
}