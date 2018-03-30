package com.legacy.server.plugins.npcs.falador;

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

public final class CassieShields  implements ShopInterface,
		TalkToNpcExecutiveListener, TalkToNpcListener {

	public static final int npcid = 101;

	private final Shop shop = new Shop(false, 25000, 100, 60, 2,
			new Item(4, 5), new Item(124, 3), new Item(128, 3),
			new Item(3, 2), new Item(2, 0), new Item(125, 0),
			new Item(129, 0), new Item(126, 0));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == npcid;
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
		if (n.getID() == npcid) {
			playerTalk(p, n, "What wares are you selling?");
			npcTalk(p,n, "I buy and sell shields", "Do you want to trade?");
			int option = showMenu(p,n, "Yes please", "No thanks");
			if (option == 0) {
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			}
		}
	}

}