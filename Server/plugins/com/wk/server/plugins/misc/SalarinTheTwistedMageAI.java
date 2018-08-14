package com.wk.server.plugins.misc;

import static com.wk.server.plugins.Functions.sleep;

import com.wk.server.model.Skills;
import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;
import com.wk.server.model.entity.update.ChatMessage;
import com.wk.server.plugins.listeners.action.PlayerMageNpcListener;
import com.wk.server.plugins.listeners.executive.PlayerMageNpcExecutiveListener;

public class SalarinTheTwistedMageAI implements PlayerMageNpcListener, PlayerMageNpcExecutiveListener {
	
	/*
	 * Player maging Salarin the twisted AI - Just to degenerate ATTACK AND STRENGTH if over 2 in said skill.
	 */

	@Override
	public boolean blockPlayerMageNpc(Player p, Npc n) {
		if(n.getID() == 567 && (p.getSkills().getLevel(Skills.ATTACK) > 2 || p.getSkills().getLevel(Skills.STRENGTH) > 2)) {
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerMageNpc(Player p, Npc n) {
		if(n.getID() == 567 && (p.getSkills().getLevel(Skills.ATTACK) > 2 || p.getSkills().getLevel(Skills.STRENGTH) > 2)) {
			if(!p.withinRange(n, 5)) 
				return;
			n.getUpdateFlags().setChatMessage(new ChatMessage(n, "Amshalaraz Nithcosh dimarilo", p));
			sleep(600);
			p.message("You suddenly feel much weaker");
			p.getSkills().setLevel(Skills.ATTACK, 0);
			p.getSkills().setLevel(Skills.STRENGTH, 0);
		}
	}
}
