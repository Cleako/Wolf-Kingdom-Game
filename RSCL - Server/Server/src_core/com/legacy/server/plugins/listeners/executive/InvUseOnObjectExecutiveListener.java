package com.legacy.server.plugins.listeners.executive;

import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.player.Player;

public interface InvUseOnObjectExecutiveListener {
    /**
     * Return true to prevent a user when he uses an inventory item on an game object
     */
    public boolean blockInvUseOnObject(GameObject obj, Item item, Player player);
}
