package com.legacy.server.plugins.listeners.action;

import com.legacy.server.model.entity.player.Player;

public interface PlayerDeathListener {
    /**
     * Called on a players death
     *
     * @param p
     */
    public void onPlayerDeath(Player p);

}
