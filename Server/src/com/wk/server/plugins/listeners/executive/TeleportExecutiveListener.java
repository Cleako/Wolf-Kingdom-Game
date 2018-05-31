package com.wk.server.plugins.listeners.executive;

import com.wk.server.model.entity.player.Player;

public interface TeleportExecutiveListener {
    /**
     * Return true to prevent teleportation (this includes ::stuck)
     * This does not include teleportations without bubbles (stairs, death, ladders etc)
     *
     * @return
     */
    public boolean blockTeleport(Player p);
}
