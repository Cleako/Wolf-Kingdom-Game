package com.legacy.server.plugins.npcs.tutorial;

import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;
import static com.legacy.server.plugins.Functions.showMenu;

import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.net.rsc.ActionSender;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class BankAssistant implements TalkToNpcExecutiveListener,
		TalkToNpcListener {
	/**
	 * @author Davve Tutorial island bank assistant
	 */

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		npcTalk(p, n, "Hello welcome to the bank of runescape",
				"You can deposit your items in banks",
				"This allows you to own much more equipment",
				"Than can be fitted in your inventory",
				"It will also keep your items safe",
				"So you won't lose them when you die",
				"You can withdraw deposited items from any bank in the world");
		if (p.getCache().hasKey("tutorial")
				&& p.getCache().getInt("tutorial") == 55) {
			playerTalk(p, n, "Can I access my bank account please?");
			npcTalk(p, n, "Certainly " + (p.isMale() ? "Sir" : "Miss"));
			p.setAccessingBank(true);
			ActionSender.showBank(p);
			p.getCache().set("tutorial", 60);
		} else {
			npcTalk(p, n, "Now proceed through the next door");
			int menu = showMenu(p, n, "Can I access my bank account please?",
					"Okay thankyou for your help");
			if (menu == 0) {
				npcTalk(p, n, "Certainly " + (p.isMale() ? "Sir" : "Miss"));
				p.setAccessingBank(true);
				ActionSender.showBank(p);
			} else if (menu == 1) {
				npcTalk(p, n, "Not a problem");
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 485;
	}

}
