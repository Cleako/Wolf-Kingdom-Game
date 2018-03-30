package com.legacy.server.model.action;

import com.legacy.server.model.Point;
import com.legacy.server.model.entity.player.Player;

public abstract class WalkToAction {
	
	protected Player player;
	protected Point location;
	protected boolean hasExecuted;
	
	public WalkToAction(Player player, Point location) {
		this.player = player;
		this.location = location;
	}
	
	public abstract void execute();
	
	public abstract boolean shouldExecute();
}
