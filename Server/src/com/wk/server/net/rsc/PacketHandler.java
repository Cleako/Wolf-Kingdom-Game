package com.wk.server.net.rsc;

import com.wk.server.model.entity.player.Player;
import com.wk.server.net.Packet;
/**
 * 
 * @author n0m
 *
 */
public interface PacketHandler {
	
    public void handlePacket(Packet p, Player player) throws Exception;
}
