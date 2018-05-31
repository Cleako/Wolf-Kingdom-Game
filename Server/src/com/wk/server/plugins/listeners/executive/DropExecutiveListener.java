package com.wk.server.plugins.listeners.executive;

import com.wk.server.model.container.Item;
import com.wk.server.model.entity.player.Player;

public interface DropExecutiveListener {
    /**
     * Return true if you wish to prevent a user from dropping an item
     */
    public boolean blockDrop(Player p, Item i);
}
