package com.wk.server.plugins.listeners.action;

import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;

public interface PlayerRangeNpcListener {
    public void onPlayerRangeNpc(Player p, Npc n);
}
