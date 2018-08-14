package com.wk.server.plugins.npcs;

import static com.wk.server.plugins.Functions.message;
import static com.wk.server.plugins.Functions.npcTalk;
import static com.wk.server.plugins.Functions.showMenu;

import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;
import com.wk.server.plugins.listeners.action.TalkToNpcListener;
import com.wk.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class MonkHealer implements TalkToNpcListener, TalkToNpcExecutiveListener {
	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		npcTalk(p, n, "Greetings traveller");
		int option = showMenu(p,n, "Can you heal me? I'm injured", "Greetings");
		if(option == 0) {
			npcTalk(p,n, "Ok");
			message(p, "The monk places his hands on your head", "You feel a little better");
			int newHp = p.getSkills().getLevel(3) + 10;
			if (newHp > p.getSkills().getMaxStat(3)) {
				newHp = p.getSkills().getMaxStat(3);
			}
			p.getSkills().setLevel(3, newHp);
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 93 || n.getID() == 174;
	}
}
