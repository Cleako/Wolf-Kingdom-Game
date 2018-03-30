package com.legacy.server.plugins.npcs.alkharid;

import static com.legacy.server.plugins.Functions.*;

import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class Warrior implements TalkToNpcListener, TalkToNpcExecutiveListener {

	public final int WARRIOR = 86;

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if(n.getID() == WARRIOR) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(n.getID() == WARRIOR) {
			int chatRandom = p.getRandom().nextInt(17);
			playerTalk(p, n, "Hello", "How's it going?");
			if(chatRandom == 0) {
				npcTalk(p, n, "Very well, thank you");
			} else if(chatRandom == 1) {
				npcTalk(p, n, "How can I help you?");
				int menu = showMenu(p, n,
						"Do you wish to trade?",
						"I'm in search of a quest",
						"I'm in search of enemies to kill");
				if(menu == 0) {
					npcTalk(p, n, "No, I have nothing I wish to get rid of",
							"If you want to do some trading,",
							"there are plenty of shops and market stalls around though");
				} else if(menu == 1) {
					npcTalk(p, n, "I'm sorry I can't help you there");
				} else if(menu == 2) {
					npcTalk(p, n, "I've heard there are many fearsome creatures under the ground");
				} 
			} else if(chatRandom == 2) {
				npcTalk(p, n, "None of your business");
			} else if(chatRandom == 3) {
				npcTalk(p, n, "No, I don't want to buy anything");
			} else if(chatRandom == 4) {
				npcTalk(p, n, "Get out of my way",
						"I'm in a hurry");
			} else if(chatRandom == 5) {
				npcTalk(p, n, "Who are you?");
				playerTalk(p, n, "I am a bold adventurer");
				npcTalk(p, n, "A very noble profession");
			} else if(chatRandom == 6) {
				npcTalk(p, n, "I'm fine",
						"How are you?");
				playerTalk(p, n, "Very well, thank you");
			} else if(chatRandom == 7) {
				npcTalk(p, n, "Hello",
						"Nice weather we've been having");
			} else if(chatRandom == 8) {
				npcTalk(p, n, "Do I know you?");
				playerTalk(p, n, "No, I was just wondering if you had anything interesting to say");
			} else if(chatRandom == 9) {
				npcTalk(p, n, "Not too bad",
						"I'm a little worried about the increase in Goblins these days");
				playerTalk(p, n, "Don't worry. I'll kill them");

			} else if(chatRandom == 10) {
				npcTalk(p, n, "No, I don't have any spare change");
			} else if(chatRandom == 11) {
				playerTalk(p, n, "I'm in search of enemies to kill");
				npcTalk(p, n, "I've heard there are many fearsome creatures under the ground");
			} else if(chatRandom == 12) {
				playerTalk(p, n, "Do you wish to trade?");
				npcTalk(p, n, "No, I have nothing I wish to get rid of",
						"If you want to do some trading,",
						"there are plenty of shops and market stalls around though");
			} else if(chatRandom == 13) {
				npcTalk(p, n, "Not too bad");
			} else if(chatRandom == 14) {
				p.message("The man ignores you");
			} else if(chatRandom == 15) {
				npcTalk(p, n, "Have this flier");
				addItem(p, 201, 1);
			} else if(chatRandom == 16) {
				npcTalk(p, n, "Hello");
			}
		}
	}
}
