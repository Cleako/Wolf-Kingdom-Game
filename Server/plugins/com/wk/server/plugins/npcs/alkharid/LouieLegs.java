package com.wk.server.plugins.npcs.alkharid;

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

public final class LouieLegs implements ShopInterface,
		TalkToNpcExecutiveListener, TalkToNpcListener {

	public static final int npcid = 85;

	private final Shop shop = new Shop(false, 25000, 100, 65, 1, new Item(206,
			5), new Item(9, 3), new Item(121, 2), new Item(248, 1),
			new Item(122, 1), new Item(123, 1));

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
		npcTalk(p, n, "Hey, wanna buy some armour?");

		int option = showMenu(p, n, "What have you got?", "No, thank you");
		if (option == 0) {
			npcTalk(p, n, "Take a look, see");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		}
	}

}