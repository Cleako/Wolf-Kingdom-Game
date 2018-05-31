package com.wk.server.plugins.listeners.executive;

import com.wk.server.model.entity.GameObject;
import com.wk.server.model.entity.player.Player;

public interface ObjectActionExecutiveListener {

    /**
     * Prevent a user from activating an in-game object.
     */
    public boolean blockObjectAction(GameObject obj, String command, Player player);
}
