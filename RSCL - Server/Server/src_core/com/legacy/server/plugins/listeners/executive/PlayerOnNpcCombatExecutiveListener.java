package com.legacy.server.plugins.listeners.executive;

import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;

public interface PlayerOnNpcCombatExecutiveListener {

    /**
     * Return true if you wish to prevent a user from ranging a player
     */
    public boolean blockPlayerOnNpcCombat(Player p, Npc n);
}
