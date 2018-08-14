package com.wk.server.plugins.listeners.executive;

import com.wk.server.model.entity.GroundItem;
import com.wk.server.model.entity.player.Player;

public interface PickupExecutiveListener {
    /**
     * Return true if you wish to prevent a user from picking up an item
     */
    public boolean blockPickup(Player p, GroundItem i);
}
