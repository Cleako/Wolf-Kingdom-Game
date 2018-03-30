package com.legacy.server.net.rsc.handlers;

import com.legacy.server.model.entity.player.Player;
import com.legacy.server.net.Packet;
import com.legacy.server.net.rsc.ActionSender;
import com.legacy.server.net.rsc.PacketHandler;
import com.legacy.server.plugins.PluginHandler;

public final class Logout implements PacketHandler {

	public void handlePacket(Packet p, Player player) throws Exception {

		if (PluginHandler.getPluginHandler().blockDefaultAction("PlayerLogout",
				new Object[] { player }, false)) {
			ActionSender.sendCantLogout(player);
			return;
		}
		player.unregister(false, "Player logged out");
	}
}
