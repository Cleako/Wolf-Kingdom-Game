package com.legacy.server.plugins.listeners.executive;

import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.player.Player;

public interface ObjectActionExecutiveListener {

    /**
     * Prevent a user from activating an in-game object.
     */
    public boolean blockObjectAction(GameObject obj, String command, Player player);
}
