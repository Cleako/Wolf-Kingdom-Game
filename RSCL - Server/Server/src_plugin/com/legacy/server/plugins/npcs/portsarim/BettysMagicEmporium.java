package com.legacy.server.plugins.npcs.portsarim;

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

public final class BettysMagicEmporium  implements
		ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 6000, 100, 75, 2, new Item(31,
			500), new Item(32, 500), new Item(33, 500), new Item(34,
			500), new Item(35, 500), new Item(36, 500), new Item(270,
			500), new Item(185, 1), new Item(199, 1));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == 149;
	}

	@Override
	public Shop[] getShops() {
		return new Shop[] { shop };
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		if (n.getID() == 149) {
			npcTalk(p, n, "Welcome to the magic emporium");
			int opt = showMenu(p, n, "Can I see your wares?",
					"Sorry I'm not into magic");
			if (opt == 0) {
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			}
			if (opt == 1) {
				npcTalk(p, n, "Send anyone my way who is");
			}
		}
	}

}
