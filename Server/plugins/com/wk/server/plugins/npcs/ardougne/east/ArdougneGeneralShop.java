package com.wk.server.plugins.npcs.ardougne.east;

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

public class ArdougneGeneralShop implements ShopInterface, TalkToNpcListener,
		TalkToNpcExecutiveListener {

	private static final int KORTAN = 337, AEMAD = 336;
	private final Shop shop = new Shop(true, 15000, 130, 40, 3, new Item(464, 
			10), new Item(156, 2), new Item(12, 2), new Item(132, 2),
			new Item(166, 2), new Item(207, 2), new Item(11, 30),
			new Item(237, 1), new Item(982, 50), new Item(1263, 10));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == KORTAN || n.getID() == AEMAD;
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
		npcTalk(p, n, "Hello you look like a bold adventurer",
				"You've come to the right place for adventurer's equipment");
		final int option = showMenu(p, n,
				new String[] { "Oh that sounds interesting",
						"No I've come to the wrong place" });
		if (option == 0) {
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		}
	}

}
