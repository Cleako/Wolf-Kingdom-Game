package com.wk.server.net.rsc.handlers;

import com.wk.server.model.entity.player.Player;
import com.wk.server.net.Packet;
import com.wk.server.net.rsc.PacketHandler;

public class BlinkHandler implements PacketHandler {

	@Override
	public void handlePacket(Packet p, Player player) throws Exception {
		int coordX = p.readShort();
		int coordY = p.readShort();
		if(player.isMod()) 
			player.teleport(coordX, coordY);
	}

}
