package com.legacy.server.net.rsc.handlers;

import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.world.World;
import com.legacy.server.model.world.region.TileValue;
import com.legacy.server.net.Packet;
import com.legacy.server.net.rsc.ActionSender;
import com.legacy.server.net.rsc.PacketHandler;
import com.legacy.server.plugins.PluginHandler;

public final class CommandHandler implements PacketHandler {

	public void handlePacket(Packet p, Player player) throws Exception {
		String s = p.readString();
		int firstSpace = s.indexOf(" ");
		String cmd = s;
		String[] args = new String[0];
		if (firstSpace != -1) {
			cmd = s.substring(0, firstSpace).trim();
			args = s.substring(firstSpace + 1).trim().split(" ");
		}
		cmd = cmd.toLowerCase();
		if(cmd.equals("tinfo")) {
			TileValue v = World.getWorld().getTile(player.getLocation());
			player.message("map: " + v.traversalMask + "");
		}
		if(cmd.equals("unloadplugins") && player.isAdmin()) {
			PluginHandler.getPluginHandler().unload();
			player.message("Plugins have been cleared! Type ::reloadplugins for reload.");
		}
		if(cmd.equals("reloadplugins") && player.isAdmin()) {
			try {
				PluginHandler.getPluginHandler().reload();
			} catch (Exception e) {
				player.message("FAILED TO RELOAD!!");
				ActionSender.sendBox(player, e.getMessage(), true);
			}
			player.message("Plugins have been reloaded succesfully!");
		} else 
		PluginHandler.getPluginHandler().handleAction("Command",
				new Object[] { cmd.toLowerCase(), args, player });
	}
}
