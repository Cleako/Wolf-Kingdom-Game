package com.wk.server.plugins.listeners.action;

import com.wk.server.model.entity.GameObject;
import com.wk.server.model.entity.player.Player;

public interface WallObjectActionListener {
	
	public void onWallObjectAction(GameObject obj, Integer click, Player p);
	
}
