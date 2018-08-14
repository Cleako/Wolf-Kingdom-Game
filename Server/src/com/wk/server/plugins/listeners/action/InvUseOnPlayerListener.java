package com.wk.server.plugins.listeners.action;

import com.wk.server.model.container.Item;
import com.wk.server.model.entity.player.Player;

public interface InvUseOnPlayerListener {
	
	public void onInvUseOnPlayer(Player player, Player otherPlayer, Item item);

}
