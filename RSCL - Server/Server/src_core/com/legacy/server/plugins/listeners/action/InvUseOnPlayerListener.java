package com.legacy.server.plugins.listeners.action;

import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.player.Player;

public interface InvUseOnPlayerListener {
	
	public void onInvUseOnPlayer(Player player, Player otherPlayer, Item item);

}
