package com.wk.server.plugins.listeners.action;

import com.wk.server.model.container.Item;
import com.wk.server.model.entity.player.Player;

public interface InvUseOnItemListener {

    public void onInvUseOnItem(Player player, Item item1, Item item2);
}
