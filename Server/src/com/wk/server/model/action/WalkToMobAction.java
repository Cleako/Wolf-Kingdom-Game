package com.wk.server.model.action;

import com.wk.server.model.entity.Mob;
import com.wk.server.model.entity.player.Player;

public abstract class WalkToMobAction extends WalkToAction {
	
	private int radius;
	protected Mob mob;

	public WalkToMobAction(Player owner, Mob mob, int radius) {
		super(owner, mob.getLocation());
		this.mob = mob;
		this.radius = radius;
		if(shouldExecute()) {
			execute();
			owner.setWalkToAction(null);
			hasExecuted = true;
		}
	}
	public Mob getMob() {
		return mob;
	}
	@Override
	public boolean shouldExecute() {
		return (player.withinRange(mob, radius) && !hasExecuted);
	}
}
