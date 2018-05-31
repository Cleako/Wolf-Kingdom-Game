package com.wk.server.plugins.listeners.executive;

import com.wk.server.model.container.Item;
import com.wk.server.model.entity.player.Player;

public interface InvUseOnPlayerExecutiveListener {
	
	public boolean blockInvUseOnPlayer(Player player, Player  otherPlayer, Item item);

}
