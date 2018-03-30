package com.legacy.server.plugins.npcs.tutorial;

import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;

import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class FinancialAdvisor implements TalkToNpcExecutiveListener, TalkToNpcListener {
	/**
	 * @author Davve
	 * Tutorial island financial advisor
	 */

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 40) {
			npcTalk(p, n, "Please proceed through the next door");
			return;
		}
		npcTalk(p, n, "Hello there",
				"I'm your designated financial advisor");
		playerTalk(p, n, "That's good because I don't have any money at the moment",
				"How do I get rich?");
		npcTalk(p, n, "There are many different ways to make money in runescape",
				"for example certain monsters will drop a bit of loot",
				"To start with killing men and goblins might be a good idea",
				"Some higher level monsters will drop quite a lot of treasure",
				"several of runescapes skills are good money making skills",
				"two of these skills are mining and fishing",
				"there are instructors on the island who will help you with this",
				"using skills and combat to make money is a good plan",
				"because using a skill also slowly increases your level in that skill",
				"A high level in a skill opens up many more oppurtunites",
				"Some other ways of making money include taking quests and tasks",
				"You can find these by talking to certain game controlled characters",
				"Our quest advisors will tell you about this",
				"Sometimes you will find items lying around",
				"Selling these to the shops makes some money too",
				"Now continue through the next door");
		p.getCache().set("tutorial", 40);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 480;
	}

}
