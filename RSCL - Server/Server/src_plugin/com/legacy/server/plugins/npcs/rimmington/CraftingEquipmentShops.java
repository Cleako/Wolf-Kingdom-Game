package com.legacy.server.plugins.npcs.rimmington;

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

public final class CraftingEquipmentShops implements ShopInterface,
		TalkToNpcExecutiveListener, TalkToNpcListener {

	public static final int ROMMIK = 156;
	public static final int DOMMIK = 173;

	private final Shop shop = new Shop(false, 5000, 100, 65, 2,
			new Item(167, 2), new Item(293, 4), new Item(295, 2),
			new Item(294, 2), new Item(39, 3), new Item(43, 100),
			new Item(386, 3));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == ROMMIK || n.getID() == DOMMIK;
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
		npcTalk(p, n, "Would you like to buy some crafting equipment");

		int option = showMenu(p, n, "No I've got all the crafting equipment I need", "Let's see what you've got then");
		if (option == 0) {
			npcTalk(p, n, "Ok fair well on your travels");
		} else if(option == 1) {
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		}
	}

}