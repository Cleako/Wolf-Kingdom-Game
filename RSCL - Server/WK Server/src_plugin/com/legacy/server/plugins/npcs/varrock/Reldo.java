package com.legacy.server.plugins.npcs.varrock;

import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;
import static com.legacy.server.plugins.Functions.sleep;

import com.legacy.server.Constants;
import com.legacy.server.Constants.Quests;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.legacy.server.plugins.menu.Menu;
import com.legacy.server.plugins.menu.Option;

public final class Reldo implements TalkToNpcListener,
		TalkToNpcExecutiveListener {
	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 20;
	}

	/**
	 * Man, this is the whole reldo with shield of arrav. dont tell me that this
	 * is bad choice.
	 */
	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		Menu defaultMenu = new Menu();
		if (p.getCache().hasKey("read_arrav")
				&& p.getQuestStage(Quests.SHIELD_OF_ARRAV) == 1) {
			playerTalk(p, n, "Ok I've read the book",
					"Do you know where I can find the Phoenix Gang");
			npcTalk(p, n, "No I don't",
					"I think I know someone who will though",
					"Talk to Baraek, the fur trader in the market place",
					"I've heard he has connections with the Phoenix Gang");
			playerTalk(p, n, "Thanks, I'll try that");
			p.updateQuestStage(Quests.SHIELD_OF_ARRAV, 2);
			return;
		}
		playerTalk(p, n, "Hello");
		npcTalk(p, n, "Hello stranger");
		if (p.getQuestStage(Constants.Quests.SHIELD_OF_ARRAV) == 0) {
			defaultMenu.addOption(new Option("I'm in search of quest.") {
				@Override
				public void action() {
					npcTalk(p, n, "I don't think there's any here");
					sleep(600);
					npcTalk(p, n, "Let me think actually",
							"If you look in a book",
							"called the shield of Arrav",
							"You'll find a quest in there",
							"I'm not sure where the book is mind you",
							"I'm sure it's somewhere in here");
					playerTalk(p, n, "Thankyou");
					p.updateQuestStage(Constants.Quests.SHIELD_OF_ARRAV, 1);
				}
			});
		}
		defaultMenu.addOption(new Option("Do you have anything to trade") {
			@Override
			public void action() {
				npcTalk(p, n, "No sorry. I'm not the trading type");
				playerTalk(p, n, "ah well");
			}
		});
		defaultMenu.addOption(new Option("What do you do?") {
			@Override
			public void action() {
				npcTalk(p, n, "I'm the palace librarian");
				playerTalk(p, n, "Ah that's why you're in the library then");
				npcTalk(p, n, "Yes",
						"Though I might be in here even if I didn't work here",
						"I like reading");
			}
		});
		if (p.getQuestStage(Quests.THE_KNIGHTS_SWORD) == 1) {
			defaultMenu.addOption(new Option(
					"What do you know about the incando dwarves?") {
				@Override
				public void action() {
					npcTalk(p,
							n,
							"The incando dwarves, you say?",
							"They were the world's most skilled smiths about a hundred years ago",
							"They used secret knowledge",
							"Which they passed down from generation to generation",
							"Unfortunatly about a century ago the once thriving race",
							"Was wiped out during the barbarian invasions of that time");
					playerTalk(p, n, "So are there any incando left at all?");
					npcTalk(p,
							n,
							"A few of them survived",
							"But with the bulk of their population destroyed",
							"Their numbers have dwindled even further",
							"Last i knew there were a couple living in asgarnia",
							"Near the cliffs on the asgarnian southern peninsula",
							"They tend to keep to themselves",
							"They don't tend to tell people that they're the descendants of the incando",
							"Which is why people think that the tribe has died out totally",
							"You may have more luck talking to them if you bring them some red berry pie",
							"They really like red berry pie");
					playerTalk(p, n, "Thank you");
					npcTalk(p, n, "You're welcome");
					p.updateQuestStage(Quests.THE_KNIGHTS_SWORD, 2);
				}
			});
		}

		defaultMenu.showMenu(p);
	}
}
