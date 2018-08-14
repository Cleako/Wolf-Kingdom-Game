package com.wk.server.event;

import com.wk.server.model.entity.player.Player;

public abstract class ShortEvent extends SingleEvent {

    public ShortEvent(Player owner) {
        super(owner, 1500);
    }

    public abstract void action();

}