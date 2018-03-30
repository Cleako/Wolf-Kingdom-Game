package com.legacy.server.plugins.listeners.action;

import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.player.Player;

public interface WallObjectActionListener {
	
	public void onWallObjectAction(GameObject obj, Integer click, Player p);
	
}
