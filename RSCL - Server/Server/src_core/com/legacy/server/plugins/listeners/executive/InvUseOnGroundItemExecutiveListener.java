package com.legacy.server.plugins.listeners.executive;

import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.GroundItem;
import com.legacy.server.model.entity.player.Player;

public interface InvUseOnGroundItemExecutiveListener {

    public boolean blockInvUseOnGroundItem(Item myItem, GroundItem item, Player player);

}
