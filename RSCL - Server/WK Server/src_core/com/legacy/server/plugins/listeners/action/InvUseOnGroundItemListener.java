package com.legacy.server.plugins.listeners.action;

import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.GroundItem;
import com.legacy.server.model.entity.player.Player;

public interface InvUseOnGroundItemListener {

	public void onInvUseOnGroundItem(Item myItem, GroundItem item, Player player);

}
