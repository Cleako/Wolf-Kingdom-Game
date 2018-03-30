package com.legacy.server.plugins.listeners.action;

import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;

public interface InvUseOnNpcListener {
	
	public void onInvUseOnNpc(Player player, Npc npc, Item item);

}
