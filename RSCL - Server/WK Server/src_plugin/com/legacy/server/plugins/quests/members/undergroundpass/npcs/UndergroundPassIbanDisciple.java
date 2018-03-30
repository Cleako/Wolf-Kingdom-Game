package com.legacy.server.plugins.quests.members.undergroundpass.npcs;

import static com.legacy.server.plugins.Functions.addItem;
import static com.legacy.server.plugins.Functions.createGroundItem;
import static com.legacy.server.plugins.Functions.hasItem;
import static com.legacy.server.plugins.Functions.message;

import com.legacy.server.Constants;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.legacy.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;

public class UndergroundPassIbanDisciple implements PlayerKilledNpcListener, PlayerKilledNpcExecutiveListener {

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		if(n.getID() == 658) {
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if(n.getID() == 658) {
			n.killedBy(p);
			if(p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) == 7 || p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) == -1) {
				message(p, "you search the diciples remains");
				if(!hasItem(p, 1031)) {
					p.message("and find a staff of iban");
					addItem(p, 1031, 1);
				} else {
					p.message("but find nothing");
				}
			} else {
				createGroundItem(702, 1, p.getX(), p.getY(), p);
				createGroundItem(703, 1, p.getX(), p.getY(), p);
			}
		}
	}
}
