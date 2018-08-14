package com.wk.server.plugins.listeners.executive;

import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;

public interface PlayerAttackNpcExecutiveListener {
	
	public boolean blockPlayerAttackNpc(Player p, Npc n);

}
