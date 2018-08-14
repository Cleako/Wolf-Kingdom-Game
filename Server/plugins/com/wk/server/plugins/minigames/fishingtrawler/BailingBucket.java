package com.wk.server.plugins.minigames.fishingtrawler;

import static com.wk.server.plugins.Functions.sleep;

import com.wk.server.model.container.Item;
import com.wk.server.model.entity.player.Player;
import com.wk.server.model.world.World;
import com.wk.server.plugins.listeners.action.InvActionListener;
import com.wk.server.plugins.listeners.executive.InvActionExecutiveListener;

public class BailingBucket implements InvActionExecutiveListener, InvActionListener {

	@Override
	public void onInvAction(Item item, Player player) {
		if (player.isBusy())
			return;
		if (World.getWorld().getFishingTrawler().getShipAreaWater().inBounds(player.getLocation())
				|| World.getWorld().getFishingTrawler().getShipArea().inBounds(player.getLocation())) {
			player.setBusyTimer(650);
			player.message("you bail a little water...");
			sleep(650);
			World.getWorld().getFishingTrawler().bailWater();
		} else {
			// player.message("");
		}
	}

	@Override
	public boolean blockInvAction(Item item, Player player) {
		return item.getID() == 1282;
	}

}
