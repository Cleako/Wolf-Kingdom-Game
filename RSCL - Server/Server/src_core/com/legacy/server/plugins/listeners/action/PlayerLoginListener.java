package com.legacy.server.plugins.listeners.action;

import com.legacy.server.model.entity.player.Player;

/**
 * Interface for handling player logins
 */
public interface PlayerLoginListener {
    /**
     * Called when player logins
     */
    public void onPlayerLogin(Player player);
}
