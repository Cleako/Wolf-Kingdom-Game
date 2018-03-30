package com.legacy.server.plugins.npcs.lostcity;

import static com.legacy.server.plugins.Functions.addItem;
import static com.legacy.server.plugins.Functions.hasItem;
import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;
import static com.legacy.server.plugins.Functions.removeItem;
import static com.legacy.server.plugins.Functions.showMenu;

import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class FairyLunderwin implements TalkToNpcListener,
TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == 219) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == 219) {
			npcTalk(p, n, "I am buying cabbage, we have no such thing where I come from",
					"I pay hansomly for this wounderous object",
					"Would 100 gold coins per cabbage be a fair price?");
			if(hasItem(p, 18)) {
				int menu = showMenu(p, n, "Yes, I will sell you all my cabbages",
						"No, I will keep my cabbbages");
				if(menu == 0) {
					p.message("You sell a cabbage");
					removeItem(p, 18, 1);
					addItem(p, 10, 100);
					npcTalk(p, n, "Good doing buisness with you");
				}
			} else {
				playerTalk(p, n, "Alas I have no cabbages either");
			}
		}
	}
}
