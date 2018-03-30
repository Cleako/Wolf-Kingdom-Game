package com.legacy.server.plugins.listeners.action;

import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;

public interface PlayerOnNpcCombatListener {
	public void onPlayerOnNpcCombat(Player p, Npc n);
}
