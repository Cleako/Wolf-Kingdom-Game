package com.legacy.server.plugins.npcs.catherby;

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

public class HarrysFishingShack  implements ShopInterface,
		TalkToNpcListener, TalkToNpcExecutiveListener {

	private static final int HARRY = 250;
	
	private final Shop shop = new Shop(false, 3000, 100, 70,2, new Item(376, 5),
			new Item(377, 3), new Item(379, 2), new Item(375, 2),
			new Item(380, 200), new Item(548, 5), new Item(349, 0),
			new Item(354, 0), new Item(361, 0), new Item(552, 0),
			new Item(550, 0), new Item(351, 0), new Item(366, 0),
			new Item(372, 0), new Item(554, 0), new Item(369, 0),
			new Item(545, 0));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == HARRY;
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
		npcTalk(p, n, "Welcome you can buy fishing equipment at my store",
				"We'll also buy fish that you catch off you");
		final int option = showMenu(p, n, new String[] {
				"Let's see what you've got then", "Sorry, I'm not interested" });
		if (option == 0) {
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		}
	}

}
