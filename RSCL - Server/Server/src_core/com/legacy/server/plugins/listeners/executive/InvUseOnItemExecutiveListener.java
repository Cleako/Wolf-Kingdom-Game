package com.legacy.server.plugins.listeners.executive;

import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.player.Player;

public interface InvUseOnItemExecutiveListener {

    public boolean blockInvUseOnItem(Player player, Item item1, Item item2);

}
