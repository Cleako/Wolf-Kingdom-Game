package com.legacy.server.plugins.listeners.action;

import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;

public interface NpcCommandListener {

	public void onNpcCommand(Npc n, String command, Player p);
	
}
