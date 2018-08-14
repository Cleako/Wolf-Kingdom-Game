package com.wk.server.content.market.task;

import java.util.ArrayList;

import com.wk.server.content.market.CollectableItem;
import com.wk.server.content.market.MarketDatabase;
import com.wk.server.external.EntityHandler;
import com.wk.server.external.ItemDefinition;
import com.wk.server.model.entity.player.Player;
import com.wk.server.net.rsc.ActionSender;

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
