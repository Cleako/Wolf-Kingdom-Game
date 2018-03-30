package com.legacy.server.plugins.npcs.grandtree;

import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;
import static com.legacy.server.plugins.Functions.showMenu;

import com.legacy.server.model.Shop;
import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.net.rsc.ActionSender;
import com.legacy.server.plugins.ShopInterface;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class Gulluck implements ShopInterface,
		TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 3000, 100, 25,1, new Item(11,
			200), new Item(190, 150), new Item(786, 1), new Item(189,
			4), new Item(188, 2), new Item(60, 2), new Item(669, 200),
			new Item(670, 180), new Item(671, 160),
			new Item(671, 140), new Item(12, 5), new Item(88, 3),
			new Item(89, 5), new Item(90, 2), new Item(91, 1),
			new Item(76, 4), new Item(77, 3), new Item(78, 2),
			new Item(426, 1), new Item(79, 1), new Item(80, 1));

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == 587) {
			playerTalk(p, n, "hello");
			npcTalk(p, n, "good day brave adventurer",
					"could i interest you in my fine selection of weapons?");

			int option = showMenu(p, n, "i'll take a look", "No thanks");
			switch (option) {
				case 0:
					p.setAccessingShop(shop);
					ActionSender.showShop(p, shop);
					break;
				case 1:
					npcTalk(p, n, "grrr");
					break;

			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 587;
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
