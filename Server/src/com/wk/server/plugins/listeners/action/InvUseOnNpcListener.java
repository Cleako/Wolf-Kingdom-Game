package com.wk.server.plugins.listeners.action;

import com.wk.server.model.container.Item;
import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;

public interface InvUseOnNpcListener {
	
	public void onInvUseOnNpc(Player player, Npc npc, Item item);

}
