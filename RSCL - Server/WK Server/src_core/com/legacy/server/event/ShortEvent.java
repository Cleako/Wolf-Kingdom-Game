package com.legacy.server.event;

import com.legacy.server.model.entity.player.Player;

public abstract class ShortEvent extends SingleEvent {

    public ShortEvent(Player owner) {
        super(owner, 1500);
    }

    public abstract void action();

}