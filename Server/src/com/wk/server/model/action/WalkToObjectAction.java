package com.wk.server.model.action;

import com.wk.server.model.entity.GameObject;
import com.wk.server.model.entity.player.Player;

public abstract class WalkToObjectAction extends WalkToAction {

	private GameObject object;

	public WalkToObjectAction(Player owner, GameObject object) {
		super(owner, object.getLocation());
		this.object = object;
		if(player.atObject(object)) {
			execute();
			player.setWalkToAction(null);
			hasExecuted = true;
		}
	}

	@Override
	public boolean shouldExecute() {
		return player.atObject(object) && !hasExecuted;
	}

}
