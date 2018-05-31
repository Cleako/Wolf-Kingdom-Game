package com.wk.server.plugins.listeners.executive;

import com.wk.server.model.entity.GameObject;
import com.wk.server.model.entity.player.Player;

public interface WallObjectActionExecutiveListener {

    public boolean blockWallObjectAction(GameObject obj, Integer click, Player player);

}
