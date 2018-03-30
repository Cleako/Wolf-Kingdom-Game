package com.legacy.server.plugins.misc;

import static com.legacy.server.plugins.Functions.CRAFTING;
import static com.legacy.server.plugins.Functions.addItem;
import static com.legacy.server.plugins.Functions.random;
import static com.legacy.server.plugins.Functions.showBubble;

import com.legacy.server.event.custom.BatchEvent;
import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.InvUseOnNpcListener;
import com.legacy.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.legacy.server.util.rsc.Formulae;

public class Sheep implements InvUseOnNpcListener, InvUseOnNpcExecutiveListener {

	@Override
	public boolean blockInvUseOnNpc(Player player, Npc npc, Item item) {
		return npc.getID() == 2 && item.getID() == 144;
	}

	@Override
	public void onInvUseOnNpc(Player player, Npc npc, Item item) {
		npc.resetPath();
		
		npc.face(player);
		player.face(npc);
		showBubble(player, item);
		player.message("You attempt to shear the sheep");
		npc.setBusyTimer(1600);
		player.setBatchEvent(new BatchEvent(player, 1500, Formulae.getRepeatTimes(player, CRAFTING)) {
			@Override
			public void action() {
				npc.setBusyTimer(1600);
				if(random(0, 4) != 0) {
					player.message("You get some wool");
					addItem(player, 145, 1);
				} else {
					player.message("The sheep manages to get away from you!");
					npc.setBusyTimer(0);
					interrupt();
					return;
				}
			}
		});
	}
}