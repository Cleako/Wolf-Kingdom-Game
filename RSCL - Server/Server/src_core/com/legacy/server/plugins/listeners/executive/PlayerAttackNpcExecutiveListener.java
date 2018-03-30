package com.legacy.server.plugins.listeners.executive;

import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;

public interface PlayerAttackNpcExecutiveListener {
	
	public boolean blockPlayerAttackNpc(Player p, Npc n);

}
