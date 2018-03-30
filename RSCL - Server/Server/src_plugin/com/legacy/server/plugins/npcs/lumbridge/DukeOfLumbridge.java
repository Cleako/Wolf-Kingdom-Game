package com.legacy.server.plugins.npcs.lumbridge;

import static com.legacy.server.plugins.Functions.addItem;
import static com.legacy.server.plugins.Functions.hasItem;
import static com.legacy.server.plugins.Functions.message;
import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;

import com.legacy.server.Constants.Quests;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.legacy.server.plugins.menu.Menu;
import com.legacy.server.plugins.menu.Option;

public final class DukeOfLumbridge implements TalkToNpcExecutiveListener,
		TalkToNpcListener {

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		Menu defaultMenu = new Menu();
		npcTalk(p, n, "Greetings welcome to my castle");
		if (p.getQuestStage(Quests.DRAGON_SLAYER) >= 2
				|| p.getQuestStage(Quests.DRAGON_SLAYER) < 0) {
			if (!hasItem(p, 420, 1)) {
				defaultMenu
						.addOption(new Option(
								"I seek a shield that will protect me from dragon's breath") {
							public void action() {
								npcTalk(p, n,
										"A knight going on a dragon quest hmm?");
								npcTalk(p, n, "A most worthy cause");
								npcTalk(p, n, "Guard this well my friend");
								message(p, "The duke hands you a shield");
								addItem(p, 420, 1);
							}
						});
			}
		}
		defaultMenu.addOptions(new Option("Have you any quests for me?") {
			public void action() {
				npcTalk(p, n, "All is well for me");
			}
		}, new Option("Where can I find money?") {
			public void action() {
				playerTalk(p, n, "Where can I find money?");
				npcTalk(p, n,
						"I've heard the blacksmiths are prosperous amoung the peasantry");
				npcTalk(p, n, "Maybe you could try your hand at that");
			}
		});
		defaultMenu.showMenu(p);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 198;
	}

}
