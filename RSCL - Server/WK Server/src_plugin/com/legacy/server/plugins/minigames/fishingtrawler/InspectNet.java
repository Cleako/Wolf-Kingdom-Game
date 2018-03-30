package com.legacy.server.plugins.minigames.fishingtrawler;

import static com.legacy.server.plugins.Functions.hasItem;
import static com.legacy.server.plugins.Functions.message;
import static com.legacy.server.plugins.Functions.removeItem;

import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.world.World;
import com.legacy.server.plugins.listeners.action.ObjectActionListener;
import com.legacy.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.legacy.server.util.rsc.DataConversions;

public class InspectNet implements ObjectActionListener, ObjectActionExecutiveListener {

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == 1102 || obj.getID() == 1101;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
	
		message(player, 1900, "you inspect the net");
		
		if(World.getWorld().getFishingTrawler().isNetBroken()) {
			player.message("it's beginning to rip");
			if(!hasItem(player, 237)) {
				player.message("you'll need some rope to fix it");
				return;
			}
			message(player, 1900, "you attempt to fix it with your rope");
			if(DataConversions.random(0, 1) == 0) {
				player.message("you manage to fix the net");
				removeItem(player, 237, 1);
				World.getWorld().getFishingTrawler().setNetBroken(false);
			} else {
				player.message("but you fail in the harsh conditions");
			}
		} else {
			player.message("it is not damaged");
		}
	}

}
