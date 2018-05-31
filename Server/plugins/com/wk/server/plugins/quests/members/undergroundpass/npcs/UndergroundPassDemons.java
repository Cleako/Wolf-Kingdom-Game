package com.wk.server.plugins.quests.members.undergroundpass.npcs;

import static com.wk.server.plugins.Functions.addItem;
import static com.wk.server.plugins.Functions.hasItem;
import static com.wk.server.plugins.Functions.inArray;
import static com.wk.server.plugins.Functions.message;

import com.wk.server.Constants;
import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;
import com.wk.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.wk.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;

public class UndergroundPassDemons implements PlayerKilledNpcListener, PlayerKilledNpcExecutiveListener {

	public static int[] DEMONS = { 645, 646, 647 };
	public static int AMULET_OF_OTHAINIAN = 1009;
	public static int AMULET_OF_DOOMION = 1010;
	public static int AMULET_OF_HOLTHION = 1011;

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		if(inArray(n.getID(), DEMONS)) {	
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if(inArray(n.getID(), DEMONS)) {
			n.killedBy(p);
			if(!p.getCache().hasKey("doll_of_iban") && p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) != 6) {
				p.message("the demon slumps to the floor");
				teleportPlayer(p, n);
			} else {
				teleportPlayer(p, n);
				message(p, "the demon slumps to the floor");
				if(!hasItem(p, n.getID() + 364)) {
					p.message("around it's neck you find a strange looking amulet");
					addItem(p, n.getID() + 364 , 1); // will give correct ammys for all.
				}
			}
		}
	}
	private void teleportPlayer(Player p, Npc n) {
		if(n.getID() == DEMONS[0]) {
			p.teleport(796, 3541);
		} else if(n.getID() == DEMONS[1]) {
			p.teleport(807, 3541);
		} else if(n.getID() == DEMONS[2]) {
			p.teleport(807, 3528);
		}
	}
}
