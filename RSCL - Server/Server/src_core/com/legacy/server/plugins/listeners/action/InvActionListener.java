package com.legacy.server.plugins.listeners.action;

import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.player.Player;

/**
 * Interface for handling Inv Actions
 *
 * @author Peeter.tomberg
 */
public interface InvActionListener {

    /**
     * Called when a user performs an inventory action
     *
     * @param item
     * @param player
     */
    public void onInvAction(Item item, Player player);
}
