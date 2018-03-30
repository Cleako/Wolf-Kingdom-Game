package com.legacy.server.plugins.npcs.brimhaven;

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

public class DavonShop implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 900000000, 120, 90, 2, new Item(44, 0), new Item(314, 0), new Item(315, 0), new Item(316, 0), new Item(317, 0));

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		npcTalk(p, n, "Pssst come here if you want to do some amulet trading");
		int menu = showMenu(p, n, "What are you selling?", "What do you mean pssst?", "Why don't you ever restock some types of amulets?");
		if(menu == 0) {
			p.message("Davon opens up his jacket to reveal some amulets");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		} else if(menu == 1) {
			npcTalk(p, n, "I was clearing my throat");
		} else if(menu == 2) {
			npcTalk(p, n, "Some of these amulets are very hard to get",
					"I have to wait until an adventurer supplies me");
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 278;
	}

	@Override
	public Shop[] getShops() {
		return new Shop[] { shop };
	}

	@Override
	public boolean isMembers() {
		return true;
	}
}
