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

public class JatixHerblawShop  implements ShopInterface,
		TalkToNpcListener, TalkToNpcExecutiveListener {

	private final int JATIX = 230;
	private final Shop shop = new Shop(false, 10000, 100, 70, 2,
			new Item(465, 50), new Item(468, 3), new Item(270, 50));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == JATIX;
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
		npcTalk(p, n, "Hello how can I help you?");
		final int option = showMenu(p, n, new String[] {
				"What are you selling?", "You can't, I'm beyond help",
				"I'm okay, thankyou" });

		if (option == 0) {
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		}

	}

}
