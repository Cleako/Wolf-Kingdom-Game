package com.legacy.server.plugins.listeners.executive;

import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;

public interface InvUseOnNpcExecutiveListener {
	
	public boolean blockInvUseOnNpc(Player player, Npc npc, Item item);

}
