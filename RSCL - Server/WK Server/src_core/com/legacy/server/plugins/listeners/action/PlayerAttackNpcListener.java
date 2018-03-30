package com.legacy.server.plugins.listeners.action;

import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;

public interface PlayerAttackNpcListener {
	
	 public void onPlayerAttackNpc(Player p, Npc affectedmob);

}
