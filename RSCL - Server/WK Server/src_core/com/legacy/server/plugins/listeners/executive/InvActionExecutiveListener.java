package com.legacy.server.plugins.listeners.executive;

import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.player.Player;

public interface InvActionExecutiveListener {
    /**
     * Return true to prevent inventory action
     */
    public boolean blockInvAction(Item item, Player player);
}
