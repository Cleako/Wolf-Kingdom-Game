package com.legacy.server.plugins.quests.free;

import static com.legacy.server.plugins.Functions.addItem;
import static com.legacy.server.plugins.Functions.hasItem;
import static com.legacy.server.plugins.Functions.message;
import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;
import static com.legacy.server.plugins.Functions.removeItem;
import static com.legacy.server.plugins.Functions.showMenu;

import com.legacy.server.Constants;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.QuestInterface;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class ImpCatcher implements QuestInterface,TalkToNpcListener,
		TalkToNpcExecutiveListener {

	@Override
	public int getQuestId() {
		return Constants.Quests.IMP_CATCHER;
	}

	@Override
	public String getQuestName() {
		return "Imp catcher";
	}

	/**
	 * // COORDS FOR KARAMJA CAVE: 425, 684
	 */
	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == 117) {
			if (p.getQuestStage(this) == 1) {
				npcTalk(p, n, "So how are you doing finding my beads?");
				if (!hasItem(p, 231) && !hasItem(p, 232) && !hasItem(p, 233)
						&& !hasItem(p, 234)) { // NEED TO GET INFO PROBABLY SAME
												// AS PRINCE ALI.
					playerTalk(p, n, "I've not found any yet");
					npcTalk(p,
							n,
							"Well get on with it",
							"I've lost a white bead, a red bead, a black bead and a yellow bead",
							"Go kill some imps");
					return;
				}
				if (hasItem(p, 231) && hasItem(p, 232) && hasItem(p, 233)
						&& hasItem(p, 234)) {
					playerTalk(p, n, "I've got all four beads",
							"It was a hard work I can tell you");
					npcTalk(p, n, "Give them here and I'll sort out a reward");
					message(p, "You give four coloured beads to Wizard Mizgog");
					removeItem(p, 231, 1);
					removeItem(p, 232, 1);
					removeItem(p, 233, 1);
					removeItem(p, 234, 1);
					npcTalk(p, n, "Here's you're reward then",
							"An Amulet of accuracy");
					message(p, "The Wizard hands you an amulet");
					addItem(p, 235, 1);
					p.sendQuestComplete(Constants.Quests.IMP_CATCHER);
				} else if (hasItem(p, 231) || hasItem(p, 232)
						|| hasItem(p, 233) || hasItem(p, 234)) {
					playerTalk(p, n, "I have found some of your beads");
					npcTalk(p, n, "Come back when you have them all",
							"The four colours of beads I need",
							"Are red,yellow,black and white",
							"Go chase some imps");
				}

				return;
			}
			if (p.getQuestStage(this) == 0) {
				npcTalk(p, n, "Hello there");
				int choice = showMenu(p, n, new String[] { "Give me a quest!",
						"Most of your friends are pretty quiet aren't they?" });
				if (choice == 0) {
					npcTalk(p, n, "Give me a quest what?");
					int choice2 = showMenu(p, n, new String[] {
							"Give me a quest please",
							"Give me a quest or else",
							"Just stop messing around and give me a quest" });
					if (choice2 == 0) {
						npcTalk(p,
								n,
								"Well seeing as you asked nicely",
								"I could do with some help",
								"The wizard Grayzag next door decided he didn't like me",
								"So he cast of spell of summoning",
								"And summoned hundreds of little imps",
								"These imps stole all sorts of my things",
								"Most of these things I don't really care about",
								"They're just eggs and balls of string and things",
								"but they stole my 4 magical beads",
								"There was a red one, a yellow one, a black one and a white one",
								"These imps have now spread out all over the kingdom",
								"Could you get my beads back for me");
						playerTalk(p, n, "I'll try");
						p.updateQuestStage(Constants.Quests.IMP_CATCHER, 1);
					} else if (choice2 == 1) {
						npcTalk(p, n, "Or else what? You'll attack me?",
								"Hahaha");
					} else if (choice2 == 2) {
						npcTalk(p, n,
								"Ah now you're assuming I have one to give");
					}
				} else if (choice == 1) {
					npcTalk(p, n,
							"Yes they've mostly got their heads in the clouds",
							"Thinking about magic");
				}
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == 117) {
			return true;
		}
		return false;
	}

	@Override
	public void handleReward(Player p) {
		p.message("Well done. You have completed the Imp catcher quest");
		p.incQuestExp(6, 875);
		p.incQuestPoints(1);
		p.message("@gre@You have gained 1 quest point!");

	}

	@Override
	public boolean isMembers() {
		return false;
	}

}
