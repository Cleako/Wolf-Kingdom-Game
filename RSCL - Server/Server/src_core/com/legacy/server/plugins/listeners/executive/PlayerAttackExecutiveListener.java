package com.legacy.server.plugins.listeners.executive;

import com.legacy.server.model.entity.player.Player;

public interface PlayerAttackExecutiveListener {
    /**
     * Return true if you wish to prevent a user from attacking a mob
     */
    public boolean blockPlayerAttack(Player p, Player affectedmob);
}
