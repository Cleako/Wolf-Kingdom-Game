package com.wk.server.plugins.listeners.executive;

import com.wk.server.model.entity.player.Player;

public interface PlayerRangeExecutiveListener {

    /**
     * Return true if you wish to prevent a user from ranging a player
     */
    public boolean blockPlayerRange(Player p, Player affectedMob);
}
