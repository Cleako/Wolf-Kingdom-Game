package com.legacy.server.plugins.npcs.dwarvenmine;

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

public final class NurmofPickaxe implements ShopInterface,
TalkToNpcExecutiveListener, TalkToNpcListener {

	public static final int npcid = 773;

	private final Shop shop = new Shop(false, 25000, 100, 60, 2, new Item(156,
			6), new Item(1258, 5), new Item(1259, 4),
			new Item(1260, 3), new Item(1261, 2), new Item(1262, 1));

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
		npcTalk(p, n, "greetings welcome to my pickaxe shop",
				"Do you want to buy my premium quality pickaxes");

		int option = showMenu(p, n, "Yes please", "No thankyou", "Are your pickaxes better than other pickaxes then?");
		if (option == 0) {
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		} else if(option == 2) {
			npcTalk(p, n, "Of course they are",
					"My pickaxes are made of higher grade metal than your ordinary bronze pickaxes",
					"Allowing you to have multiple swings at a rock until you get the ore from it");
		}
	}

}