package com.wk.server.plugins.npcs.varrock;

import static com.wk.server.plugins.Functions.npcTalk;
import static com.wk.server.plugins.Functions.playerTalk;
import static com.wk.server.plugins.Functions.showMenu;

import com.wk.server.model.Shop;
import com.wk.server.model.container.Item;
import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;
import com.wk.server.net.rsc.ActionSender;
import com.wk.server.plugins.ShopInterface;
import com.wk.server.plugins.listeners.action.TalkToNpcListener;
import com.wk.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class Tailor  implements ShopInterface,
		TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 30000, 130, 40,2, new Item(192,
			0), new Item(185, 3), new Item(512, 1), new Item(541, 3),
			new Item(146, 3), new Item(39, 3), new Item(43, 100),
			new Item(16, 10), new Item(17, 10), new Item(807, 3),
			new Item(808, 3), new Item(191, 1), new Item(194, 5),
			new Item(195, 3), new Item(187, 2), new Item(183, 4),
			new Item(609, 3));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == 501;
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
		npcTalk(p, n, "Now you look like someone who goes to a lot of fancy dress parties");
		playerTalk(p, n,"Errr... what are you saying exactly?");
		npcTalk(p, n, "I'm just saying that perhaps you would like to peruse my selection of garments");
		int opt = showMenu(p,n, "I think I might leave the perusing for now thanks",
				"OK,lets see what you've got then" );
		if(opt == 1) {
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		}
	}

}
