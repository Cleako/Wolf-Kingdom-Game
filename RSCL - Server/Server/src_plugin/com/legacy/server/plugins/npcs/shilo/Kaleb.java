package com.legacy.server.plugins.npcs.shilo;

import static com.legacy.server.plugins.Functions.*;

import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class Kaleb implements TalkToNpcExecutiveListener, TalkToNpcListener {

	public static final int KALEB = 621;

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(n.getID() == KALEB) {
			playerTalk(p, n, "Hello.");
			npcTalk(p, n, "Hello Bwana,",
					"What can I do for you today?");
			int menu = showMenu(p, n,
					"Can you tell me a bit about this place?",
					"Buy some wine : 1 Gold.",
					"Buy some Beer: 2 Gold.",
					"Buy a nights rest: 35 Gold",
					"Buy a pack of 5 Dorm tickets: 175 Gold");
			if(menu == 0) {
				npcTalk(p, n, "Of course Bwana, you look like a traveler!");
				playerTalk(p, n, "Yes I am actually!");
				npcTalk(p, n, "Well, I am a traveller myself, and I have set up this hostel",
						"for adventurers and travellers who are weary from their journey",
						"There is a dormitory upstairs if you are tired, it costs 35 gold",
						"pieces which covers the costs of laundry and cleaning.");
			} else if(menu == 1) {
				npcTalk(p, n, "Very good " +(p.isMale() ? "sir" : "madam!") + "!");
				if(hasItem(p, 10, 1)) {
					removeItem(p, 10, 1);
					addItem(p, 142, 1);
					p.message("You purchase a jug of wine.");
				} else {
					npcTalk(p, n, "Sorry Bwana, you don't have enough money.");
				}
			} else if(menu == 2) {
				npcTalk(p, n, "Very good " +(p.isMale() ? "sir" : "madam!") + "!");
				if(hasItem(p, 10, 2)) {
					removeItem(p, 10, 2);
					addItem(p, 193, 1);
					p.message("You purchase a frothy glass of beer.");
				} else {
					npcTalk(p, n, "Sorry Bwana, you don't have enough money.");
				}
			} else if(menu == 3) {
				npcTalk(p, n, "Very good " +(p.isMale() ? "sir" : "madam!") + "!");
				if(hasItem(p, 10, 35)) {
					removeItem(p, 10, 35);
					addItem(p, 987, 1);
					p.message("You purchase a ticket to access the dormitory.");
				} else {
					npcTalk(p, n, "Sorry Bwana, you don't have enough money.");
				}
			} else if(menu == 5) {
				npcTalk(p, n, "Very good " +(p.isMale() ? "sir" : "madam!") + "!");
				if(hasItem(p, 10, 175)) {
					removeItem(p, 10, 175);
					addItem(p, 987, 5);
					p.message("You purchase 5 tickets to access the dormitory.");
				} else {
					npcTalk(p, n, "Sorry Bwana, you don't have enough money.");
				}
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if(n.getID() == KALEB) {
			return true;
		}
		return false;
	}

}
