package com.legacy.server.plugins.listeners.executive;

import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.player.Player;

public interface DropExecutiveListener {
    /**
     * Return true if you wish to prevent a user from dropping an item
     */
    public boolean blockDrop(Player p, Item i);
}
