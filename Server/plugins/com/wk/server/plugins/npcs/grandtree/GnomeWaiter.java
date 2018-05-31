package com.wk.server.plugins.npcs.grandtree;

import static com.wk.server.plugins.Functions.npcTalk;
import static com.wk.server.plugins.Functions.showMenu;

import com.wk.server.model.Shop;
import com.wk.server.model.container.Item;
import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;
import com.wk.server.net.rsc.ActionSender;
import com.wk.server.plugins.ShopInterface;
import com.wk.server.plugins.listeners.action.TalkToNpcListener;
import com.wk.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class GnomeWaiter implements ShopInterface,
		TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 30000, 100, 25, 1,
			new Item(944, 3), new Item(945, 3), new Item(947, 3),
			new Item(948, 3), new Item(949, 3), new Item(950, 3),
			new Item(951, 3), new Item(952, 3), new Item(953, 3),
			new Item(954, 4), new Item(955, 4), new Item(956, 4),
			new Item(957, 4));

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		npcTalk(p, n,  "hello", "good afternoon",
				"can i tempt you with our new menu?");

		int option = showMenu(p, n, "i'll take a look", "not really");
		switch (option) {
		case 0:
			npcTalk(p, n, "i hope you like what you see");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
			break;

		case 1:
			npcTalk(p, n, "ok then, enjoy your stay");
			break;
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 581;
	}

	@Override
	public Shop[] getShops() {
		return new Shop[] { shop };
	}

	@Override
	public boolean isMembers() {
		return true;
	}

}
