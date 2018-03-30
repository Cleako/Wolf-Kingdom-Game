package com.legacy.server.plugins.misc;

import static com.legacy.server.plugins.Functions.message;

import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.ObjectActionListener;
import com.legacy.server.plugins.listeners.executive.ObjectActionExecutiveListener;

public class Dummy implements ObjectActionListener, ObjectActionExecutiveListener{

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == 49;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		message(player, 3200, "You swing at the dummy");
		if(player.getSkills().getMaxStat(0) > 7) {
			player.message("There is only so much you can learn from hitting a dummy");
		} else {
			player.message("You hit the dummy");
			player.incExp(0, 5, true);
		}
	}

}
