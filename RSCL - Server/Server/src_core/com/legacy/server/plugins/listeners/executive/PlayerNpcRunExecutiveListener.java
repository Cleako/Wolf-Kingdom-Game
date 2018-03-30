package com.legacy.server.plugins.listeners.executive;

import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;

public interface PlayerNpcRunExecutiveListener {
	  /**
     * Return true if you wish to prevent a user from running from npc
     */
	public boolean blockPlayerNpcRun(Player p, Npc n);
}
