package com.wk.server.plugins.listeners.action;

import com.wk.server.model.container.Item;
import com.wk.server.model.entity.GameObject;
import com.wk.server.model.entity.player.Player;

public interface InvUseOnObjectListener {

    /**
     * Called when a user uses an inventory item on an game object
     */
    public void onInvUseOnObject(GameObject obj, Item item, Player player);
}
