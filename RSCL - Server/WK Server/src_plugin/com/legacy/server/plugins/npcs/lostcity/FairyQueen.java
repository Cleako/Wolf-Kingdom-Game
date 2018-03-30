package com.legacy.server.plugins.npcs.lostcity;

import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;
import static com.legacy.server.plugins.Functions.showMenu;

import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class FairyQueen implements TalkToNpcListener,
TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == 392) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == 392) {
			int menu = showMenu(p, n, "How do crops and such survive down here?",
					"What's so good about this place?");
			if(menu == 0) {
				playerTalk(p, n, "Surely they need a bit of sunlight?");
				npcTalk(p, n, "Clearly you come from a plane dependant on sunlight",
						"Down here the plants grow in the aura of faerie");
			} else if(menu == 1) {
				npcTalk(p, n, "Zanaris is a meeting point of cultures",
						"those from many worlds converge here to exchange knowledge and goods");
			}
		}
	}
}
