package com.legacy.server.plugins.npcs.tutorial;

import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;
import static com.legacy.server.plugins.Functions.showMenu;

import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class WildernessGuide implements TalkToNpcExecutiveListener, TalkToNpcListener {
	/**
	 * @author Davve
	 * Tutorial island wilderness guide
	 */

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		npcTalk(p, n, "Hi are you someone who likes to fight other players?",
				"Granted it has big risks",
				"but it can be very rewarding too");
		int menu = showMenu(p, n, "Yes I'm up for a bit of a fight", "I'd prefer to avoid that");
		if(menu == 0) {
			npcTalk(p, n, "Then the wilderness is the place for you",
					"That is the area of the game where you can attack other players",
					"Be careful though",
					"Other players can be a lot more dangerous than monsters",
					"they will be much more persistant in chasing after you",
					"Especially when they hunt in groups");
			optionsDialogue(p, n);
		} else if(menu == 1) {
			npcTalk(p, n, "Then don't stray into the wilderness",
					"That is the area of the game where you can attack other players");
			optionsDialogue(p, n);

		}
	}
	private void optionsDialogue(Player p, Npc n) {
		int menu2 = showMenu(p, n, "Where is this wilderness?", "What happens when I die?");
		if(menu2 == 0) {
			npcTalk(p, n, "Once you get into the main playing area head north",
					"then you will eventually reach the wilderness",
					"The deeper you venture into the wilderness",
					"The greater the level range of players who can attack you",
					"So if you go in really deep",
					"Players much stronger than you can attack you");
			playerTalk(p, n, "What happens when I die?");
			npcTalk(p, n, "normally when you die",
					"you will lose all of the items in your inventory",
					"Except the three most valuable",
					"You never keep stackable items like coins and runes",
					"which is why it is a good idea to leave things in the bank",
					"However if you attack another player",
					"You get a skull above your head for twenty minutes",
					"If you die with a skull above your head you lose your entire inventory",
					"Now proceed through the next door");
			if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") != 70) {
				p.getCache().set("tutorial", 70);
			}
		} else if(menu2 == 1) {
			npcTalk(p, n, "normally when you die",
					"you will lose all of the items in your inventory",
					"Except the three most valuable",
					"You never keep stackable items like coins and runes",
					"which is why it is a good idea to leave things in the bank",
					"However if you attack another player",
					"You get a skull above your head for twenty minutes",
					"If you die with a skull above your head you lose your entire inventory");
			playerTalk(p, n, "Where is the wilderness?");
			npcTalk(p, n, "Once you get into the main playing area head north",
					"then you will eventually reach the wilderness",
					"The deeper you venture into the wilderness",
					"The greater the level range of players who can attack you",
					"So if you go in really deep",
					"Players much stronger than you can attack you",
					"Now proceed through the next door");
			if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") != 70) {
				p.getCache().set("tutorial", 70);
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 493;
	}

}
