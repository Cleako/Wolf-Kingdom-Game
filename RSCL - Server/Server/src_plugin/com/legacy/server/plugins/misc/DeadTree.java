package com.legacy.server.plugins.misc;

import static com.legacy.server.plugins.Functions.sleep;

import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.ObjectActionListener;
import com.legacy.server.plugins.listeners.executive.ObjectActionExecutiveListener;

public class DeadTree implements ObjectActionListener, ObjectActionExecutiveListener {

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == 88;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		player.message("The tree seems to lash out at you!");
		sleep(500);
		player.damage((int) (player.getSkills().getLevel(3) * (double) 0.2D));
		player.message("You are badly scratched by the tree");
	}
}