package com.legacy.server.content.market.task;

import com.legacy.server.content.market.Market;
import com.legacy.server.content.market.MarketDatabase;
import com.legacy.server.content.market.MarketItem;
import com.legacy.server.external.EntityHandler;
import com.legacy.server.external.ItemDefinition;
import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.world.World;
import com.legacy.server.net.rsc.ActionSender;

public class BuyMarketItemTask extends MarketTask {
	
	private Player playerBuyer;
	private int auctionID;
	private int amount;

	public BuyMarketItemTask(Player buyer, final int auctionID, int amount) {
		this.playerBuyer = buyer;
		this.auctionID = auctionID;
		this.amount = amount;
	}
	
	@Override
	public void doTask()  throws Exception {
		MarketItem item = MarketDatabase.getAuctionItem(auctionID);
	
		if(item == null) {
			ActionSender.sendBox(playerBuyer, "@red@[Auction House - Error] % @whi@ This item is sold out! % Click 'Refresh' to update the Auction.", false);
			return;
		}
		if (amount <= 0) {
			ActionSender.sendBox(playerBuyer, "@red@[Auction House - Error] % @whi@ Invalid amount", false);
			return;
		}
		if (item.getSeller() == playerBuyer.getDatabaseID()) {
			ActionSender.sendBox(playerBuyer, "@red@[Auction House - Error] % @whi@ You can't buy your own object, please select another item. % Or cancel this item from the 'My Auction' tab.", false);
			return;
		}
		
		if (amount > item.getAmountLeft()) {
			amount = item.getAmountLeft();
		}

		int priceForEach = item.getPrice() / item.getAmountLeft();
		int auctionPrice = amount * priceForEach;

		if (playerBuyer.getInventory().countId(10) < auctionPrice) {
			ActionSender.sendBox(playerBuyer, "@ora@[Auction House - Warning] % @whi@ You don't have enough coins!", false);
			return;
		}

		ItemDefinition def = EntityHandler.getItemDef(item.getItemID());
		if (!playerBuyer.getInventory().full()
				&& (!def.isStackable() && playerBuyer.getInventory().size() + amount <= 30)) {
			if (!def.isStackable()) {
				for (int i = 0; i < amount; i++)
					playerBuyer.getInventory().add(new Item(item.getItemID(), 1));
			} else {
				playerBuyer.getInventory().add(new Item(item.getItemID(), amount));
			}
			playerBuyer.getInventory().remove(10, auctionPrice);
			ActionSender.sendBox(playerBuyer, "@gre@[Auction House - Success] % @whi@ The item has been placed to your inventory.", false);
			playerBuyer.save();
		}
		else if (!playerBuyer.getBank().full()) {
			playerBuyer.getBank().add(new Item(item.getItemID(), amount));
			playerBuyer.getInventory().remove(10, auctionPrice);
			ActionSender.sendBox(playerBuyer, "@gre@[Auction House - Success] % @whi@ The item has been placed to your bank.", false);
			playerBuyer.save();
		}
		else {
			ActionSender.sendBox(playerBuyer, "@red@[Auction House - Error] % @whi@ Unable to buy auction, no space left in your inventory or bank.", false);
			return;
		}

		int sellerUsernameID = item.getSeller();
		Player sellerPlayer = World.getWorld().getPlayerID(sellerUsernameID);
		
		if (sellerPlayer != null) {
			sellerPlayer.message("@gre@[Auction House]@lre@ " + amount + "x " + def.getName() + "@whi@ has been sold!");
			sellerPlayer.message("@gre@[Auction House]@whi@ You can collect your earnings from a bank.");
			sellerPlayer.save();
		}

		MarketDatabase.addCollectableItem("Sold " + def.getName() + "("+ item.getItemID() +") x" + amount + " for " + auctionPrice + "gp", 10, auctionPrice, sellerUsernameID);
		item.setBuyers(!item.getBuyers().isEmpty() ? item.getBuyers() + ", \n" + "[" + (System.currentTimeMillis() / 1000) + ": "
				+ playerBuyer.getUsername() + ": x" + amount + "]" : "[" + (System.currentTimeMillis() / 1000) + ": "
				+ playerBuyer.getUsername() + ": x" + amount + "]");
		
		item.setAmountLeft(item.getAmountLeft() - amount);
		item.setPrice(item.getAmountLeft() * priceForEach);
		
		if (item.getAmountLeft() == 0) {
			MarketDatabase.setSoldOut(item);
		} else {
			MarketDatabase.update(item);
		}
		Market.getInstance().addRequestOpenAuctionHouseTask(playerBuyer);
	}

}
