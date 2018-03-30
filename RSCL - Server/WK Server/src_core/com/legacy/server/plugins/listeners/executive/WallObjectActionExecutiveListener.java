package com.legacy.server.plugins.listeners.executive;

import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.player.Player;

public interface WallObjectActionExecutiveListener {

    public boolean blockWallObjectAction(GameObject obj, Integer click, Player player);

}
