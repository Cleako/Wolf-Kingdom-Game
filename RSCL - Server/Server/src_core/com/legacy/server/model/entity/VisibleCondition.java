package com.legacy.server.model.entity;

import com.legacy.server.model.entity.player.Player;

public abstract class VisibleCondition {
	public abstract boolean isVisibleTo(Entity entity, Player observer);
}
