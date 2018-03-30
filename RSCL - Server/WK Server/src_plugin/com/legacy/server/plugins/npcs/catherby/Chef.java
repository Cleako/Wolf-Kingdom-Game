package com.legacy.server.plugins.npcs.catherby;

import static com.legacy.server.plugins.Functions.addItem;
import static com.legacy.server.plugins.Functions.hasItem;
import static com.legacy.server.plugins.Functions.message;
import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;
import static com.legacy.server.plugins.Functions.removeItem;
import static com.legacy.server.plugins.Functions.showMenu;

import com.legacy.server.Constants.Quests;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.legacy.server.plugins.menu.Menu;
import com.legacy.server.plugins.menu.Option;

public class Chef implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		if(p.getQuestStage(Quests.FAMILY_CREST) == -1) {
			npcTalk(p,n, "I hear you have brought the completed crest to my father",
					"Impressive work I must say");
			if(hasItem(p, 698)) {
				playerTalk(p,n, "My Father says you can improve these gauntlets for me");
				npcTalk(p,n, "Yes that is true",
						"I can change them to gauntlets of cooking",
						"Wearing them means you will burn your lobsters, swordish and shark less");
				int menu = showMenu(p,n,
						"Yes please do that for me",
						"I'll see what your brothers have to offer first");
				if(menu == 0) {
					message(p, "Caleb holds the gauntlets and closes his eyes",
							"Caleb concentrates",
							"Caleb hands the gauntlets to you");
					p.getInventory().replace(698, 700);
				} else if(menu == 1) {
					npcTalk(p,n, "Ok suit yourself");
				}
			}
			return;
		}
		if(p.getQuestStage(Quests.FAMILY_CREST) >= 4 && p.getQuestStage(Quests.FAMILY_CREST) <= 8) {
			npcTalk(p,n, "How are you doing getting the rest of the crest?");
			playerTalk(p,n, "I am still working on it");
			if(!hasItem(p, 695)) {
				playerTalk(p, n, "I have lost the piece of crest you gave me");
				npcTalk(p, n, "Well I have one more here, be careful with it");
				p.message("Caleb gives you his piece of the crest");
				addItem(p, 695, 1);
			}
			npcTalk(p,n, "Well good luck in your quest");
			return;
		}
		if(p.getQuestStage(Quests.FAMILY_CREST) == 3) {
			if(p.getCache().hasKey("skipped_menu")) {
				npcTalk(p, n, "Hello again, I'm just putting the finishing touches to my salad");
				int menu = showMenu(p, n, "Err what happened to the rest of the crest?", "Good luck with that then");
				if(menu == 0) {
					npcTalk(p,n, "Well we had a bit of a fight over it",
							"We all wanted to be the heir of our fathers lands",
							"we each ended up with a piece of the crest",
							"none of us wanted to give their piece of the crest up to any of the others",
							"And none of us wanted to face our father",
							"coming home without a complete crest");
					playerTalk(p,n, "So do you know where I could find any of your brothers?");
					npcTalk(p,n, "Well we haven't really kept in touch",
							"What with all falling out over the crest",
							"I did hear from my brother Avan about a year ago though",
							"He said he was a living in a town in the desert",
							"Ask around the desert and you may find him",
							"My brother has very expensive tastes",
							"He may not give up the crest easily");
					p.getCache().remove("skipped_menu");
				}
				return;
			}
			playerTalk(p,n, "Where did you say I could find Avan?");
			npcTalk(p,n, "He said he was a living in a town in the desert",
					"Ask around the desert and you may find him");
			return;
		}
		if(p.getQuestStage(Quests.FAMILY_CREST) == 2) {
			npcTalk(p,n, "How is the fish collecting going?");
			if(!hasItem(p, 370) && !hasItem(p, 555) && !hasItem(p, 367) && !hasItem(p, 357) && !hasItem(p, 350)) {
				playerTalk(p,n, "I haven't got all the fish yet");
				npcTalk(p,n, "Remember I want cooked swordfish, bass, tuna, salmon and shrimp");
			} else {
				playerTalk(p,n, "Yes i have all of that now");
				message(p, "You give all of the fish to Caleb");
				removeItem(p, 370, 1);
				removeItem(p, 555, 1);
				removeItem(p, 367, 1);
				removeItem(p, 357, 1);
				removeItem(p, 350, 1);
				p.message("Caleb gives you his piece of the crest");
				addItem(p, 695, 1);
				p.getCache().store("skipped_menu", true);
				p.updateQuestStage(Quests.FAMILY_CREST, 3);
				int m = showMenu(p,n,
						"Err what happened to the rest of it?",
						"Thankyou very much");
				if(m == 0) {
					npcTalk(p,n, "Well we had a bit of a fight over it",
							"We all wanted to be the heir of our fathers lands",
							"we each ended up with a piece of the crest",
							"none of us wanted to give their piece of the crest up to any of the others",
							"And none of us wanted to face our father",
							"coming home without a complete crest");
					playerTalk(p,n, "So do you know where I could find any of your brothers?");
					npcTalk(p,n, "Well we haven't really kept in touch",
							"What with all falling out over the crest",
							"I did hear from my brother Avan about a year ago though",
							"He said he was a living in a town in the desert",
							"Ask around the desert and you may find him",
							"My brother has very expensive tastes",
							"He may not give up the crest easily");
					p.getCache().remove("skipped_menu");
				}
			}
			return;
		}
		npcTalk(p,n, "Who are you? What are you after?");
		Menu defaultMenu = new Menu();
		if (p.getQuestStage(Quests.FAMILY_CREST) == 1) {
			defaultMenu.addOption(new Option("Are you Caleb Fitzharmon?") {
				@Override
				public void action() {
					npcTalk(p,n, "I am he, and who might you be?");
					playerTalk(p,n, "I have been sent by your father",
							"He wants me to retrieve the Fitzharmon family crest");
					npcTalk(p,n, "Ah, yes hmm well I do have a bit of it yes");
					new Menu().addOptions(
							new Option("Err what happened to the rest of crest?") {
								public void action() {
									npcTalk(p, n, "Well we had a bit of a fight over it", 
											"We all wanted to be the heir of our fathers lands", 
											"we each ended up with a piece of the crest",
											"none of us wanted to give their piece of the crest up to any of the others",
											"And none of us wanted to face our father",
											"coming home without a complete crest");
									playerTalk(p, n, "So can I have your bit?");
									HAVE_YOUR_BIT(p, n);
								}
							},
							new Option("So can I have your bit?") {
								public void action() {
									HAVE_YOUR_BIT(p, n);
								}
							}).showMenu(p);
				}
			});
		}
		defaultMenu.addOption(new Option("Nothing, I will be on my way") {
			@Override
			public void action() {
				// NOTHING
			}
		});
		defaultMenu.addOption(new Option("I see you are a chef, will you cook me anything?") {
			@Override
			public void action() {
				npcTalk(p,n, "I would, but I am very busy",
						"Trying to prepare my special fish salad",
						"Which I hope will significantly increase my renown as a master chef");
			}
		});
		defaultMenu.showMenu(p);
	}

	private void HAVE_YOUR_BIT(Player p, Npc n) {
		npcTalk(p,n, "Well I am the oldest son, by rights it is mine");
		playerTalk(p,n, "It's not a lot of use to you without the rest of it though");
		npcTalk(p,n, "Well true",
				"So I'll tell you what I'll do",
				"I am struggling to complete my seafood salad",
				"I don't seem to be able to get hold of the ingredients I need",
				"Help me and I'll help you");
		playerTalk(p,n, "What are you missing exactly?");
		npcTalk(p,n, "I need cooked swordfish,bass,tuna,salmon and shrimp");
		int menu = showMenu(p,n,
				"Ok I will get those",
				"Why don't you just give me the crest?");
		if(menu == 0) {
			p.updateQuestStage(Quests.FAMILY_CREST, 2);
		} else if(menu == 1) {
			npcTalk(p,n, "No I don't want to just give it away");
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 310;
	}

}
