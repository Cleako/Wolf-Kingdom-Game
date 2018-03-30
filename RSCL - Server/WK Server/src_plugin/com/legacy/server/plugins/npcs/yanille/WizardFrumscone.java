package com.legacy.server.plugins.npcs.yanille;

import static com.legacy.server.plugins.Functions.npcTalk;

import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class WizardFrumscone implements TalkToNpcListener, TalkToNpcExecutiveListener {
	
	public static int WIZARD_FRUMSCONE = 515;

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == WIZARD_FRUMSCONE;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(n.getID() == WIZARD_FRUMSCONE) {
			npcTalk(p,n, "Do you like my magic zombies",
					"Feel free to kill them",
					"Theres plenty more where these came from");
		}
	}
}
