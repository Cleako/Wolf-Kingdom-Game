package com.wk.server.plugins.npcs.varrock;

import static com.wk.server.plugins.Functions.npcTalk;

import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;
import com.wk.server.plugins.listeners.action.TalkToNpcListener;
import com.wk.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

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
