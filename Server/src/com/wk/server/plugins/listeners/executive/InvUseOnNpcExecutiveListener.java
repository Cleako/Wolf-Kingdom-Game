package com.wk.server.plugins.listeners.executive;

import com.wk.server.model.container.Item;
import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;

public interface InvUseOnNpcExecutiveListener {
	
	public boolean blockInvUseOnNpc(Player player, Npc npc, Item item);

}
