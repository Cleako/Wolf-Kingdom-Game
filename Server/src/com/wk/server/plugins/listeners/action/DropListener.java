package com.wk.server.plugins.listeners.action;

import com.wk.server.model.container.Item;
import com.wk.server.model.entity.player.Player;

public interface DropListener {
    /**
     * Called when a user drops an item
     */
    public void onDrop(Player p, Item i);
}
