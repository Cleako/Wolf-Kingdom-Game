package com.legacy.server.plugins.listeners.action;

import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.player.Player;

public interface DropListener {
    /**
     * Called when a user drops an item
     */
    public void onDrop(Player p, Item i);
}
