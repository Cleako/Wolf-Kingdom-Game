package com.wk.server.plugins.listeners.executive;

import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;

public interface PlayerKilledNpcExecutiveListener {
    /**
     * Return true to prevent the default action on a npcs death (no loot) //yeah. well. i still think it should control whole death.
     */
    public boolean blockPlayerKilledNpc(Player p, Npc n);
}
