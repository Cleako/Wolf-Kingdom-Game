package com.legacy.server.net.rsc.handlers;

import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.world.World;
import com.legacy.server.net.Packet;
import com.legacy.server.net.rsc.ActionSender;
import com.legacy.server.net.rsc.PacketHandler;

public final class GameSettingHandler implements PacketHandler {

	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Player player) throws Exception {

		int idx = (int) p.readByte();
		if (idx < 0 || idx > 11) {
			player.setSuspiciousPlayer(true);
			return;
		}
		
		if(idx >= 4) {
			if(idx == 4) {
				player.getCache().set("setting_android_longpress",  p.readByte());
			} else if(idx == 5) {
				player.getCache().store("setting_showroof",  p.readByte() == 1);
			} else if(idx == 6) {
				player.getCache().store("setting_fogofwar",  p.readByte() == 1);
			} else if(idx == 7) {
				player.getCache().store("setting_android_holdnchoose",  p.readByte() == 1);
			} else if(idx == 8) {
				player.getCache().store("block_tab_messages",  p.readByte() == 1);
			} else if(idx == 9) {
				player.getCache().set("setting_block_global", p.readByte());
			} else if(idx == 10) {
				player.getCache().store("p_xp_notifications_enabled", p.readByte() == 1);
			} else if(idx == 11) {
				player.getCache().store("p_block_invites", p.readByte() == 1);
			}
			return;
		}
		
		boolean on = p.readByte() == 1;
		player.getSettings().setGameSetting(idx, on);
		ActionSender.sendGameSettings(player);
	}
}
