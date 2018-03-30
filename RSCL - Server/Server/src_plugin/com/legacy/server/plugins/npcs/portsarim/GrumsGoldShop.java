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

public final class GrumsGoldShop implements ShopInterface,
		TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 30000, 100, 70,2, new Item(283,
			0), new Item(284, 0), new Item(285, 0), new Item(286, 0),
			new Item(287, 0), new Item(288, 0), new Item(289, 0),
			new Item(290, 0), new Item(291, 0), new Item(292, 0),
			new Item(301, 0), new Item(302, 0));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == 157;
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
		npcTalk(p, n, "Would you like to buy or sell some gold jewellery");
		int option = showMenu(p, n, "Yes please", "No, I'm not that rich");
		switch (option) {
		case 0:
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
			break;
		case 1:
			npcTalk(p, n, "Get out then we don't want any riff-raff in here");
			break;
		}

	}

}
