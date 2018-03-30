package com.legacy.server.plugins.npcs.falador;

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

public final class WaynesChains  implements ShopInterface,
		TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 25000, 100, 65, 1, new Item(113,
			3), new Item(7, 2), new Item(114, 1), new Item(431, 1),
			new Item(115, 1), new Item(116, 1));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == 141;
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
		if (n.getID() == 141) {
			npcTalk(p, n, "Welcome to Wayne's chains",
					"Do you wanna buy or sell some chain mail?");

			final String[] options = new String[] { "Yes please", "No thanks" };
			int option = showMenu(p,n, options);
			if (option == 0) {
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			}
		}
	}

}
