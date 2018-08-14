package com.wk.server.plugins.listeners.action;

import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;

public interface NpcCommandListener {

	public void onNpcCommand(Npc n, String command, Player p);
	
}
