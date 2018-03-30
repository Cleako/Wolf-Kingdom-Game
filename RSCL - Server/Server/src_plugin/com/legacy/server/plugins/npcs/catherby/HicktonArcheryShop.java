package com.legacy.server.plugins.npcs.catherby;

import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.showMenu;

import com.legacy.server.model.Shop;
import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.net.rsc.ActionSender;
import com.legacy.server.plugins.ShopInterface;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class HicktonArcheryShop  implements ShopInterface,
		TalkToNpcListener, TalkToNpcExecutiveListener {

	private static final int HICKTON = 289;
	private final Shop shop = new Shop(false, 1000, 100, 80,1,
			new Item(190, 200), new Item(11, 200), new Item(638, 200),
			new Item(640, 0), new Item(642, 0), new Item(644, 0),
			new Item(646, 0), new Item(669, 200), new Item(670, 180),
			new Item(671, 160), new Item(672, 140),
			new Item(673, 120), new Item(674, 100), new Item(189, 4),
			new Item(188, 2), new Item(60, 2), new Item(649, 4),
			new Item(648, 4));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == HICKTON;
	}

	@Override
	public Shop[] getShops() {
		return new Shop[] { shop };
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		npcTalk(p, n, "Welcome to Hickton's Archery store",
				"Do you want to see my wares?");
		final int option = showMenu(p, n, new String[] { "Yes please",
				"No I prefer to bash things close up" });
		if (option == 0) {
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		}
	}

}
