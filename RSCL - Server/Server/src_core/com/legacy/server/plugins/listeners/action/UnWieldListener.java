package com.legacy.server.plugins.listeners.action;

import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.player.Player;

public interface UnWieldListener {

    public void onUnWield(Player player, Item item);

}
