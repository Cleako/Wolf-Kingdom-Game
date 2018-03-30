package com.legacy.server.plugins.minigames.fishingtrawler;

import static com.legacy.server.plugins.Functions.message;

import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.ObjectActionListener;
import com.legacy.server.plugins.listeners.executive.ObjectActionExecutiveListener;

public class ExitBarrel implements ObjectActionListener, ObjectActionExecutiveListener {

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == 1070;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		message(player, 1900, "you climb onto the floating barrel", "and begin to kick your way to the shore",
				"you make it to the shore tired and weary");
		player.teleport(550, 711);
		player.damage(3);
	}

}
