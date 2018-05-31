package com.wk.server.plugins.npcs.catherby;

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

public class ArheinGeneralShop  implements ShopInterface,
		TalkToNpcListener, TalkToNpcExecutiveListener {

	private static final int ARHEIN = 280;
	private final Shop shop = new Shop(true, 15000, 130, 40, 3, new Item(21, 10),
			new Item(156, 2), new Item(341, 2), new Item(338, 2),
			new Item(166, 2), new Item(167, 2), new Item(168, 5),
			new Item(237, 2), new Item(135, 2));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == ARHEIN;
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
		npcTalk(p, n, "Hello would you like to trade");
		final int option = showMenu(p, n, new String[] { "Yes ok",
				"No thankyou" }); // "is that your ship"
		if (option == 0) {
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		}
	}

}
