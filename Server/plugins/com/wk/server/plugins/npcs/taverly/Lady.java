package com.wk.server.plugins.npcs.taverly;

import static com.wk.server.plugins.Functions.npcTalk;
import static com.wk.server.plugins.Functions.playerTalk;

import com.wk.server.Constants.Quests;
import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;
import com.wk.server.plugins.listeners.action.TalkToNpcListener;
import com.wk.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.wk.server.plugins.menu.Menu;
import com.wk.server.plugins.menu.Option;

public class Lady implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		npcTalk(p, n, "Good day to you " + p.isMale() != null ? "sir" : "madam");
		Menu defaultMenu = new Menu();
		defaultMenu.addOption(new Option("Who are you?") {
			@Override
			public void action() {
				npcTalk(p, n, "I am the lady of the lake");
			}
		});
		defaultMenu.addOption(new Option("Good day") {
			@Override
			public void action() {
				// NOTHING HAPPENS
			}
		});
		if (p.getQuestStage(Quests.MERLINS_CRYSTAL) == 3 || p.getQuestStage(Quests.MERLINS_CRYSTAL) == -1) {
			defaultMenu.addOption(new Option("I seek the sword Exalibur") {
				@Override
				public void action() {
					npcTalk(p,
							n,
							"Aye, I have that artifact in my possession",
							"Tis very valuable and not an artifact to be given away lightly",
							"I would want to give it away only to one who is worthy and good");
					playerTalk(p, n, "And how am I meant to prove that");
					npcTalk(p, n, "I will set a test for you",
							"First I need you to travel to Port Sarim",
							"Then go to the upstairs room of the jeweller's shop there");
					playerTalk(p, n, "Ok that seems easy enough");
					p.getCache().store("lady_test", true);
				}
			});
		}
		defaultMenu.showMenu(p);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 283;
	}

}