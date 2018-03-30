package com.legacy.server.content.market.task;

import com.legacy.server.content.market.Market;
import com.legacy.server.content.market.MarketDatabase;
import com.legacy.server.content.market.MarketItem;
import com.legacy.server.external.EntityHandler;
import com.legacy.server.external.ItemDefinition;
import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.net.rsc.ActionSender;

public class CancelMarketItemTask extends MarketTask {

	private Player owner;
	private int auctionID;

	public CancelMarketItemTask(Player owner, final int auctionID) {
		this.owner = owner;
		this.auctionID = auctionID;
	}
	
	@Override
	public void doTask()  throws Exception {
		MarketItem item = MarketDatabase.getAuctionItem(auctionID);
		if (item != null) {
			int itemIndex = item.getItemID();
			int amount = item.getAmountLeft();
			ItemDefinition def = EntityHandler.getItemDef(itemIndex);
			if (!owner.getInventory().full() && (!def.isStackable() && owner.getInventory().size() + amount <= 30)) {
				if(MarketDatabase.cancel(item)) {
					if (!def.isStackable()) {
						for (int i = 0; i < amount; i++)
							owner.getInventory().add(new Item(itemIndex, 1));
					} else {
						owner.getInventory().add(new Item(itemIndex, amount));
					}
					ActionSender.sendBox(owner, "@gre@[Auction House - Success] % @whi@ The item has been canceled and returned to your inventory.", false);
				}
			} else if (!owner.getBank().full()) {
				if(MarketDatabase.cancel(item)) {
					owner.getBank().add(new Item(itemIndex, amount));
					ActionSender.sendBox(owner, "@gre@[Auction House - Success] % @whi@ The item has been canceled and returned to your bank. % Talk with a Banker to collect your item(s).", false);	
				}
			} else {
				ActionSender.sendBox(owner, "@red@[Auction House - Error] % @whi@ Unable to cancel auction! % % @red@Reason: @whi@No space left in your bank or inventory.", false);	
			}
		}
		Market.getInstance().addRequestOpenAuctionHouseTask(owner);
	}

}
