package com.legacy.server.net.rsc;

import com.legacy.server.model.entity.player.Player;
import com.legacy.server.net.Packet;
/**
 * 
 * @author n0m
 *
 */
public interface PacketHandler {
	
    public void handlePacket(Packet p, Player player) throws Exception;
}
