package com.legacy.server.plugins.quests.members.undergroundpass.npcs;

import static com.legacy.server.plugins.Functions.addItem;
import static com.legacy.server.plugins.Functions.hasItem;
import static com.legacy.server.plugins.Functions.message;
import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;

import com.legacy.server.Constants;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class UndergroundPassPaladin implements TalkToNpcListener,
TalkToNpcExecutiveListener, PlayerKilledNpcListener, PlayerKilledNpcExecutiveListener {

	public static int COAT_OF_ARMS_RED = 998;
	public static int COAT_OF_ARMS_BLUE = 999;

	public static int PALADIN_BEARD = 632;
	public static int PALADIN = 633;

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == PALADIN_BEARD;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		switch(p.getQuestStage(Constants.Quests.UNDERGROUND_PASS)) {
		case 4:
			playerTalk(p,n, "hello paladin");
			if(!p.getCache().hasKey("paladin_food")) {
				npcTalk(p,n, "you've done well to get this far traveller, here eat");
				p.message("the paladin gives you some food");
				addItem(p, 259, 2);
				addItem(p, 138, 1);
				addItem(p, 346, 1);
				addItem(p, 475, 1);
				addItem(p, 484, 1);
				p.getCache().store("paladin_food", true);
				playerTalk(p,n, "thanks");
			}
			npcTalk(p,n, "you should leave this place now traveller",
					"i heard the crashing of rocks further down the cavern",
					"iban must be restless",
					"i have no doubt that zamorak still controls these caverns",
					"a little further on lies the great door of iban",
					"we've tried everything, but it will not let us enter",
					"leave now before iban awakes and it's too late");
			break;
		case 5:
		case 6:
		case 7:
		case -1:
			playerTalk(p, n, "hello");
			npcTalk(p, n, "you again, die zamorakian scum");
			n.startCombat(p);
			break;
		}
	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		return n.getID() == PALADIN_BEARD || n.getID() == PALADIN;
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if(n.getID() == PALADIN_BEARD) {
			n.killedBy(p);
			message(p, "the paladin slumps to the floor",
					"you search his body");
			if(!hasItem(p, COAT_OF_ARMS_RED)) {
				addItem(p, COAT_OF_ARMS_RED, 1);
				p.message("and find a paladin coat of arms");
			} else {
				p.message("but find nothing");
			}
		}
		if(n.getID() == PALADIN) {
			n.killedBy(p);
			message(p, "the paladin slumps to the floor",
					"you search his body");
			if(!hasItem(p, COAT_OF_ARMS_BLUE, 2)) {
				addItem(p, COAT_OF_ARMS_BLUE, 1);
				p.message("and find a paladin coat of arms");
			} else {
				p.message("but find nothing");
			}
		}
	}
}
