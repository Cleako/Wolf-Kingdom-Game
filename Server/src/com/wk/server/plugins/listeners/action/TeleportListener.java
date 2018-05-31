package com.wk.server.plugins.listeners.action;

import com.wk.server.model.entity.player.Player;

public interface TeleportListener {

    /**
     * Called when a user teleports (includes ::stuck)
     * This does not include teleportations without bubbles (stairs, death, ladders etc)
     */
    public void onTeleport(Player p);
}
