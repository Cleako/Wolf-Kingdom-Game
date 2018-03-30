package com.legacy.server.plugins.skills;

import static com.legacy.server.plugins.Functions.*;

import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.InvUseOnNpcListener;
import com.legacy.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;

public class Cow implements InvUseOnNpcListener, InvUseOnNpcExecutiveListener{

	@Override
	public boolean blockInvUseOnNpc(Player player, Npc npc, Item item) {
		return npc.getID() == 217 && item.getID() == 21 || npc.getID() == 6 && item.getID() == 21;
	}

	@Override
	public void onInvUseOnNpc(Player player, Npc npc, Item item) {
		npc.resetPath();
		npc.face(player);
		npc.setBusy(true);
		showBubble(player, item);
		if (removeItem(player, item.getID(), 1)) {
			addItem(player, 22, 1);
		}
		message(player, 3500, "You milk the cow");
		npc.setBusy(false);
	}
}