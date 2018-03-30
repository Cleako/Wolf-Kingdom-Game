package com.legacy.server.plugins.npcs.barbarian;

import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;

import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.legacy.server.util.rsc.DataConversions;

public class Barbarians implements TalkToNpcListener, TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 76 || n.getID() == 78;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		playerTalk(p, n, "Hello");
		int randomDiag = DataConversions.random(0, 10);
		if(randomDiag == 0) {
			npcTalk(p, n, "Go away",
					"This is our village");
		} else if(randomDiag == 1) {
			npcTalk(p, n, "Hello");
		} else if(randomDiag == 2) {
			npcTalk(p, n, "Wanna fight?");
			n.startCombat(p);
		} else if(randomDiag == 3) {
			npcTalk(p, n, "Who are you?");
			playerTalk(p, n, "I'm a bold adventurer");
			npcTalk(p, n, "You don't look very strong");
		} else if(randomDiag == 4) {
			p.message("The barbarian grunts");
		} else if(randomDiag == 5) {
			p.message("Ello");
		} else if(randomDiag == 6) {
			npcTalk(p, n, "ug");
		} else if(randomDiag == 7) {
			npcTalk(p, n, "I'm a little busy right now",
					"We're getting ready for our next barbarian raid");
		} else if(randomDiag == 8) {
			npcTalk(p, n, "Beer?");
		} else if(randomDiag == 9) {
			p.message("The barbarian ignores you");
		} else if(randomDiag == 10) {
			p.message("Grr");
		}
	}
}
