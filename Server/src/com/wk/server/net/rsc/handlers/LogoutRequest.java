package com.wk.server.net.rsc.handlers;

import com.wk.server.model.entity.player.Player;
import com.wk.server.model.world.World;
import com.wk.server.net.Packet;
import com.wk.server.net.rsc.ActionSender;
import com.wk.server.net.rsc.PacketHandler;

public class LogoutRequest implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Player player) throws Exception {
		if (player.canLogout()) {
			player.unregister(false, "Player requested log out");
		} else {
			ActionSender.sendCantLogout(player);
		}
	}
}
