package com.legacy.server.plugins.listeners.action;

import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.player.Player;

public interface ObjectActionListener {

    /**
     * When a user activates an in-game Object.
     */
    public void onObjectAction(GameObject obj, String command, Player player);

}
