package com.bocchipet.models.events;

import com.bocchipet.models.Player;


public abstract class GameEvent {

    protected String eventMessage;

    public abstract void trigger(Player player);

    public String getMessage() {
        return eventMessage;
    }
}