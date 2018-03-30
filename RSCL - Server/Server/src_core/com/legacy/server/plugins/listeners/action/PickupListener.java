package com.legacy.server.plugins.listeners.action;

import com.legacy.server.model.entity.GroundItem;
import com.legacy.server.model.entity.player.Player;

public interface PickupListener {
    /**
     * Called when a user picks up an item
     */
    public void onPickup(Player p, GroundItem i);
}
