package com.wk.server.plugins.listeners.action;

import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;

public interface PlayerOnNpcCombatListener {
	public void onPlayerOnNpcCombat(Player p, Npc n);
}
