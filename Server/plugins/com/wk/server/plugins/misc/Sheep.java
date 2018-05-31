package com.wk.server.plugins.misc;

import static com.wk.server.plugins.Functions.CRAFTING;
import static com.wk.server.plugins.Functions.addItem;
import static com.wk.server.plugins.Functions.random;
import static com.wk.server.plugins.Functions.showBubble;

import com.wk.server.event.custom.BatchEvent;
import com.wk.server.model.container.Item;
import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;
import com.wk.server.plugins.listeners.action.InvUseOnNpcListener;
import com.wk.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.wk.server.util.rsc.Formulae;

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