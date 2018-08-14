package com.wk.server.model.action;

import com.wk.server.model.Point;
import com.wk.server.model.entity.player.Player;

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
