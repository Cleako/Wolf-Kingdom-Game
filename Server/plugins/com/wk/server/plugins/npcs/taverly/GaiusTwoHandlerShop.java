package com.wk.server.plugins.npcs.taverly;

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

public class GaiusTwoHandlerShop  implements ShopInterface,
		TalkToNpcListener, TalkToNpcExecutiveListener {

	private final int GAIUS = 228;
	private final Shop shop = new Shop(false, 30000, 100, 60, 2,
			new Item(76, 4), new Item(77, 3), new Item(78, 2),
			new Item(426, 1), new Item(79, 1), new Item(80, 1));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == GAIUS;
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
		npcTalk(p, n, "Welcome to my two handed sword shop");
		final int option = showMenu(p, n, new String[] { "Let's trade",
				"Thankyou" });
		if (option == 0) {
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		}
	}

}
