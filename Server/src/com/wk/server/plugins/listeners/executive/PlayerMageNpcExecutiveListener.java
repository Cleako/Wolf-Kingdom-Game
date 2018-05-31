package com.wk.server.plugins.listeners.executive;

import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;

public interface PlayerMageNpcExecutiveListener {

    /**
     * Return true if you wish to prevent a user from ranging a player
     */
    public boolean blockPlayerMageNpc(Player p, Npc n);
}
