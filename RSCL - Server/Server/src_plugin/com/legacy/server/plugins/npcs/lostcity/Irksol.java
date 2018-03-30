package com.legacy.server.plugins.npcs.lostcity;

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

public final class Irksol implements ShopInterface, TalkToNpcExecutiveListener,
		TalkToNpcListener {

	private final Shop shop = new Shop(false, 3000, 50, 30,2,
			new Item(286, 5));

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == 218) {
			npcTalk(p, n, "selling ruby rings",
					"The best deals in all the planes of existance");
			int option = showMenu(p, n, "I'm interested in these deals",
					"No thank you");
			switch (option) {

			case 0:
				npcTalk(p, n, "Take a look at these beauties");
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
				break;
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 218;
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
