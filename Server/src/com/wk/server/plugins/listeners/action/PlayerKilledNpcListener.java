package com.wk.server.plugins.listeners.action;

import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;

public interface PlayerKilledNpcListener {
    public void onPlayerKilledNpc(Player p, Npc n);
}
