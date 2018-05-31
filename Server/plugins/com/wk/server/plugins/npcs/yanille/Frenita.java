package com.wk.server.plugins.npcs.yanille;

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

public final class Frenita implements ShopInterface,
		TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 3000, 100, 55, 1,
			new Item(251, 5), new Item(252, 2), new Item(338, 2),
			new Item(341, 2), new Item(348, 5), new Item(166, 4),
			new Item(140, 1), new Item(135, 8), new Item(337, 2),
			new Item(136, 8));

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		npcTalk(p, n, "Would you like to buy some cooking equipment");

		int option = showMenu(p, n, "Yes please", "No thank you");
		switch (option) {

		case 0:
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
			break;
		}

	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 530;
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
