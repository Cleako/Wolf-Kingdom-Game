package com.legacy.server.plugins.listeners.executive;

import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.player.Player;

public interface InvUseOnPlayerExecutiveListener {
	
	public boolean blockInvUseOnPlayer(Player player, Player  otherPlayer, Item item);

}
