package com.legacy.server.plugins.listeners.action;

import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;

public interface PlayerKilledNpcListener {
    public void onPlayerKilledNpc(Player p, Npc n);
}
