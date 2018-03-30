package com.legacy.server.plugins.npcs.khazard;

import static com.legacy.server.plugins.Functions.hasItem;
import static com.legacy.server.plugins.Functions.message;
import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;
import static com.legacy.server.plugins.Functions.removeItem;
import static com.legacy.server.plugins.Functions.showMenu;
import static com.legacy.server.plugins.Functions.sleep;

import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class Docky implements TalkToNpcExecutiveListener, TalkToNpcListener {

	public static final int DOCKY = 390;

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(n.getID() == DOCKY) {
			playerTalk(p, n, "hello there");
			npcTalk(p, n, "ah hoy there, wanting",
					"to travel on the beatiful",
					"lady valentine are we");
			int menu = showMenu(p, n, "not really, just looking around", "where are you travelling to");
			if(menu == 0) {
				npcTalk(p, n, "o.k land lover");
			} else if(menu == 1) {
				npcTalk(p, n, "we sail direct to Birmhaven port",
						"it really is a speedy crossing",
						"so would you like to come",
						"it cost's 30 gold coin's");
				int travel = showMenu(p, n, "no thankyou", "ok");
				if(travel == 1) {
					if(hasItem(p, 10, 30)) {
						message(p, 1900, "You pay 30 gold");
						removeItem(p, 10, 30);
						message(p, 3000, "You board the ship");
						p.teleport(467, 647);
						sleep(2000);
						p.message("The ship arrives at Port Birmhaven");
					} else {
						playerTalk(p,n, "Oh dear I don't seem to have enough money");
					}
				}
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == DOCKY;
	}
}
