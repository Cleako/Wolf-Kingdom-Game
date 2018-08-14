package com.wk.server.plugins.listeners.action;

import com.wk.server.model.container.Item;
import com.wk.server.model.entity.GroundItem;
import com.wk.server.model.entity.player.Player;

public interface InvUseOnGroundItemListener {

	public void onInvUseOnGroundItem(Item myItem, GroundItem item, Player player);

}
