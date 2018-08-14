package com.wk.server.plugins.misc;

import static com.wk.server.plugins.Functions.*;

import com.wk.server.model.entity.GameObject;
import com.wk.server.model.entity.player.Player;
import com.wk.server.plugins.listeners.action.ObjectActionListener;
import com.wk.server.plugins.listeners.executive.ObjectActionExecutiveListener;

public class LeafyPalmTree implements ObjectActionListener, ObjectActionExecutiveListener {

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == 1176) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == 1176) {
			message(p, 1300, "You give the palm tree a good shake.");
			message(p, 0, "A palm leaf falls down.");
			createGroundItem(1279, 1, obj.getX(), obj.getY(), p);
			replaceObjectDelayed(obj, 15000, 33);
		}
	}
}
