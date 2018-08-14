package com.wk.server.plugins.npcs.lostcity;

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
