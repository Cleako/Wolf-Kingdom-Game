package com.legacy.server.plugins.npcs.taverly;

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

public class HelemosShop  implements ShopInterface,
TalkToNpcListener, TalkToNpcExecutiveListener {

	private static final int HELEMOS = 269;
	private final Shop shop = new Shop(false, 60000, 100, 55,3, new Item(594, 1));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == HELEMOS;
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
		npcTalk(p, n, "Welcome to the hero's guild");
		final int option = showMenu(p, n, new String[] {
				"So do you sell anything here?", "So what can I do here?" });
		if (option == 0) {
			npcTalk(p, n, "Why yes we do run an exclusive shop for our members");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		} else if (option == 1) {
			npcTalk(p, n, "Look around there are all sorts of things to keep our members entertained");
		} 
	}
}
