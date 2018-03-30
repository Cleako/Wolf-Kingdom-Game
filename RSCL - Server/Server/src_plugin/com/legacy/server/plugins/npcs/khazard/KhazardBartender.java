package com.legacy.server.plugins.npcs.khazard;

import static com.legacy.server.plugins.Functions.addItem;
import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;
import static com.legacy.server.plugins.Functions.showMenu;

import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class KhazardBartender implements TalkToNpcExecutiveListener,
		TalkToNpcListener {

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == 382) {
			playerTalk(p, n, "Hello");
			npcTalk(p, n,
					"Hello, what can i get you? we have all sorts of brew");
			int bar = showMenu(p, n, "I'll have a beer please",
					"I'd like a khali brew please", "Got any news?");
			if (bar == 0) {
				npcTalk(p, n, "There you go, that's one gold coin");
				p.getInventory().add(new Item(193));
				p.getInventory().remove(10, 1);
			} else if (bar == 1) {
				npcTalk(p, n, "There you go", "No charge");
				addItem(p, 735, 1);
			} else if (bar == 2) {
				npcTalk(p, n,
						"Well have you seen the famous khazard fight arena?",
						"I've seen some grand battles in my time..",
						"Ogres, goblins, even dragons, they all come to fight",
						"The poor slaves of general khazard");
			}
		}

	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == 382) {
			return true;
		}
		return false;
	}
}
