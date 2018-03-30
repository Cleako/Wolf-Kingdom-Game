package com.legacy.server.plugins.listeners.action;

import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.player.Player;

public interface InvUseOnItemListener {

    public void onInvUseOnItem(Player player, Item item1, Item item2);
}
