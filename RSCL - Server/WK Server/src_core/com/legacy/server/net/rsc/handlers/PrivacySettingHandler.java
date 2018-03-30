package com.legacy.server.net.rsc.handlers;

import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.world.World;
import com.legacy.server.net.Packet;
import com.legacy.server.net.rsc.PacketHandler;

public class PrivacySettingHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Player player) throws Exception {

		boolean[] newSettings = new boolean[4];
		for (int i = 0; i < 4; i++) {
			newSettings[i] = p.readByte() == 1;
		}
		for (int i = 0; i < 4; i++) {
			player.getSettings().setPrivacySetting(i, newSettings[i]);
		}
	}

}
