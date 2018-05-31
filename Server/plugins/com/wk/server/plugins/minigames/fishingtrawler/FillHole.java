package com.wk.server.plugins.minigames.fishingtrawler;

import static com.wk.server.plugins.Functions.message;
import static com.wk.server.plugins.Functions.removeItem;
import static com.wk.server.plugins.Functions.removeObject;

import com.wk.server.model.entity.GameObject;
import com.wk.server.model.entity.player.Player;
import com.wk.server.plugins.listeners.action.ObjectActionListener;
import com.wk.server.plugins.listeners.executive.ObjectActionExecutiveListener;

public class FillHole implements ObjectActionExecutiveListener, ObjectActionListener{

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		player.setBusyTimer(650);
		if(removeItem(player, 785, 1)) {
			removeObject(obj);
			message(player, 0, "you fill the hole with swamp paste");
		} else {
			message(player, 0, "you'll need some swamp paste to fill that");
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == 1077 || obj.getID() == 1071;
	}
}
