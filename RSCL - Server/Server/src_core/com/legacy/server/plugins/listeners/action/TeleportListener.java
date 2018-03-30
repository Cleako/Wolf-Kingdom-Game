package com.legacy.server.plugins.listeners.action;

import com.legacy.server.model.entity.player.Player;

public interface TeleportListener {

    /**
     * Called when a user teleports (includes ::stuck)
     * This does not include teleportations without bubbles (stairs, death, ladders etc)
     */
    public void onTeleport(Player p);
}
