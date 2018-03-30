package com.legacy.server.plugins.npcs.varrock;

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

public final class AuburysRunes implements ShopInterface,
		TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 3000, 100, 70, 2, new Item(31,
			500), new Item(32, 500), new Item(33, 500), new Item(34,
			500), new Item(35, 500), new Item(36, 500));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == 54;
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
			npcTalk(p, n, "Do you want to buy some runes?");
			int opt = showMenu(p, n, "Yes please",
					"Oh it's a rune shop. No thank you, then");
			if (opt == 0) {
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			}
			if (opt == 1) {
				npcTalk(p, n,
						"Well if you do find someone who does want runes,",
						"send them my way");
			}
	}

}
