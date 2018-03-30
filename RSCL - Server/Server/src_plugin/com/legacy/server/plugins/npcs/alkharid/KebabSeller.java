package com.legacy.server.plugins.npcs.alkharid;

import static com.legacy.server.plugins.Functions.addItem;
import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;
import static com.legacy.server.plugins.Functions.removeItem;
import static com.legacy.server.plugins.Functions.showMenu;

import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class KebabSeller implements TalkToNpcListener,
		TalkToNpcExecutiveListener {

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		npcTalk(p, n, "Would you like to buy a nice kebab? Only 1 gold");
		int o = showMenu(p, n, "I think I'll give it a miss", "Yes please");
		if (o == 1) {
			if (removeItem(p, 10, 1)) {
				p.message("You buy a kebab");
				addItem(p, 210, 1);
			} else {
				playerTalk(p, n, "Oops I forgot to bring any money with me");
				npcTalk(p, n, "Come back when you have some");
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 90;
	}

}
