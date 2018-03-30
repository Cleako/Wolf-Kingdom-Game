package com.legacy.server.content.market.task;

import java.util.ArrayList;

import com.legacy.server.content.market.CollectableItem;
import com.legacy.server.content.market.MarketDatabase;
import com.legacy.server.external.EntityHandler;
import com.legacy.server.external.ItemDefinition;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.net.rsc.ActionSender;

public class CollectableItemsNotificationTask extends MarketTask {
	
	private Player player;

	public CollectableItemsNotificationTask(Player player) {
		this.player = player;
	}

	@Override
	public void doTask() throws Exception {
		ArrayList<CollectableItem> list = MarketDatabase.getCollectableItemsFor(player.getDatabaseID());
		String items = "Following items have been removed from market: % ";
		for (CollectableItem item : list) {
			ItemDefinition def = EntityHandler.getItemDef(item.item_id);
			items += " @lre@" + def.getName() + " @whi@x @cya@" + item.item_amount + " " + item.explanation + "@whi@ %";
		}
		items += "@gre@You can claim them back from Auctioneer";

		if (list.size() == 0) {
			return;
		}
		ActionSender.sendBox(player, items, true);
	}
	

}
