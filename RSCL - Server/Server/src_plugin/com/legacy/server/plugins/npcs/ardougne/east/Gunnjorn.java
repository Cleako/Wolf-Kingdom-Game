package com.legacy.server.plugins.npcs.ardougne.east;

import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.showMenu;

import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class Gunnjorn implements TalkToNpcListener, TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == 588) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == 588) {
			npcTalk(p, n, "Ahoy there!");
			int menu = showMenu(p, n, "What is this place?");
			if(menu == 0) {
				npcTalk(p, n, "Haha welcome to my obstacle course",
						"Have fun, but remember this isn't a child's playground",
						"People have died here", "The best way to train",
						"Is to go round the course in a clockwise direction");
			}
		}
	}
}
