package com.wk.server.plugins.npcs.taverly;

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
