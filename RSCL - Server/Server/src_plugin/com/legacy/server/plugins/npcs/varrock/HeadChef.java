package com.legacy.server.plugins.npcs.varrock;

import static com.legacy.server.plugins.Functions.npcTalk;

import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class HeadChef implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		npcTalk(p, n, "Hello welcome to the chef's guild",
				"Only accomplished chefs and cooks are allowed in here",
				"Feel free to use any of our facilities");
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 133;
	}

}
