package com.wk.server.net.rsc.handlers;

import com.wk.server.model.entity.player.Player;
import com.wk.server.net.Packet;
import com.wk.server.net.rsc.ActionSender;
import com.wk.server.net.rsc.PacketHandler;
import com.wk.server.plugins.PluginHandler;

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
