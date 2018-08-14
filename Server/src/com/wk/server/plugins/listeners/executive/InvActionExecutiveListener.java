package com.wk.server.plugins.listeners.executive;

import com.wk.server.model.container.Item;
import com.wk.server.model.entity.player.Player;

public interface InvActionExecutiveListener {
    /**
     * Return true to prevent inventory action
     */
    public boolean blockInvAction(Item item, Player player);
}
