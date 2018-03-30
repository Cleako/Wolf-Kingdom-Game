package com.legacy.server.plugins.listeners.action;

import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.player.Player;

public interface InvUseOnWallObjectListener {

    /**
     * Called when a user uses an inventory item on an game object
     */
    public void onInvUseOnWallObject(GameObject obj, Item item, Player player);
}
