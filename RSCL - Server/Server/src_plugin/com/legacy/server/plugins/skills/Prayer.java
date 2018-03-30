package com.legacy.server.plugins.skills;

import static com.legacy.server.plugins.Functions.message;
import static com.legacy.server.plugins.Functions.sleep;

import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.ObjectActionListener;
import com.legacy.server.plugins.listeners.executive.ObjectActionExecutiveListener;
public class Prayer implements ObjectActionExecutiveListener, ObjectActionListener {

	@Override
	public void onObjectAction(final GameObject object, String command, Player player) {
		if(command.equalsIgnoreCase("recharge at")) {
			int maxPray = object.getID() == 200 ? player.getSkills().getMaxStat(5) + 2 : player.getSkills().getMaxStat(5);
			if(player.getSkills().getLevel(5) == maxPray) {
				player.message("You already have full prayer points");
			} else {
				player.message("You recharge your prayer points");
				player.playSound("recharge");
				if (player.getSkills().getLevel(5) < maxPray) {
					player.getSkills().setLevel(5, maxPray);
				}
			
			}
			if(object.getID() == 625 && object.getY() == 3573) {
				sleep(650);
				message(player, "Suddenly a trapdoor opens beneath you");
				player.teleport(608, 3525);
			}
			return;
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		if (command.equals("recharge at")) {
			return true;
		}
		return false;
	}

}
