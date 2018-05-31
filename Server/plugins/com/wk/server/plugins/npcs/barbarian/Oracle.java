package com.wk.server.plugins.npcs.barbarian;

import static com.wk.server.plugins.Functions.npcTalk;

import com.wk.server.Constants.Quests;
import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;
import com.wk.server.plugins.listeners.action.TalkToNpcListener;
import com.wk.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.wk.server.plugins.menu.Menu;
import com.wk.server.plugins.menu.Option;

public final class Oracle implements TalkToNpcExecutiveListener,
TalkToNpcListener {

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		Menu defaultMenu = new Menu();
		if (p.getQuestStage(Quests.DRAGON_SLAYER) == 2) {
			defaultMenu.addOption(new Option(
					"I seek a piece of the map of the isle of Crondor") {
				@Override
				public void action() {
					npcTalk(p, n, "The map's behind a door below",
							"But entering is rather tough",
							"And this is what you need to know",
							"You must hold the following stuff",
							"First a drink used by the mage",
							"Next some worm string, changed to sheet",
							"Then a small crustacean cage",
							"Last a bowl that's not seen heat");
				}
			});
		}
		defaultMenu.addOption(new Option(
				"Can you impart your wise knowledge to me oh oracle") {
			public void action() {
				npcTalk(p, n, "You must search from within to find your true destiny");
			}
		});
		defaultMenu.showMenu(p);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getDef().getName().equals("Oracle");
	}
}
