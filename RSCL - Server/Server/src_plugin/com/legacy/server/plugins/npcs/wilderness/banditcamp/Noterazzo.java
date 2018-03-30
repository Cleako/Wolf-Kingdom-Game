package com.legacy.server.plugins.npcs.wilderness.banditcamp;

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

public class Noterazzo implements ShopInterface, TalkToNpcListener, TalkToNpcExecutiveListener {
	
	private final Shop shop = new Shop(true, 12400, 90, 60, 3,
			new Item(135, 3), new Item(140, 2), new Item(166, 2),
			new Item(167, 2), new Item(168, 5), new Item(156, 5),
			new Item(87, 10));
	
	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if(n.getID() == 233) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(n.getID() == 233) {
		
			npcTalk(p, n, "Hey wanna trade?, I'll give the best deals you can find");
			int menu = showMenu(p, n, "Yes please", "No thankyou", "How can you afford to give such good deals?");
			if(menu == 0) {
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			} else if(menu == 1) {
				//NOTHING
			} else if(menu == 2) {
				npcTalk(p, n, "The general stores in Asgarnia and Misthalin are heavily taxed",
						"It really makes it hard for them to run an effective buisness",
						"For some reason taxmen don't visit my store");
				p.message("Noterazzo winks at you");
			}
		}
	}

	@Override
	public Shop[] getShops() {
		return new Shop[] { shop };
	}

	@Override
	public boolean isMembers() {
		return false;
	}
}
