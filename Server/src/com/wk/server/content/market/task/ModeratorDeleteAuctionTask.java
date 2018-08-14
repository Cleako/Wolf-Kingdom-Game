package com.wk.server.content.market.task;

import com.wk.server.content.market.Market;
import com.wk.server.content.market.MarketDatabase;
import com.wk.server.content.market.MarketItem;
import com.wk.server.model.entity.player.Player;
import com.wk.server.net.rsc.ActionSender;

public class ModeratorDeleteAuctionTask extends MarketTask {

	private Player player;
	private int auctionID;

	public ModeratorDeleteAuctionTask(Player mod, int auctionID) {
		this.player = mod;
		this.auctionID = auctionID;
	}

	@Override
	public void doTask() throws Exception {
		MarketItem item = MarketDatabase.getAuctionItem(auctionID);
		if (item != null) {
			int itemIndex = item.getItemID();
			int amount = item.getAmountLeft();
			if (MarketDatabase.setSoldOut(item)) {
				MarketDatabase.addCollectableItem("Removed by " + player.getUsername(), itemIndex, amount, item.getSeller());
				ActionSender.sendBox(player, "@gre@[Auction House - Success] % @whi@ Item has been removed from Auctions. % % Returned to collections for:  " + item.getSellerName(), false);
			} else {
				ActionSender.sendBox(player, "@red@[Auction House - Error] % @whi@ Unable to remove auction", false);
			}
		}
		Market.getInstance().addRequestOpenAuctionHouseTask(player);
	}
}
