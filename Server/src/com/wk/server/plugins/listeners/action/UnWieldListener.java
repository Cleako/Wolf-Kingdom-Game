package com.wk.server.plugins.listeners.action;

import com.wk.server.model.container.Item;
import com.wk.server.model.entity.player.Player;

public interface UnWieldListener {

    public void onUnWield(Player player, Item item);

}
