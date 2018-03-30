package com.legacy.server.plugins.listeners.action;

import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.player.Player;

public interface WieldListener {

    public void onWield(Player player, Item item);
}
