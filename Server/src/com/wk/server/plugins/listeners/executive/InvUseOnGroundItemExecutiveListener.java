package com.wk.server.plugins.listeners.executive;

import com.wk.server.model.container.Item;
import com.wk.server.model.entity.GroundItem;
import com.wk.server.model.entity.player.Player;

public interface InvUseOnGroundItemExecutiveListener {

    public boolean blockInvUseOnGroundItem(Item myItem, GroundItem item, Player player);

}
