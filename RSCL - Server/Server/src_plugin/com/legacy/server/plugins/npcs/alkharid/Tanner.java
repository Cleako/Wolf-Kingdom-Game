package com.legacy.server.plugins.npcs.alkharid;

import static com.legacy.server.plugins.Functions.addItem;
import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.showMenu;
import static com.legacy.server.plugins.Functions.sleep;

import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class Tanner implements TalkToNpcListener, TalkToNpcExecutiveListener {
	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		npcTalk(p, n, "Greeting friend i'm a manufacturer of leather");
		int option = showMenu(p, n, "Can I buy some leather then?",
				"Here's some cow hides, can I buy some leather now?",
				"Leather is rather weak stuff");

		switch (option) {
		case 0:
			npcTalk(p,n, "I make leather from cow hides", "Bring me some of them and a gold coin per hide");
			break;
		case 1:
			npcTalk(p,n, "Ok");
			while(true) {
				sleep(500);
				if(p.getInventory().countId(147) < 1) {
					p.message("You have run out of cow hides");
					break;
				} else if (p.getInventory().countId(10) < 1) {
					p.message("You have run out of coins");
					break;
				} else if (p.getInventory().remove(new Item(147)) > -1 && p.getInventory().remove(10, 1) > -1) {
					addItem(p, 148, 1);
				} else {
					break;
				}
			}
			break;
		case 2:
			npcTalk(p,n, "Well yes if all you're concerned with is how much it will protect you in a fight");
			break;
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 172;
	}

}
