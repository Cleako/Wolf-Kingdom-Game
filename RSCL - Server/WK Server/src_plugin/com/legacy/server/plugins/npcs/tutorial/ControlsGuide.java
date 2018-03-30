package com.legacy.server.plugins.npcs.tutorial;

import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;

import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class ControlsGuide implements TalkToNpcExecutiveListener, TalkToNpcListener {
	/**
	 * @author Davve
	 * Tutorial island second room guide
	 */
	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 15) {
			npcTalk(p, n, "Please proceed through the next door");
			return;
		}
		npcTalk(p, n, "Hello I'm here to tell you more about the game's controls",
				"Most of your options and character information",
				"can be accessed by the menus in the top right corner of the screen",
				"moving your mouse over the map icon",
				"which is the second icon from the right",
				"gives you a view of the area you are in",
				"clicking on this map is an affective way of walking around",
				"though if the route is blocked, for example by a closed door",
				"then your character won't move",
				"Also notice the compass on the map which may be of help to you");
		playerTalk(p, n, "Thankyou for your help");
		npcTalk(p, n, "Now carry on to speak to the combat instructor");
		p.getCache().set("tutorial", 15);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 499;
	}

}
