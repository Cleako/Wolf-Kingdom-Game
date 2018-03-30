package com.legacy.server.plugins.npcs.alkharid;

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

public final class ZekeScimitars implements ShopInterface,
		TalkToNpcExecutiveListener, TalkToNpcListener {

	public static final int npcid = 84;

	private final Shop shop = new Shop(false, 25000, 100, 55, 2,
			new Item(82, 5), new Item(83, 3), new Item(84, 2),
			new Item(85, 1));

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
		npcTalk(p, n, "A thousand greetings " + ((p.isMale()) ? "sir"
				: "madam"));

		final String[] options = new String[] { "Do you want to trade?", "Nice cloak" };
		int option = showMenu(p, n, options);
		if (option == 0) {
			npcTalk(p, n, "Yes, certainly","I deal in scimitars");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		} else if(option == 1) {
			npcTalk(p, n, "Thank you");
		}
	}

}