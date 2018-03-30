package com.legacy.server.plugins.npcs.brimhaven;

import static com.legacy.server.plugins.Functions.addItem;
import static com.legacy.server.plugins.Functions.hasItem;
import static com.legacy.server.plugins.Functions.message;
import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;
import static com.legacy.server.plugins.Functions.showMenu;

import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class BrimHavenBartender implements TalkToNpcExecutiveListener,
		TalkToNpcListener {

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == 279) {
			npcTalk(p, n, "Yohoho me hearty what would you like to drink?");
			String[] options;
			if (p.getCache().hasKey("barcrawl")
					&& !p.getCache().hasKey("barfour")) {
				options = new String[] { "Nothing thankyou",
						"A pint of Grog please", "A bottle of rum please",
						"I'm doing Alfred Grimhand's barcrawl" };
			} else {
				options = new String[] { "Nothing thankyou",
						"A pint of Grog please", "A bottle of rum please" };
			}
			int firstMenu = showMenu(p, n, options);
			if (firstMenu == 0) {// NOTHING
			} else if (firstMenu == 1) {
				npcTalk(p, n, "One grog coming right up", "That'll be 3 gold");
				if (hasItem(p, 10, 3)) {
					p.message("You buy a pint of Grog");
					p.getInventory().remove(10, 3);
					addItem(p, 598, 1);
				} else {
					playerTalk(p, n,
							"Oh dear. I don't seem to have enough money");
				}
			} else if (firstMenu == 2) {
				npcTalk(p, n, "That'll be 27 gold");
				if (hasItem(p, 10, 27)) {
					p.message("You buy a bottle of rum");
					p.getInventory().remove(10, 27);
					addItem(p, 318, 1);
				} else {
					playerTalk(p, n,
							"Oh dear. I don't seem to have enough money");
				}
			} else if (firstMenu == 3) {
				npcTalk(p, n, "Haha time to be breaking out the old supergrog",
						"That'll be 15 coins please");
				if (hasItem(p, 10, 15)) {
					message(p,
							"The bartender serves you a glass of strange thick dark liquid",
							"You wince and drink it", "You stagger backwards",
							"You think you see 2 bartenders signing 2 barcrawl cards");
					p.getCache().store("barfour", true);
				} else {
					playerTalk(p, n, "I don't have 15 coins right now");
				}
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == 279) {
			return true;
		}
		return false;
	}
}
