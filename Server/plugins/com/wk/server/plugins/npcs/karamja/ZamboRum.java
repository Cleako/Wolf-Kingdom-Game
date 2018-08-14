package com.wk.server.plugins.npcs.karamja;

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

public final class ZamboRum implements ShopInterface,
		TalkToNpcExecutiveListener, TalkToNpcListener {

	public static final int npcid = 165;

	private final Shop shop = new Shop(false, 25000, 100, 70, 2, new Item(193,
			3), new Item(318, 3), new Item(142, 1));

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
		npcTalk(p,
				n,
				"Hey are you wanting to try some of my fine wines and spirits?",
				"All brewed locally on Karamja island");

		final String[] options = new String[] { "Yes please", "No thankyou" };
		int option = showMenu(p, n, options);
		if (option == 0) {
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		}
	}

}