package com.wk.server.model.entity;

import com.wk.server.model.entity.player.Player;

public abstract class VisibleCondition {
	public abstract boolean isVisibleTo(Entity entity, Player observer);
}
