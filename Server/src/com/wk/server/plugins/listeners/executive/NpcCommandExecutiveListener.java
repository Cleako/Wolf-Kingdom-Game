package com.wk.server.plugins.listeners.executive;

import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;

public interface NpcCommandExecutiveListener {
	
	public boolean blockNpcCommand(Npc n, String command, Player p);

}
