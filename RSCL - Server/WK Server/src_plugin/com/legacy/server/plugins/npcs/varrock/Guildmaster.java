package com.legacy.server.plugins.npcs.varrock;

import static com.legacy.server.plugins.Functions.npcTalk;

import com.legacy.server.Constants;
import com.legacy.server.Constants.Quests;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.legacy.server.plugins.menu.Menu;
import com.legacy.server.plugins.menu.Option;

public class Guildmaster implements TalkToNpcListener,
		TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getDef().getName().equals("Guildmaster");
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		Menu defaultMenu = new Menu();
		defaultMenu.addOption(new Option("What is this place?") {
			@Override
			public void action() {
				npcTalk(p,
						n,
						" This is the champions' guild",
						" Only Adventurers who have proved themselves worthy",
						" by gaining influence from quests are allowed in here",
						" As the number of quests in the world rises",
						" So will the requirements to get in here",
						" But so will the rewards");
			}
		});
		defaultMenu.addOption(new Option(
				"Do you know where I could get a rune plate mail body?") {
			@Override
			public void action() {
				npcTalk(p,
						n,
						"I have a friend called Oziach who lives by the cliffs",
						"He has a supply of rune plate mail",
						"He may sell you some if you're lucky, he can be little strange sometimes though");
				if (p.getQuestStage(Constants.Quests.DRAGON_SLAYER) == 0) {
					p.updateQuestStage(Quests.DRAGON_SLAYER, 1);
				}
			}
		});
		defaultMenu.showMenu(p);
	}

}