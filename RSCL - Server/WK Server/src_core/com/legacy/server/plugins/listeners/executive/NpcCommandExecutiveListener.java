package com.legacy.server.plugins.listeners.executive;

import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;

public interface NpcCommandExecutiveListener {
	
	public boolean blockNpcCommand(Npc n, String command, Player p);

}
