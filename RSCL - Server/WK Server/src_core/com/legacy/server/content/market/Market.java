  package com.legacy.server.content.market;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.legacy.server.Constants;
import com.legacy.server.content.market.task.BuyMarketItemTask;
import com.legacy.server.content.market.task.CancelMarketItemTask;
import com.legacy.server.content.market.task.CollectableItemsNotificationTask;
import com.legacy.server.content.market.task.MarketTask;
import com.legacy.server.content.market.task.ModeratorDeleteAuctionTask;
import com.legacy.server.content.market.task.NewMarketItemTask;
import com.legacy.server.content.market.task.OpenMarketTask;
import com.legacy.server.content.market.task.PlayerCollectItemsTask;
import com.legacy.server.external.EntityHandler;
import com.legacy.server.external.ItemDefinition;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.world.World;
import com.legacy.server.util.NamedThreadFactory;

public class Market implements Runnable {
	
	/**
     * The asynchronous logger.
     */
    private static final Logger LOGGER = LogManager.getLogger();

	private static Market instance;

	public static Market getInstance() {
		if (instance == null) {
			instance = new Market();
			instance.start();
		}
		return instance;
	}

	private ArrayList<MarketItem> auctionItems = new ArrayList<MarketItem>();

	private LinkedBlockingQueue<MarketTask> auctionTaskQueue = new LinkedBlockingQueue<MarketTask>();

	private long lastCleanUp = 0;
	
	private LinkedBlockingQueue<OpenMarketTask> refreshRequestTasks = new LinkedBlockingQueue<OpenMarketTask>();

	private final ScheduledExecutorService scheduledExecutor = Executors
			.newSingleThreadScheduledExecutor(new NamedThreadFactory("AuctionHouseService"));

	public void addBuyAuctionItemTask(final Player player, int auctionID, int amount) {
		auctionTaskQueue.add(new BuyMarketItemTask(player, auctionID, amount));
	}

	public void addCancelAuctionItemTask(final Player player, final int auctionID) {
		auctionTaskQueue.add(new CancelMarketItemTask(player, auctionID));
	}

	public void addNewAuctionItemTask(final Player player, int itemID, final int amount, final int price) {
		auctionTaskQueue.add(new NewMarketItemTask(player, new MarketItem(-1, itemID, amount, amount, price,
				player.getDatabaseID(), player.getUsername(), "", System.currentTimeMillis() / 1000)));
	}

	public void addRequestOpenAuctionHouseTask(final Player player) {
		refreshRequestTasks.add(new OpenMarketTask(player));
	}

	public void addCollectableItemsNotificationTask(Player player) {
		auctionTaskQueue.add(new CollectableItemsNotificationTask(player));
	}
	public void addPlayerCollectItemsTask(Player player) {
		auctionTaskQueue.add(new PlayerCollectItemsTask(player));
	}
	
	public void addModeratorDeleteItemTask(Player player, int auctionID) {
		auctionTaskQueue.add(new ModeratorDeleteAuctionTask(player, auctionID));
	}
	
	private void checkAndRemoveExpiredItems() {
		try {
			LinkedList<MarketItem> expiredItems = new LinkedList<MarketItem>();
			
			for (MarketItem auction : auctionItems) {
				if (auction.hasExpired()) {
					expiredItems.add(auction);
				}
			}
			
			if (expiredItems.size() != 0) {
				PreparedStatement expiredItemsStatement = MarketDatabase.databaseInstance.prepareStatement(
						"INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX
						+ "expired_auctions`(`item_id`, `item_amount`, `time`, `playerID`, `explanation`) VALUES (?,?,?,?,?)");
				for (MarketItem expiredItem : expiredItems) {
					
					int itemIndex = expiredItem.getItemID();
					int amount = expiredItem.getAmountLeft();
	
					Player sellerPlayer = World.getWorld().getPlayerID(expiredItem.getSeller());
					MarketDatabase.setSoldOut(expiredItem);
	
					expiredItemsStatement.setInt(1, itemIndex);
					expiredItemsStatement.setInt(2, amount);
					expiredItemsStatement.setLong(3, System.currentTimeMillis() / 1000);
					expiredItemsStatement.setInt(4, expiredItem.getSeller());
					expiredItemsStatement.setString(5, "Expired");
					expiredItemsStatement.addBatch();
	
					ItemDefinition def = EntityHandler.getItemDef(itemIndex);
					if (sellerPlayer != null) {
						sellerPlayer.message("@gre@[Auction House] @whi@Your auction - @lre@" + def.getName() + " x" + amount
								+ "@whi@ has expired!");
						sellerPlayer.message("You can collect it back from a banker.");
					}
				}
				expiredItemsStatement.executeBatch();
			}
			lastCleanUp = System.currentTimeMillis();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public ArrayList<MarketItem> getAuctionItems() {
		return auctionItems;
	}

	private void processAuctionTasks() {
		MarketTask nextTask = null;
		while ((nextTask = auctionTaskQueue.poll()) != null) {
			try {
				nextTask.doTask();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void processRefreshRequests() {
		MarketTask refreshTask = null;
		while ((refreshTask = refreshRequestTasks.poll()) != null) {
			try {
				refreshTask.doTask();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void processUpdateAuctionItemCache() {
		int activeAuctionCount = MarketDatabase.getAuctionCount();
		if (activeAuctionCount == auctionItems.size()) {
			return;
		}
		auctionItems.clear();
		auctionItems = MarketDatabase.getAuctionItemsOnSale();
	}

	@Override
	public void run() {
		try {
			if (System.currentTimeMillis() - lastCleanUp > 60000) {
				checkAndRemoveExpiredItems();
			}
			
			processAuctionTasks();
			processUpdateAuctionItemCache();
			processRefreshRequests();
		} catch(Throwable r) {
			r.printStackTrace();
		}
	}

	public void start() {
		scheduledExecutor.scheduleAtFixedRate(this, 50, 50, TimeUnit.MILLISECONDS);
		LOGGER.info("Market executor running");
	}
}
